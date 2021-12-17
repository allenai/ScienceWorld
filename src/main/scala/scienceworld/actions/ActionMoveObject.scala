package scienceworld.actions

import language.model.{ActionExpr, ActionExprIdentifier, ActionExprOR, ActionExprObject, ActionExprText, ActionRequestDef, ActionTrigger}
import scienceworld.actions.ActionPickUpObjectIntoInventory.remap
import scienceworld.actions.ActionPutDownObjectIntoInventory.remap
import scienceworld.objects.portal.Door
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/*
 * Action: Move Object
 */
class ActionMoveObject(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")
    val objToMove = assignments("obj")
    val container = assignments("moveTo")

    // Do checks for valid action
    val (invalidStr, isValid) = ActionMoveObject.isValidAction(assignments)
    if (!isValid) return (invalidStr, false)

    // Move the object
    container.addObject(objToMove)
    // TODO: Also disconnect the object, if it's electrically connected
    val os = new StringBuilder
    if (objToMove.isElectricallyConnected()) {
      os.append("(disconnecting " + objToMove.name + ")")
      objToMove.disconnectElectricalTerminals()
    }
    os.append("You move the " + objToMove.name + " to the " + container.name + ".")

    return (os.toString(), true)
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


  def isValidAction(assignments:Map[String, EnvObject]): (String, Boolean) = {
    val agent = assignments("agent")
    val objToMove = assignments("obj")
    val container = assignments("moveTo")

    // Check 1: Check that agent is valid
    agent match {
      case a:Agent => { }
      case _ => return ("I'm not sure what that means", false)
    }

    // Check that the object is moveable
    if ((objToMove.propMoveable.isEmpty) || (!objToMove.propMoveable.get.isMovable)) {
      return ("The " + objToMove.name + " is not moveable.", false)
    }

    // Check that both arguments aren't the same
    if (objToMove.uuid == container.uuid) {
      return ("You can't move something into itself.", false)
    }

    // Check that the object is not a liquid or a gas (which can't be directly held by the agent)
    if (objToMove.propMaterial.isDefined) {
      if (objToMove.propMaterial.get.stateOfMatter == "liquid") {
        return ("You can't pick up a liquid directly.  Try pouring it from one container to another, or dunking empty containers into containers filled with the liquid.", false)
      } else if (objToMove.propMaterial.get.stateOfMatter == "gas") {
        return ("You can't pick up a gas directly.", false)
      }
    }

    // Check that if the container is a proper container, that it's open
    if (container.propContainer.isEmpty) {
      return ("That can't be moved there.", false)
    }

    if (!container.propContainer.get.isOpen) {
      return ("That can't be moved there, because the " + container.name + " isn't open.", false)
    }

    // If we reach here, all tests have passed
    return ("", true)
  }

  def generatePossibleValidActions(agent:EnvObject, visibleObjects:Array[EnvObject], uuid2referentLUT:Map[Long, String]):Array[PossibleAction] = {
    val out = new ArrayBuffer[PossibleAction]()

    for (obj1 <- visibleObjects) {
      for (obj2 <- visibleObjects) {
        // Pack for check
        val assignments = Map(
          "agent" -> agent,
          "obj" -> obj1,
          "moveTo" -> obj2
        )

        // Do check
        if (this.isValidAction(assignments)._2 == true) {
          // Pack and store
          val pa = new PossibleAction(Array[ActionExpr](
            new ActionExprText("move"),
            new ActionExprObject(obj1, referent = uuid2referentLUT(obj1.uuid)),
            new ActionExprText("to"),
            new ActionExprObject(obj2, referent = uuid2referentLUT(obj2.uuid))
          ), this.ACTION_ID)
          out.append(pa)
        }
      }
    }

    return out.toArray
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

  def isValidAction(assignments:Map[String, EnvObject], agent:Agent): (String, Boolean) = {
    val (errStr, success) = ActionMoveObject.isValidAction( remap(assignments, agent) )
    if (!success) return (errStr, success)

    // If success, do secondary checks
    val agent1 = assignments("agent")
    val objToMove = assignments("obj")

    agent1 match {
      case a:Agent => {
        val inventoryContents = a.getInventoryContainer().getContainedObjectsNotHidden()
        if (!inventoryContents.contains(objToMove)) {
          // Inventory does not contain the object to pick up -- is a valid action
          return ("", true)
        } else {
          return ("That object already appears to be in the inventory.", false)
        }
      }
    }

    return (Action.MESSAGE_UNKNOWN_CATCH, false)
  }

  def generatePossibleValidActions(agent:Agent, visibleObjects:Array[EnvObject], uuid2referentLUT:Map[Long, String]):Array[PossibleAction] = {
    val out = new ArrayBuffer[PossibleAction]()

    for (obj1 <- visibleObjects) {
      // Pack for check
      val assignments = Map(
        "agent" -> agent,
        "obj" -> obj1,
      )

      // Do check
      if (this.isValidAction(assignments, agent)._2 == true) {
        // Pack and store
        val pa = new PossibleAction(Array[ActionExpr](
          new ActionExprText("pick up"),
          new ActionExprObject(obj1, referent = uuid2referentLUT(obj1.uuid))
        ), this.ACTION_ID)
        out.append(pa)
      }
    }

    return out.toArray
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

  def isValidAction(assignments:Map[String, EnvObject], agent:Agent): (String, Boolean) = {
    val (errStr, success) = ActionMoveObject.isValidAction( remap(assignments, agent) )
    if (!success) return (errStr, success)

    // If success, do secondary checks
    val agent1 = assignments("agent")
    val objToMove = assignments("obj")

    agent1 match {
      case a:Agent => {
        val inventoryContents = a.getInventoryContainer().getContainedObjectsNotHidden()
        if (inventoryContents.contains(objToMove)) {
          // Inventory contains the object to drop -- is a valid action
          return ("", true)
        } else {
          return ("That object does not appear to be in the inventory.", false)
        }
      }
    }

    return (Action.MESSAGE_UNKNOWN_CATCH, false)
  }

  def generatePossibleValidActions(agent:Agent, visibleObjects:Array[EnvObject], uuid2referentLUT:Map[Long, String]):Array[PossibleAction] = {
    val out = new ArrayBuffer[PossibleAction]()

    for (obj1 <- visibleObjects) {
      // Pack for check
      val assignments = Map(
        "agent" -> agent,
        "obj" -> obj1,
      )

      // Do check
      if (this.isValidAction(assignments, agent)._2 == true) {
        // Pack and store
        val pa = new PossibleAction(Array[ActionExpr](
          new ActionExprText("put down"),
          new ActionExprObject(obj1, referent = uuid2referentLUT(obj1.uuid))
        ), this.ACTION_ID)
        out.append(pa)
      }
    }

    return out.toArray
  }

}



/*
 * Action: Pour (liquid, or contents of containers)
 */
class ActionPourObject(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")
    val objToMove = assignments("obj")
    val container = assignments("moveTo")

    // Do checks for valid action
    val (invalidStr, isValid) = ActionPourObject.isValidAction(assignments)
    if (!isValid) return (invalidStr, false)

    // Check to see if we're pouring out a container
    var pouringOutContainer:Boolean = false
    if ((objToMove.propContainer.isDefined) && (objToMove.propContainer.get.isContainer)) pouringOutContainer = true

    // Check that both arguments aren't the same
    if (objToMove.uuid == container.uuid) {
      return ("You can't move something into itself.", false)
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
      return (os.toString(), true)

    } else {
      container.addObject(objToMove)
      if (objToMove.isElectricallyConnected()) {
        // Electrically disconnect, if connected
        os.append("(disconnecting " + objToMove.name + ")\n")
        objToMove.disconnectElectricalTerminals()
      }
      os.append("You pour the " + objToMove.name + " into the " + container.name + ".")
      return (os.toString(), true)

    }


    return (Action.MESSAGE_UNKNOWN_CATCH, false)
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

  def isValidAction(assignments:Map[String, EnvObject]): (String, Boolean) = {
    val agent = assignments("agent")
    val objToMove = assignments("obj")
    val container = assignments("moveTo")

    // Check 1: Check that agent is valid
    agent match {
      case a:Agent => { }
      case _ => return ("I'm not sure what that means", false)
    }

    // Check to see if we're pouring out a container
    var pouringOutContainer:Boolean = false
    if ((objToMove.propContainer.isDefined) && (objToMove.propContainer.get.isContainer)) pouringOutContainer = true

    // Check that the object is moveable
    if ((objToMove.propMoveable.isEmpty) || (!objToMove.propMoveable.get.isMovable)) {
      return ("The " + objToMove.name + " is not moveable.", false)
    }
    // If we're pouring out something in a container, check that the container is movable too
    if (!pouringOutContainer) {
      val curContainer = objToMove.getContainer()
      if (curContainer.isEmpty) return ("The " + objToMove.name + " can't be poured out because it doesn't appear to be in a container.", false)

      if ((curContainer.get.propMoveable.isEmpty) || (!curContainer.get.propMoveable.get.isMovable)) {
        return ("The " + curContainer.get.name + " the " + objToMove.name + " is in is not movable.", false)
      }
    }


    if (pouringOutContainer) {
      if (!objToMove.propContainer.get.isOpen) {
        return ("The " + objToMove.name + " can't be poured out, because it's not open.", false)
      }
    } else  {
      // Check that the object is not a liquid or a gas (which can't be directly held by the agent)
      if (objToMove.propMaterial.isDefined) {
        if (objToMove.propMaterial.get.stateOfMatter == "solid") {
          return ("I'm not sure how to pour a solid. ", false)
        } else if (objToMove.propMaterial.get.stateOfMatter == "gas") {
          return ("You can't pour a gas directly. ", false)
        }
      }
    }

    // Check that if the container is a proper container, that it's open
    if (container.propContainer.isEmpty) {
      return ("That can't be moved there, because it's not a container.", false)
    }

    if (!container.propContainer.get.isOpen) {
      return ("That can't be moved there, because the " + container.name + " isn't open.", false)
    }

    if (pouringOutContainer) {
      if (objToMove.getContainedObjects().size == 0) {
        return ("The " + objToMove.name + " is empty, so there's nothing to pour.", false)
      }
    }

    // If we reach here, the action is valid
    return ("", true)
  }

  def generatePossibleValidActions(agent:EnvObject, visibleObjects:Array[EnvObject], uuid2referentLUT:Map[Long, String]):Array[PossibleAction] = {
    val out = new ArrayBuffer[PossibleAction]()

    for (obj1 <- visibleObjects) {
      for (obj2 <- visibleObjects) {
        // Pack for check
        val assignments = Map(
          "agent" -> agent,
          "obj" -> obj1,
          "moveTo" -> obj2
        )

        // Do check
        if (this.isValidAction(assignments)._2 == true) {
          // Pack and store
          val pa = new PossibleAction(Array[ActionExpr](
            new ActionExprText("pour"),
            new ActionExprObject(obj1, referent = uuid2referentLUT(obj1.uuid)),
            new ActionExprText("into"),
            new ActionExprObject(obj2, referent = uuid2referentLUT(obj2.uuid))
          ), this.ACTION_ID)
          out.append(pa)
        }
      }
    }

    return out.toArray
  }
}


//## TODO: Dunk container in liquid?