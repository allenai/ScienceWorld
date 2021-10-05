package scienceworld.input

import language.model.{ActionExpr, ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger, ParamSigList}
import language.runtime.runners.{ActionRunner, PredicateRunner}
import language.struct.{DynamicValue, ScopedVariableLUT}
import scienceworld.actions.Action
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.ObjMonitor

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

class InputParser(actionRequestDefs:Array[ActionRequestDef]) {
  val stopWords = Array("a", "the")

  // Get a list of all referents
  def getAllReferents(objTreeRoot:EnvObject):Array[String] = {
    val out = mutable.Set[String]()
    val allObjs = InputParser.collectObjects(objTreeRoot).toArray
    for (obj <- allObjs) {
      out ++= obj.getReferentsWithContainers(perspectiveContainer = objTreeRoot)
    }

    out.toArray.map(_.toLowerCase).sorted
  }

  // Main entry point
  // Perspective container: The container where the agent is located
  def parse(inputStr:String, objTreeRoot:EnvObject, agent:EnvObject, objMonitor:ObjMonitor, perspectiveContainer:EnvObject): (Boolean, String, String, Option[Action]) = {      // (Success, errorMessage, userString)
    // TODO: Only include observable objects in the list of all objects
    val tokens = this.tokenize(inputStr.toLowerCase)
    val allObjs = InputParser.collectObjects(objTreeRoot).toArray

    //println ("inputStr: " + inputStr)

    val matchesOut = mutable.Map[String, Array[InputMatch]]()

    for (actionRequestDef <- actionRequestDefs) {
      //println ("ActionRequestDef: " + actionRequestDef.name)

      // Iterate through all possible triggers for this action
      val matches = new ArrayBuffer[InputMatch]
      for (trigger <- actionRequestDef.triggers) {
        matches.insertAll(matches.length, this.populate(tokens, actionRequestDef.paramSigList, trigger, allObjs, perspectiveContainer) )
      }

      // Populate the actionRequestDef references in the storage class
      for (inputMatch <- matches) inputMatch.actionRequestDef = Some(actionRequestDef)

      // Populate the agent references in the storage class
      for (inputMatch <- matches) inputMatch.varLUT("agent") = agent

      // Store (but, only store for a given actionRequestDef if there are matches)
      if (matches.length > 0) {
        matchesOut(actionRequestDef.name) = matches.toArray
      }

      //println ("Pattern: " + actionRequestDef.name + "\t matches: " + matches.length)
    }


    // Check for (a) single match (good), (b) ambiguous matches, or (c) no matches
    val numMatches = this.countMatches(matchesOut)
    println ("numMatches: " + numMatches)

    // CASE 1: No matches
    if (numMatches == 0) {
      // No matches
      println ("No Matches")
      return (false, "", "No known action matches that input.", None)

    } else if (numMatches > 1) {
      // Ambiguous matches
      println ("Ambiguous matches")
      return (false, "", this.mkAmbiguousMessage(matchesOut), None )

    } else {
      // Exactly one match
      println ("Exactly one match")
      // Step 1: Get one match
      var oneMatch:Option[InputMatch] = None
      for (key <- matchesOut.keySet) oneMatch = Some( matchesOut(key)(0) )

      // Step 2: Create action request
      //## TODO: val (success, errorStr) = actionRunner.setActionRequest(oneMatch.get.actionRequestDef.get, oneMatch.get.varLUT)
      // Convert from InputMatch to Action
      var oneAction:Option[Action] = None
      if (oneMatch.isDefined) oneAction = Some( ActionTypecaster.typecastAction(oneMatch.get, objMonitor) )

      val success = true
      var errorStr = ""

      if (!success) {
        // Error case
        val errStr = "ERROR: InputParser: Unable to post action request for action (" + oneMatch.get.actionRequestDef.get.name + ").\n" + errorStr
        println (errStr)
        return (false, errStr, "Error Encountered", None)
      }
      return (true, "", "Successfully parsed input into single action (" + oneMatch.get.actionRequestDef.get.name + ").", oneAction)
    }
  }

  def countMatches(matches:mutable.Map[String, Array[InputMatch]]) : Int = {
    var numMatches:Int = 0
    for (key <- matches.keys) numMatches += matches(key).size
    return numMatches
  }

  def mkAmbiguousMessage(matches:mutable.Map[String, Array[InputMatch]]): String = {
    val os = new StringBuilder
    if (matches.keySet.size > 1) {
      // CASE: Multiple different actions are matched.
      os.append("Ambiguous request: Multiple different possible actions were matched to this input (")
      os.append( matches.map(_._1).mkString(", ") + "). ")
      os.append("It's possible the action space needs to be refined to remove possible duplicate/ambiguous patterns.")
    } else {
      // CASE: One action, but multiple ways of filling it in
      val slots = new ArrayBuffer[Array[String]]

      // TODO...
      os.append("Ambiguous request: Multiple object matches.  I'm not sure which (X:TODO) you mean... ")
    }

    // Return
    os.toString()
  }

  // Try to populate the slots of a given actionRequestDef with a given set of input tokens
  def populate(tokens:Array[String], paramSigList:ParamSigList, trigger:ActionTrigger, allObjs:Array[EnvObject], perspectiveContainer:EnvObject): Array[InputMatch] = {
    val paramLUT = paramSigList.paramLUT
    var sanitizedStr = tokens.mkString(" ")
    val out = new ArrayBuffer[InputMatch]

    val (success, matches) = this.populateHelper(sanitizedStr, trigger.pattern, allObjs, Array.empty[EnvObject], perspectiveContainer)
    if (success) {
      // Remove duplicates
      val matchesNoDuplicates = this.removeDuplicates(matches)
      /*
      for (i <- 0 until matchesNoDuplicates.length) {
        println ("Match " + i + ": " + matchesNoDuplicates(i).mkString(",").toString)
      }
       */

      // Create an array of matches in an InputMatch storage class
      for (matchedObjs <- matchesNoDuplicates) {
        val varLUT = this.mkVariableLUT(trigger, matchedObjs)
        out.append( new InputMatch(trigger, varLUT) )
      }
    }

    return out.toArray
  }

  // Try to populate the slots of a given actionRequestDef with a given set of input tokens
  def populateHelper(inStr: String, pattern: List[ActionExpr], allObjs: Array[EnvObject], objMatchesSoFar: Array[EnvObject], perspectiveContainer:EnvObject): (Boolean, Array[Array[EnvObject]]) = {
    var sanitizedStr:String = inStr

    // println(" * populateHelper: sanitizedStr: " + sanitizedStr + "  pattern: " + pattern.mkString(", "))
    // println("   * populateHelper: perspectiveContainer: " + perspectiveContainer.toStringMinimal())

    // Check if we're at the end of the trigger pattern
    if (pattern.length == 0) {
      if (inStr.trim.length == 0) {
        // Nothing left to parse in input string -- return match
        return (true, Array(objMatchesSoFar))
      } else {
        // Still input left to parse -- return false
        return (false, Array.empty[Array[EnvObject]])
      }
    }
    var objMatchesSoFar_ = new ArrayBuffer[EnvObject]
    objMatchesSoFar_.insertAll(0, objMatchesSoFar)
    val restOfPattern = pattern.slice(1, pattern.length)

    val out = new ArrayBuffer[Array[EnvObject]]()

    val elem = pattern(0)
    elem match {
      case ActionExprOR(orElements) => {
        val (success, matchStrings) = this.matchLexical(sanitizedStr, orElements)
        if (!success) return (false, Array.empty[Array[EnvObject]])
        for (matchString <- matchStrings) {
          val sanitizedStr = matchString
          val (successRecurse, objMatches) = this.populateHelper(sanitizedStr, restOfPattern, allObjs, objMatchesSoFar_.toArray, perspectiveContainer)
          // If we reach here, success
          for (m <- objMatches) {
            out.append(m)
          }
        }
      }
      case ActionExprIdentifier(name) => {
        val (success, matchTuples) = this.matchObj(sanitizedStr, allObjs, perspectiveContainer)
        if (!success) return (false, Array.empty[Array[EnvObject]])
        for (tupleMatch <- matchTuples) {
          val sanitizedStr = tupleMatch._1
          val objMatchPartial = objMatchesSoFar_.toArray ++ Array(tupleMatch._2)
          val (successRecurse, objMatches) = this.populateHelper(sanitizedStr, restOfPattern, allObjs, objMatchPartial, perspectiveContainer)
          // If we reach here, success
          for (m <- objMatches) {
            out.append( m )
          }
        }

      }
    }

    /*
    println ("  * populateHelper: out = ")
    for (elem <- out) {
      println (elem.map(_.toStringMinimal()).mkString(", "))
    }
     */

    if (out.length == 0) return (false, out.toArray)
    return (true, out.toArray)
  }


  // See if the start of a given string matches with a list of lexical possibilities.
  // e.g. inStr = "this is a test", possibilities=("a", "the", "this") would return (true, "is a test").
  def matchLexical(inStr:String, possibilities:List[String]):(Boolean, Array[String]) = {
    val out = new ArrayBuffer[String]()
    for (possibleStr <- possibilities) {
      if (inStr.startsWith(possibleStr)) {
        // Match -- consume possibility and return
        val outStr = inStr.substring( possibleStr.length ).trim()
        out.append(outStr)
      }
    }
    if (out.length == 0) return (false, Array.empty[String])
    // Return
    return (true, out.toArray)
  }

  // See if the start of a given string matches with a list of possible referents for objects
  def matchObj(inStr: String, allObjs: Array[EnvObject], perspectiveContainer:EnvObject): (Boolean, Array[(String, EnvObject)]) = {
    val out = new ArrayBuffer[(String, EnvObject)]

    for (obj <- allObjs) {
      val referents = InputParser.getObjectReferents(obj, perspectiveContainer)
      // println ("\t matchObj: " + referents.mkString(", "))
      for (referent <- referents) {
        if (inStr.startsWith(referent)) {
          val restOfStr = inStr.substring(referent.length).trim()
          out.append((restOfStr, obj))
        }
      }
    }
    // Return
    if (out.length == 0) return (false, out.toArray)
    return (true, out.toArray)
  }

  /*
   * Helper functions
   */
  def tokenize(inputStr:String):Array[String] = {
    val out = new ArrayBuffer[String]
    val tokens = inputStr.trim.replaceAll("\\s+", " ").split(" ")
    for (token <- tokens) {
      if (!stopWords.contains(token)) {
        out.append(token)
      }
    }
    // Return
    out.toArray
  }

  // Removes duplicate matches
  def removeDuplicates(in:Array[Array[EnvObject]]):Array[Array[EnvObject]] = {
    val out = new ArrayBuffer[ Array[EnvObject] ]()
    for (setIn <- in) {
      breakable {
        for (setOut <- out) {
          if (setIn.deep == setOut.deep) break      // Deep array comparison, to check for duplicates (if this ends up being too slow, refactor)
        }
        out.append(setIn)
      }
    }
    // Return
    out.toArray
  }

  // Populate a variable LUT based on the trigger variable identifiers and a parallel array of matched objects
  def mkVariableLUT(trigger:ActionTrigger, matchedObjs:Array[EnvObject]): mutable.Map[String, EnvObject] = {
    // Step 1: Assemble a list of the variables in the trigger pattern
    val triggerElems = new ArrayBuffer[ActionExprIdentifier]
    for (patternElem <- trigger.pattern) {
      patternElem match {
        case x:ActionExprIdentifier => triggerElems.append(x)
        case _ => { // Do nothing
        }
      }
    }

    // Step 2: Do assignments
    if (triggerElems.length != matchedObjs.length) throw new RuntimeException("ERROR: Number of trigger identifiers (" + triggerElems.mkString(", ") + ") is not the same as the number of matched objects (" + matchedObjs.length + ")")
    val outLUT = mutable.Map[String, EnvObject]()
    for (i <- 0 until triggerElems.length) {
      val triggerElem = triggerElems(i)
      val matchedObj = matchedObjs(i)
      outLUT(triggerElem.identifier) = matchedObj
    }

    // Return
    outLUT
  }

  /*
   * More helper functions
   */



}


object InputParser {

  // DEBUG: Get a list of all possible valid referents, for debugging
  def getPossibleReferents(objTreeRoot:EnvObject, perspectiveContainer:EnvObject):Array[String] = {
    val out = new ArrayBuffer[String]()

    val allObjs = collectObjects(objTreeRoot).toArray

    for (obj <- allObjs) {
      out.insertAll(out.size, getObjectReferents(obj, perspectiveContainer) )
    }

    out.toArray
  }

  def getObjectReferents(obj:EnvObject, perspectiveContainer:EnvObject):Array[String] = {
    return obj.getReferentsWithContainers(perspectiveContainer).map(_.toLowerCase).toArray
  }


  /*
   * Helper functions (collecting objects from the object tree into more easily traversed data structures)
   */

  // Collect all objects in the object tree into a flat set
  def collectObjects(objectTreeRoot:EnvObject):mutable.Set[EnvObject] = {
    val out = mutable.Set[EnvObject]()

    // Step 1: Add this object
    if (!out.contains(objectTreeRoot)) out.add(objectTreeRoot)

    // Step 2: Add children recursively
    for (obj <- objectTreeRoot.getContainedObjectsAndPortals()) {
      if (!out.contains(obj)) {
        out ++= this.collectObjects(obj)
      }
    }

    // Return
    out
  }

  /*
  // Collect all objects in the object tree, arranged by type (stored in a hashmap)
  def collectObjectsByType(objectTreeRoot:EnvObject, taxonomy:Taxonomy):Map[String, Set[EnvObject]] = {
    val out = mutable.Map[String, Set[EnvObject]]()

    val allObjects = this.collectObjects(objectTreeRoot)
    for (obj <- allObjects) {
      val typeKeyBase = obj.getType()   // Get the type the object is defined as

      // Get any hypernyms that base type might have
      val (success, errorStrs, hyponyms) = taxonomy.getHypernyms(typeKeyBase)
      var typeKeys = Array[String](typeKeyBase)
      if (success) typeKeys = hyponyms // The object does exist in the taxonomy

      // For each inherited type, add it to the LUT
      for (typeKey <- typeKeys) {
        if (!out.contains(typeKey)) out(typeKey) = Set[EnvObject]()
        out(typeKey) += obj
      }
    }

    return out.toMap
  }
   */

}

// Storage class
class InputMatch(val actionTrigger:ActionTrigger, val varLUT:mutable.Map[String, EnvObject], var actionRequestDef:Option[ActionRequestDef] = None) {

  override def toString():String = {
    val os = new StringBuilder
    os.append("InputMatch(")
    os.append("ActionTrigger: " + actionTrigger + ", ")
    os.append("varLUT: " + varLUT.toString() )
    os.append(")")
    os.toString
  }
}