package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject

/*
 * Action: Activate
 */
class ActionActivate(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  // This action is essentially always valid
  override def isValidAction(): (String, Boolean) = {
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

  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")
    val obj = assignments("device")

    // Do checks for valid action
    val (invalidStr, isValid) = this.isValidAction()
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

}


/*
 * Action: Deactivate
 */
class ActionDeactivate(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def isValidAction(): (String, Boolean) = {
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

  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")
    val obj = assignments("device")

    // Do checks for valid action
    val (invalidStr, isValid) = this.isValidAction()
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

}