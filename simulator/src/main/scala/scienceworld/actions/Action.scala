package scienceworld.actions

import language.model.{ActionExpr, ActionExprIdentifier, ActionExprOR, ActionExprObject, ActionRequestDef, ActionTrigger}
import main.scala.util.CombinationIterator
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.runtime.pythonapi.TemplateAction
import scienceworld.struct.EnvObject

import scala.collection.mutable.ArrayBuffer

class Action(val action:ActionRequestDef, val assignments:Map[String, EnvObject]) {
  def name:String = action.name

  // Returns (User error message (if applicable), whether the action could be executed or not)
  def isValidAction():(String, Boolean) = {
    throw new RuntimeException("ERROR: Action.isValidAction() base class method called.")
    return ("error message", false)
  }

  // Returns: (User message, whether the action was successful or not)
  def runAction():(String, Boolean) = {
    throw new RuntimeException("ERROR: Action.runAction() base class method called.")
    return ("Empty action (" + this.name + ").", false)
  }

}


object Action {
  val MESSAGE_UNKNOWN_CATCH       = "<unknown catch>"
}



class PossibleAction(val sequence:Array[ActionExpr], templateID:Int) {

  def mkHumanReadableStr():String = {
    return sequence.map(_.mkHumanReadableExample()).mkString(" ")
  }

  def mkAllPossibleHumanReadableStr(perspectiveContainer:EnvObject):Array[String] = {
    val out = new ArrayBuffer[String]

    // Get the possibilities for each sequence element
    val possibilities = new Array[Array[String]](sequence.length)
    val numPossibilities = Array.fill[Int](sequence.length)(0)
    for (i <- 0 until sequence.length) {
      possibilities(i) = sequence(i).mkAllHumanReadableExamples(perspectiveContainer: EnvObject)
      numPossibilities(i) = possibilities(i).length
    }

    // Then, enumerate all
    val iter = new CombinationIterator(numPossibilities)
    while (iter.hasNext()) {
      val combination = iter.next()
      val assembledStr = new StringBuilder

      for (i <- 0 until sequence.length) {
        assembledStr.append( possibilities(i)(combination(i)) + " ")
      }

      out.append( assembledStr.toString().trim )
    }

    /*
    // DEBUG
    if ((out.length > 0) && (out(0).startsWith("activate") || out(0).startsWith("turn on"))) {
      println("!!!!! ")
      println ("Possibilities length: " + possibilities.length)
      for (i <- 0 until possibilities.length) {
        println(i + "\t" + sequence(i).getClass.toString + "\t" + possibilities(i).mkString(", "))
      }
      println(out.mkString(", "))
    }
     */

    // Convert to set (to get unique set)
    return out.toSet.toArray
  }

  // Export into a TemplateAction storage class
  def toTemplate(perspectiveContainer:EnvObject):Array[TemplateAction] = {
    // Step 1: Get sanitized plain-text string
    //val sanitizedStr = this.mkHumanReadableStr()              // OLD: Single string
    val sanitizedStrs = this.mkAllPossibleHumanReadableStr(perspectiveContainer)  // NEW: All possible strings

    // Step 2: Collect objects
    val objects = new ArrayBuffer[EnvObject]
    for (elem <- this.sequence) {

      elem match {
        case e:ActionExprObject => {
          objects.append( e.obj )
        }
        case _ => {
          // Ignore
        }
      }

    }

    // Step 3: Get object IDs
    val objectIDs = objects.map(_.uuid.toInt).toList
    val objectTypes = objects.map(_.typeID.toInt).toList

    // Return
    val out = new ArrayBuffer[TemplateAction]()
    //println("toTemplate: " + sanitizedStrs.mkString(", "))
    for (sanitizedStr <- sanitizedStrs) {
      out.append( new TemplateAction(sanitizedStr, templateID, objectIDs, objectTypes) )
    }
    return out.toArray
  }


  override def toString():String = {
    this.mkHumanReadableStr()
  }
}
