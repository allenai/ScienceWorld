package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.objects.portal.Door
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject

/*
 * Action: Move Object
 */
class ActionMoveObject(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val objToMove = assignments("obj")
    val container = assignments("moveTo")

    // Check that the object is moveable
    if ((objToMove.propMoveable.isEmpty) || (!objToMove.propMoveable.get.isMovable)) {
      return "The " + objToMove.name + " is not moveable."
    }

    // Check that if the container is a proper container, that it's open
    if (container.propContainer.isEmpty) {
      return "That can't be moved there."
    }

    if (!container.propContainer.get.isOpen) {
      return "That can't be moved there, because the " + container.name + " isn't open."
    }

    // Move the agent through door
    container.addObject(objToMove)
    return "You move the " + objToMove.name + " to the " + container.name + "."

  }

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