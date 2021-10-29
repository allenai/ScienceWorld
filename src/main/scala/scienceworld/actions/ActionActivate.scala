package scienceworld.actions

import language.model.{ActionExpr, ActionExprIdentifier, ActionExprOR, ActionExprObject, ActionExprText, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject

import scala.collection.mutable.ArrayBuffer

/*
 * Action: Activate
 */
class ActionActivate(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")
    val obj = assignments("device")

    // Do checks for valid action
    val (invalidStr, isValid) = ActionActivate.isValidAction(assignments)
    if (!isValid) return (invalidStr, false)

    // Activate
    if (!obj.propDevice.get.isActivated) {
      obj.propDevice.get.isActivated = true
      return ("The " + obj.name + " is now activated.", true)
    }

    return (Action.MESSAGE_UNKNOWN_CATCH, false)
  }

}

object ActionActivate {
  val ACTION_NAME = "activate"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_ACTIVATE

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("activate", "turn on")),
      new ActionExprIdentifier("device")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)
  }


  def isValidAction(assignments:Map[String, EnvObject]): (String, Boolean) = {
    val agent = assignments("agent")
    val obj = assignments("device")

    // Check 1: Check that agent is valid
    agent match {
      case a:Agent => { }
      case _ => return ("I'm not sure what that means", false)
    }

    // Check 2: Check that the object is activable
    if ((obj.propDevice.isEmpty) || (obj.propDevice.get.isActivable == false)) {
      return ("The " + obj.name + " is not something that can be activated.", false)
    }

    // Check 3: Check that the object is not already activated
    if (obj.propDevice.get.isActivated) {
      return ("The " + obj.name + " is already activated.", false)
    }

    // Checks complete -- if we reach here, the action is valid
    return ("", true)
  }

  def generatePossibleValidActions(agent:EnvObject, visibleObjects:Array[EnvObject]):Array[PossibleAction] = {
    val out = new ArrayBuffer[PossibleAction]()

    for (obj <- visibleObjects) {
      // Pack for check
      val assignments = Map(
        "agent" -> agent,
        "device" -> obj
      )

      // Do check
      if (this.isValidAction(assignments)._2 == true) {
        // Pack and store
        val pa = new PossibleAction(Array[ActionExpr](
          new ActionExprText("activate"),
          new ActionExprObject(obj)
        ))
        out.append(pa)
      }
    }

    return out.toArray
  }
}


/*
 * Action: Deactivate
 */
class ActionDeactivate(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")
    val obj = assignments("device")

    // Do checks for valid action
    val (invalidStr, isValid) = ActionDeactivate.isValidAction(assignments)
    if (!isValid) return (invalidStr, false)

    // Open
    if (obj.propDevice.get.isActivated) {
      obj.propDevice.get.isActivated = false
      return ("The " + obj.name + " is now deactivated.", true)
    }

    return (Action.MESSAGE_UNKNOWN_CATCH, false)
  }


}

object ActionDeactivate {
  val ACTION_NAME = "deactivate"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_DEACTIVATE

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("deactivate", "turn off")),
      new ActionExprIdentifier("device")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)
  }


  def isValidAction(assignments:Map[String, EnvObject]): (String, Boolean) = {
    val agent = assignments("agent")
    val obj = assignments("device")

    // Check 1: Check that agent is valid
    agent match {
      case a:Agent => { }
      case _ => return ("I'm not sure what that means", false)
    }

    // Check 2: Check that the object is activable
    if ((obj.propDevice.isEmpty) || (obj.propDevice.get.isActivable == false)) {
      return ("The " + obj.name + " is not something that can be activated.", false)
    }

    // Check 3: Check that the object is not already activated
    if (!obj.propDevice.get.isActivated) {
      return ("The " + obj.name + " is already deactivated.", false)
    }

    // Checks complete -- if we reach here, the action is valid
    return ("", true)
  }

  def generatePossibleValidActions(agent:EnvObject, visibleObjects:Array[EnvObject]):Array[PossibleAction] = {
    val out = new ArrayBuffer[PossibleAction]()

    for (obj <- visibleObjects) {
      // Pack for check
      val assignments = Map(
        "agent" -> agent,
        "device" -> obj
      )

      // Do check
      if (this.isValidAction(assignments)._2 == true) {
        // Pack and store
        val pa = new PossibleAction(Array[ActionExpr](
          new ActionExprText("deactivate"),
          new ActionExprObject(obj)
        ))
        out.append(pa)
      }
    }

    return out.toArray
  }

}