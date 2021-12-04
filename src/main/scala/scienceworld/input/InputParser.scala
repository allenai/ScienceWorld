package scienceworld.input

import language.model.{ActionExpr, ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger, ParamSigList}
import language.runtime.runners.{ActionRunner, PredicateRunner}
import language.struct.{DynamicValue, ScopedVariableLUT}
import scienceworld.actions.Action
import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{GoalSequence, ObjMonitor}

import scala.Console.out
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}

class InputParser(actionRequestDefs:Array[ActionRequestDef]) {
  var lastAmbiguousMatches:Option[Array[InputMatch]] = None

  // Get a list of all referents
  def getAllReferents(objTreeRoot:EnvObject, includeHidden:Boolean):Array[String] = {
    val out = mutable.Set[String]()
    val allObjs = InputParser.collectAccessibleObjects(objTreeRoot, includeHidden).toArray
    for (obj <- allObjs) {
      out ++= obj.getReferentsWithContainers(perspectiveContainer = objTreeRoot)
    }

    out.toArray.map(_.toLowerCase).sorted
  }

  // Get a list of all referents
  def getAllUniqueReferents(objTreeRoot:EnvObject, includeHidden:Boolean):Array[(String, EnvObject)] = {
    // Step 1: Collect a list of all referents for each object
    val objReferents = new ArrayBuffer[Array[String]]()
    val allObjs = InputParser.collectAccessibleObjects(objTreeRoot, includeHidden).toArray
    for (obj <- allObjs) {
      objReferents.append( obj.getReferentsWithContainers(perspectiveContainer = objTreeRoot).toArray.sorted )
    }

    // Step 2: Choose unique referents (find their indices)
    val indices = this.chooseUniqueReferents(objReferents.toArray)

    // Step 2A: Populate an array of the unique referents (as strings)
    val out = new ArrayBuffer[(String, EnvObject)]()
    for (i <- 0 until allObjs.length) {
      val referent = objReferents(i)(indices(i))
      out.append( (referent.toLowerCase(), allObjs(i)) )
    }

    // Return
    out.toArray.sortBy(_._1)
  }

  def getAllUniqueReferentsLUT(objTreeRoot:EnvObject, includeHidden:Boolean):Map[Long, String] = {
    val out = mutable.Map[Long, String]()

    val tuples = this.getAllUniqueReferents(objTreeRoot, includeHidden)
    for (tuple <- tuples) {
      val referent = tuple._1
      val obj = tuple._2
      val uuid = obj.uuid

      out(uuid) = referent
    }

    return out.toMap
  }

  private def chooseUniqueReferents(objReferents:Array[Array[String]]): Array[Int] = {
    // Array of referent indicies
    val indices = Array.fill[Int](objReferents.length)(0)
    var numAttempts:Int = 0
    val MAX_ATTEMPTS = 20

    while (numAttempts < MAX_ATTEMPTS) {
      // Get a frequency counter of chosen referents
      val frequency = new mutable.HashMap[String, Int]()
      for (i <- 0 until objReferents.length) {
        val referent = objReferents(i)(indices(i))
        if (frequency.contains(referent)) {
          frequency(referent) = frequency(referent) + 1
        } else {
          frequency(referent) = 1
        }
      }

      // Check to see if they're unique
      var numDuplicates: Int = 0
      for (i <- 0 until objReferents.length) {
        val referent = objReferents(i)(indices(i))
        if (frequency(referent) > 1) {
          // If there's a duplicate, then increment the array for both elements
          if (numAttempts < 10) {
            // For the first 10 attempts, just increment the index, which should go to further specifications of the object names (e.g. 'cat', 'cat in the box')
            indices(i) = (indices(i) + 1) % objReferents(i).length
          } else {
            // For subsequent attempts, pick random referent IDs
            indices(i) = Random.nextInt( objReferents(i).length )
          }
          numDuplicates += 1
        }
      }

      // If no duplicates, exit
      if (numDuplicates == 0) {
        return indices
      }

      numAttempts += 1
    }

    // If we reach here, then there was an error creating unique referents.  Just pick the ones we have so far.
    println ("WARNING: Unable to create unique referents. ")
    return indices
  }

  // Main entry point
  // Perspective container: The container where the agent is located
  def parse(inputStr:String, objTreeRoot:EnvObject, agent:Agent, objMonitor:ObjMonitor, goalSequence:GoalSequence, perspectiveContainer:EnvObject): (Boolean, String, String, Option[Action]) = {      // (Success, errorMessage, userString)
    // TODO: Only include observable objects in the list of all objects
    val tokens = InputParser.tokenize(inputStr.toLowerCase)
    val allObjs = (InputParser.collectAccessibleObjects(objTreeRoot, includeHidden = true) ++ InputParser.collectAccessibleObjects(agent, includeHidden = true)).toArray

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
      val (ambiguousStr, allAmbiguousMatches) = this.mkAmbiguousMessage(matchesOut, agent)
      lastAmbiguousMatches = Some(allAmbiguousMatches)      // Store ambiguous matches, to allow resolution in next step
      return (false, "", ambiguousStr, None )               // Return the ambiguity resolution string

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
      if (oneMatch.isDefined) oneAction = Some( ActionTypecaster.typecastAction(oneMatch.get, objMonitor, goalSequence, agent) )

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

  def mkAmbiguousMessage(matches: mutable.Map[String, Array[InputMatch]], agent:Agent): (String, Array[InputMatch]) = {
    val os = new StringBuilder
    if (matches.keySet.size > 1) {
      // CASE: Multiple different actions are matched.
      os.append("Ambiguous request: Multiple different possible actions were matched to this input (")
      os.append(matches.map(_._1).mkString(", ") + "). ")
      os.append("It's possible the action space needs to be refined to remove possible duplicate/ambiguous patterns.")
    }

    os.append("Ambiguous request: Please enter the number for the action you intended (or blank to cancel):\n")

    // Collect all possible ambiguous actions
    val allAmbiguousActions = new ArrayBuffer[InputMatch]
    for (matchSet <- matches) {
      allAmbiguousActions.insertAll(allAmbiguousActions.length, matchSet._2)
    }

    // Create a string that displays the options
    val agentContainer = agent.getContainer()
    for (i <- 0 until allAmbiguousActions.length) {
      os.append(i + ":\t" + allAmbiguousActions(i).mkHumanReadableClarification(agentContainer.getOrElse(new EnvObject())) + "\n")
    }

    // Return
    (os.toString(), allAmbiguousActions.toArray)
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
      //println ("\t matchObj: " + referents.mkString(", "))
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
   * Ambiguity resolution
   */
  def isInAmbiguousState():Boolean = {
    if (this.lastAmbiguousMatches.isDefined) return true
    // Otherwise
    return false
  }

  def clearAmbiguousState(): Unit = {
    this.lastAmbiguousMatches = None
  }

  def resolveAmbiguity(inputStr:String, agent:Agent, objMonitor:ObjMonitor, goalSequence:GoalSequence):(String, Option[Action]) = {
    // Checks
    if (!this.isInAmbiguousState()) {
      val errorStr = "ERROR: Not in ambiguous state."
      return (errorStr, None)
    }

    // Step 1: Convert input to integer
    var ambiguityIdx:Int = -1
    try {
      ambiguityIdx = inputStr.toInt
    } catch {
      case _:Throwable => {
        val errorStr = "ERROR: Unknown response (" + inputStr + ").  Action cancelled."
        this.clearAmbiguousState()
        return (errorStr, None)
      }
    }

    // Check in range
    if ((ambiguityIdx < 0) || (ambiguityIdx >= this.lastAmbiguousMatches.get.length)) {
      val errorStr = "ERROR: Value out of range (" + inputStr + ").  Action cancelled."
      this.clearAmbiguousState()
      return (errorStr, None)
    }

    // Step 2: Resolve
    val action = this.lastAmbiguousMatches.get(ambiguityIdx)
    // Convert from InputMatch to Action
    val oneAction = Some( ActionTypecaster.typecastAction(action, objMonitor, goalSequence, agent) )

    this.clearAmbiguousState()
    return ("", oneAction)
  }


  /*
   * More helper functions
   */



}


object InputParser {
  val stopWords = Array("a", "an", "the")

  // DEBUG: Get a list of all possible valid referents, for debugging
  def getPossibleReferents(objTreeRoot:EnvObject, perspectiveContainer:EnvObject):Array[String] = {
    val out = new ArrayBuffer[String]()

    val allObjs = collectAccessibleObjects(objTreeRoot).toArray

    for (obj <- allObjs) {
      out.insertAll(out.size, getObjectReferents(obj, perspectiveContainer) )
    }

    out.toArray
  }

  def getObjectReferents(obj:EnvObject, perspectiveContainer:EnvObject):Array[String] = {
    val referents = obj.getReferentsWithContainers(perspectiveContainer).map(_.toLowerCase).toArray

    val out = new ArrayBuffer[String]
    for (referent <- referents) {
      val filtered = tokenize(referent).mkString(" ")     // Tokenize, remove stop words, etc.
      out.append(filtered)
    }

    out.toArray
  }

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


  /*
   * Helper functions (collecting objects from the object tree into more easily traversed data structures)
   */

  // Collect all objects in the object tree into a flat set.
  // NOTE: This function does not respect container boundaries (e.g. whether a container is open/closed), and simply gets every object that's contained regardless of accessibility.
  def collectObjects(objectTreeRoot:EnvObject, includeHidden:Boolean = false):mutable.Set[EnvObject] = {
    val out = mutable.Set[EnvObject]()

    // Step 1: Add this object
    if (!out.contains(objectTreeRoot)) {
      if (includeHidden || !objectTreeRoot.isHidden()) {
        out.add(objectTreeRoot)
      }
    }

    // Step 2A: Also add the destination locations of any portals
    for (portal <- objectTreeRoot.getPortals()) {
      val destination = portal.getConnectsTo(perspectiveContainer = objectTreeRoot)
      if (destination.isDefined) {
        out.add(destination.get)
      }
    }

    // Step 2: Add children recursively
    if (includeHidden || !objectTreeRoot.isHidden()) {
      for (obj <- objectTreeRoot.getContainedObjectsAndPortals()) {
        out ++= this.collectObjects(obj, includeHidden)
      }
    }

    // Return
    out
  }

  def collectAccessibleObjects(objectTreeRoot:EnvObject, includeHidden:Boolean = false):mutable.Set[EnvObject] = {
    val out = mutable.Set[EnvObject]()

    // Step 1: Add this object
    if (!out.contains(objectTreeRoot)) {
      if (includeHidden || !objectTreeRoot.isHidden()) {
        out.add(objectTreeRoot)
      }
    }

    // Step 2: Also add the destination locations of any portals
    for (portal <- objectTreeRoot.getPortals()) {
      val destination = portal.getConnectsTo(perspectiveContainer = objectTreeRoot)
      if (destination.isDefined) {
        out.add(destination.get)
      }
    }

    // Step 3: Add children recursively
    if (includeHidden || !objectTreeRoot.isHidden()) {
      out ++= objectTreeRoot.getContainedAccessibleObjects(includeHidden)
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

  def mkHumanReadableClarification(agentContainer:EnvObject): String = {
    return actionTrigger.mkHumanReadableInstance(varLUT, agentContainer)
  }

  override def toString():String = {
    val os = new StringBuilder
    os.append("InputMatch(")
    os.append("ActionTrigger: " + actionTrigger + ", ")
    os.append("varLUT: " + varLUT.toString() )
    os.append(")")
    os.toString
  }
}