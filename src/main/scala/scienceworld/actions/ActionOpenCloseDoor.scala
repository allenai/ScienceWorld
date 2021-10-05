package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.objects.portal.Door
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject

/*
 * Action: Open Door
 */
class ActionOpenDoor(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val obj = assignments("door")

    // Case 1: Openable portals (e.g. doors)
    if (obj.propPortal.isDefined) {
      // Open
      if (obj.propPortal.get.isOpen) {
        return "The " + obj.name + " is already open."
      } else {
        if (obj.propPortal.get.isOpenable) {
          obj.propPortal.get.isOpen = true
          return "The " + obj.name + " is now open."
        } else {
          return "The " + obj.name + " is not openable."
        }
      }
    }

    // Case 2: Openable containers (e.g. cupboards)
    if (obj.propContainer.isDefined) {
      if (!obj.propContainer.get.isClosable) {
        return "The " + obj.name + " is not openable."
      }

      // Open
      if (obj.propContainer.get.isOpen) {
        return "The " + obj.name + " is already open."
      } else {
        obj.propContainer.get.isOpen = true
        return "The " + obj.name + " is now open."
      }
    }

    // Case 3: Not a portal or a container
    return "The " + obj.name + " is not openable."

  }

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

  override def runAction(): String = {
    val agent = assignments("agent")
    val obj = assignments("door")

    // Case 1: Openable portals (e.g. doors)
    if (obj.propPortal.isDefined) {
      // Open
      if (!obj.propPortal.get.isOpen) {
        return "The " + obj.name + " is already closed."
      } else {
        if (obj.propPortal.get.isOpenable) {
          obj.propPortal.get.isOpen = false
          return "The " + obj.name + " is now closed."
        } else {
          return "The " + obj.name + " is not closeable."
        }
      }
    }

    // Case 2: Openable containers (e.g. cupboards)
    if (obj.propContainer.isDefined) {
      if (!obj.propContainer.get.isClosable) {
        return "The " + obj.name + " is not closeable."
      }

      // Open
      if (!obj.propContainer.get.isOpen) {
        return "The " + obj.name + " is already closed."
      } else {
        obj.propContainer.get.isOpen = false
        return "The " + obj.name + " is now closed."
      }
    }

    // Case 3: Not a portal or a container
    return "The " + obj.name + " is not closeable."


  }

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