package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject

/*
 * Action: Activate
 */
class ActionActivate(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

}

object ActionActivate {
  val ACTION_NAME = "activate"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("activate", "turn on")),
      new ActionExprIdentifier("device")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}


/*
 * Action: Deactivate
 */
class ActionDeactivate(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

}

object ActionDeactivate {
  val ACTION_NAME = "deactivate"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("deactivate", "turn off")),
      new ActionExprIdentifier("device")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}