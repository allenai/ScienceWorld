package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject

/*
 * Action: Open Door
 */
class ActionOpenDoor(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

}

object ActionOpenDoor {
  val ACTION_NAME = "open door"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("open")),
      new ActionExprIdentifier("door")
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}


/*
 * Action: Close Door
 */
class ActionCloseDoor(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

}

object ActionCloseDoor {
  val ACTION_NAME = "close door"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("close")),
      new ActionExprIdentifier("door")
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}