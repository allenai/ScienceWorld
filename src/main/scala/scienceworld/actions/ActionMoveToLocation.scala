package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.objects.portal.Door
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject



/*
 * Action: Move through door
 */
class ActionMoveThroughDoor(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val door = assignments("door")

    // Check that the object is a door
    if (!door.isInstanceOf[Door]) {
      return "The " + door.name + " is not a door."
    }

    // Check that the door is open
    if (!door.propPortal.get.isOpen) {
      return "The " + door.name + " is not open."
    }

    // Move the agent through door
    val connectsTo = door.propPortal.get.connectsTo
    connectsTo.addObject(agent)
    return "You move through the " + door.name + " to the " + connectsTo.name + "."

  }

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