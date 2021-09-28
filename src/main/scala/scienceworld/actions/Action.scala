package scienceworld.actions

import language.model.{ActionExpr, ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject

class Action(val action:ActionRequestDef, val assignments:Map[String, EnvObject]) {
  def name:String = action.name

  def runAction():String = {
    return "Empty action (" + this.name + ")."
  }

}


