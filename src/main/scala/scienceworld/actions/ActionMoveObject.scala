package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject

/*
 * Action: Move Object
 */
class ActionMoveObject(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

}

object ActionMoveObject {
  val ACTION_NAME = "move object"

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("move")),
      new ActionExprIdentifier("obj"),
      new ActionExprOR(List("to")),
      new ActionExprIdentifier("moveTo")
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)

  }

}