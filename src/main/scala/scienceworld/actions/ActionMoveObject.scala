package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.objects.portal.Door
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject

import scala.collection.mutable


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

    // Check that the object is not a liquid or a gas (which can't be directly held by the agent)
    if (objToMove.propMaterial.isDefined) {
      if (objToMove.propMaterial.get.stateOfMatter == "liquid") {
        return "You can't pick up a liquid directly.  Try pouring it from one container to another, or dunking empty containers into containers filled with the liquid."
      } else if (objToMove.propMaterial.get.stateOfMatter == "gas") {
        return "You can't pick up a gas directly. "
      }
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
    // TODO: Also disconnect the object, if it's electrically connected
    val os = new StringBuilder
    if (objToMove.isElectricallyConnected()) {
      os.append("(disconnecting " + objToMove.name + ")")
      objToMove.disconnectElectricalTerminals()
    }
    os.append("You move the " + objToMove.name + " to the " + container.name + ".")

    return os.toString()
  }

}

object ActionMoveObject {
  val ACTION_NAME = "move object"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_MOVEOBJECT

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("move", "put")),
      new ActionExprIdentifier("obj"),
      new ActionExprOR(List("to", "in", "into", "on")),
      new ActionExprIdentifier("moveTo")
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)

  }

}

/*
 * Action: Pick up object and place it in inventory
 */
object ActionPickUpObjectIntoInventory {
  val ACTION_NAME = "pick up"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_PICKUP

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("pick up", "get", "take")),
      new ActionExprIdentifier("obj"),
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)

  }

  def remap(assignments:Map[String, EnvObject], agent:Agent):Map[String, EnvObject] = {
    val out = mutable.Map[String, EnvObject]()
    // Copy existing map
    for (key <- assignments.keySet) out(key) = assignments(key)
    // Add new keys
    out("moveTo") = agent.getInventoryContainer()

    out.toMap
  }

}

/*
 * Action: Put down object and place it in agent's current container
 */
object ActionPutDownObjectIntoInventory {
  val ACTION_NAME = "put down"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_PUTDOWN

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("put down", "drop")),
      new ActionExprIdentifier("obj"),
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)

  }

  def remap(assignments:Map[String, EnvObject], agent:Agent):Map[String, EnvObject] = {
    val out = mutable.Map[String, EnvObject]()
    // Copy existing map
    for (key <- assignments.keySet) out(key) = assignments(key)
    // Add new keys
    if (agent.getContainer().isEmpty) {
      println ("ERROR: Agent should always be in a container. Defaulting to returning to inventory.")
      out("moveTo") = agent.getInventoryContainer()
    } else {
      out("moveTo") = agent.getContainer().get
    }

    out.toMap
  }

}



/*
 * Action: Pour (liquid, or contents of containers)
 */
class ActionPourObject(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val objToMove = assignments("obj")
    val container = assignments("moveTo")

    var pouringOutContainer:Boolean = false
    // Check to see if we're pouring out a container
    if ((objToMove.propContainer.isDefined) && (objToMove.propContainer.get.isContainer)) pouringOutContainer = true

    // Check that the object is moveable
    if ((objToMove.propMoveable.isEmpty) || (!objToMove.propMoveable.get.isMovable)) {
      return "The " + objToMove.name + " is not moveable."
    }
    // If we're pouring out something in a container, check that the container is movable too
    if (!pouringOutContainer) {
      val curContainer = objToMove.getContainer()
      if (curContainer.isEmpty) return "The " + objToMove.name + " can't be poured out because it doesn't appear to be in a container."     // ERROR

      if ((curContainer.get.propMoveable.isEmpty) || (!curContainer.get.propMoveable.get.isMovable)) {
        return "The " + curContainer.get.name + " the " + objToMove.name + " is in is not movable."
      }
    }


    if (pouringOutContainer) {
      if (!objToMove.propContainer.get.isOpen) {
        return "The " + objToMove.name + " can't be poured out, because it's not open."
      }
    } else  {
      // Check that the object is not a liquid or a gas (which can't be directly held by the agent)
      if (objToMove.propMaterial.isDefined) {
        if (objToMove.propMaterial.get.stateOfMatter == "solid") {
          return "I'm not sure how to pour a solid. "
        } else if (objToMove.propMaterial.get.stateOfMatter == "gas") {
          return "You can't pour a gas directly. "
        }
      }
    }

    // Check that if the container is a proper container, that it's open
    if (container.propContainer.isEmpty) {
      return "That can't be moved there, because it's not a container."
    }

    if (!container.propContainer.get.isOpen) {
      return "That can't be moved there, because the " + container.name + " isn't open."
    }

    if (pouringOutContainer) {
      if (objToMove.getContainedObjects().size == 0) {
        return "The " + objToMove.name + " is empty, so there's nothing to pour."
      }
    }

    // Do the pouring
    val os = new StringBuilder

    if (pouringOutContainer) {
      // Pour all objects in the old container into the new container
      for (obj <- objToMove.getContainedObjects()) {
        if ((obj.propMoveable.isDefined) && (obj.propMoveable.get.isMovable)) {
          // Move all movable objects
          container.addObject(obj)
          // Electrically disconnect, if connected
          if (obj.isElectricallyConnected()) {
            os.append("(disconnecting " + obj.name + ")\n")
            obj.disconnectElectricalTerminals()
          }
        }
      }
      os.append("You pour the contents of the " + objToMove.name + " into the " + container.name + ".")
      return os.toString()

    } else {
      container.addObject(objToMove)
      if (objToMove.isElectricallyConnected()) {
        // Electrically disconnect, if connected
        os.append("(disconnecting " + objToMove.name + ")\n")
        objToMove.disconnectElectricalTerminals()
      }
      os.append("You pour the " + objToMove.name + " into the " + container.name + ".")
      return os.toString()

    }

  }

}

object ActionPourObject {
  val ACTION_NAME = "pour object"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_POUROBJECT

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("pour")),
      new ActionExprIdentifier("obj"),
      new ActionExprOR(List("in", "into", "on")),
      new ActionExprIdentifier("moveTo")
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)

  }

}


//## TODO: Dunk container in liquid?