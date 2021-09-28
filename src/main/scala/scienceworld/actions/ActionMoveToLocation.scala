package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject



/*
 * Action: Move through door
 */
class ActionMoveThroughDoor(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

}

object ActionMoveThroughDoor {
  val ACTION_NAME = "move through door"

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move through door
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("go through", "walk through", "move through", "go to", "walk to", "move to")),
      new ActionExprIdentifier("door")
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}