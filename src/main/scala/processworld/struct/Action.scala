package processworld.struct

import scala.collection.mutable.ArrayBuffer
import Requirement._
import Result._


class Action(val name:String, val isAutomatic:Boolean = false, val callback:ActionInfo => Boolean) {


  // Do the action (by calling the callback)
  def doAction(actionInfo:ActionInfo): Boolean = {
    return callback(actionInfo)
  }


  /*
val requirements = new ArrayBuffer[Requirement]()
val result = new ArrayBuffer[Result]()


// Requirements
def addRequirement(req:Requirement): Unit = {
  requirements.append(req)
}

// Check that *all* requirements are met
def checkRequirements(obj:Object): Boolean = {
  for (requirement <- requirements) {
    if (!checkRequirementMet(requirement, obj)) return false
  }
  // If we reach here, all the requirements are met (or, there are no requirements)
  true
}

// Check that a single requirement is met
def checkRequirementMet(req:Requirement, obj:Object): Boolean = {

  val success = req.relation match {
    case REQ_CONTAINSOBJ => {
      // Contains object
      if (obj.containsObjName(req.argThing)) return true
    }
    case REQ_HASPROP => {
      // Has property
      if (obj.hasVarStr(req.argThing)) return true
      if (obj.hasVarCategorical(req.argThing)) return true
      if (obj.hasVarDouble(req.argThing)) return true
    }
    case REQ_HASPROPVALUE => {
      // Has property equal to a specified value
      if (obj.getVarStr(req.argThing, "") == req.argValue) return true
      if (obj.getVarCategorical(req.argThing, "") == req.argValue) return true
      if (obj.getVarDouble(req.argThing, 0.0) == req.argValue) return true
    }

    case _ => println("Unknown requirement relation: " + req.relation)
  }

  // Default return -- requirement not met
  false
}
*/




  /*
   * String
   */
  override def toString():String = {
    val os = new StringBuilder

    os.append(name + " (auto = " + isAutomatic + ")")
    /*
    os.append("\tRequirements: \n")
    for (i <- 0 until requirements.length) {
      os.append("\t\t" + i + ": " + requirements(i).toString() + "\n")
    }
     */

    // Return
    os.toString()
  }

}



// Storage class for a requirement (pre-condition)
class Requirement(val relation:String, val argThing:String, val argValue:String) {

  /*
   * String
   */
  override def toString():String = {
    val os = new StringBuilder

    os.append("Requirement (" + relation + ", " + argThing + ", " + argValue + ")")

    // Return
    os.toString()
  }
}

object Requirement {
  val REQ_CONTAINSOBJ        = "contains_obj"
  val REQ_HASPROP            = "has_property"
  val REQ_HASPROPVALUE       = "has_property_value"

}


// Storage class for a result (post-condition)
class Result(val relation:String, val argThing:String, val argValue:String) {

  /*
   * String
   */
  override def toString():String = {
    val os = new StringBuilder

    os.append("Result (" + relation + ", " + argThing + ", " + argValue + ")")

    // Return
    os.toString()
  }
}

object Result {
  val RES_CREATEOBJ         = "create_obj"
  val RES_DESTROYOBJ        = "destroy_obj"
  val RES_SETPROPERTY       = "set_property"
  // Others? Increment/decrement, set specific property (e.g. str/categorical/double).  Get reference to created objects so that properties can be set for them?
  // Handle expressions?  e.g. temperature = the temperature of one of the input objects plus some equation?
}



// Storage class for passing information to an action
class ActionInfo {

}

class ActionInfoParameterless() extends ActionInfo {
  // No parameters required for this action (e.g. turn off/turn on, open/close container, etc)
}

class ActionInfoMove(val agent:Object, val newContainer:Object) extends ActionInfo {
  // Agent: Agent doing the moving
  // New Container: Where the object will be moved to
}

class ActionInfoUse(val agent:Object, val patientObject:Object) extends ActionInfo {
  // Agent: The agent doing the using
  // Patient object: The object that the tool will be used on (e.g. an apple, if the tool is a knife, to slice it).
}

