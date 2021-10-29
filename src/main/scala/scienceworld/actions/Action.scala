package scienceworld.actions

import language.model.{ActionExpr, ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject

class Action(val action:ActionRequestDef, val assignments:Map[String, EnvObject]) {
  def name:String = action.name

  // Returns (User error message (if applicable), whether the action could be executed or not)
  def isValidAction():(String, Boolean) = {
    return ("error message", false)
  }

  // Returns: (User message, whether the action was successful or not)
  def runAction():(String, Boolean) = {
    return ("Empty action (" + this.name + ").", false)
  }

}


object Action {
  val MESSAGE_UNKNOWN_CATCH       = "<unknown catch>"
}



class PossibleAction(val sequence:Array[ActionExpr]) {

  def mkHumanReadableStr():String = {
    return sequence.map(_.mkHumanReadableExample()).mkString(" ")
  }

  override def toString():String = {
    this.mkHumanReadableStr()
  }
}