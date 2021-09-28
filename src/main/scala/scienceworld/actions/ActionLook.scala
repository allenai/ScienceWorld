package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject

/*
 * Action: Look Around
 */
class ActionLookAround(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

}

object ActionLookAround {
  val ACTION_NAME = "look around"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("look", "look around"))
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}