package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.struct.EnvObject

/*
 * Action: Activate
 */
class ActionActivate(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def isValidAction(): (String, Boolean) = {
    // Unimplemented
    return ("", true)
  }

  override def runAction(): String = {
    val agent = assignments("agent")
    val obj = assignments("device")

    // Check that the object is openable
    if ((obj.propDevice.isEmpty) || (obj.propDevice.get.isActivable == false)) {
      return "The " + obj.name + " is not something that can be activated."
    }

    // Open
    if (obj.propDevice.get.isActivated) {
      return "The " + obj.name + " is already activated."
    } else {
      obj.propDevice.get.isActivated = true
      return "The " + obj.name + " is now activated."
    }

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
    // Unimplemented
    return ("", true)
  }

  override def runAction(): String = {
    val agent = assignments("agent")
    val obj = assignments("device")

    // Check that the object is openable
    if ((obj.propDevice.isEmpty) || (obj.propDevice.get.isActivable == false)) {
      return "The " + obj.name + " is not something that can be deactivated."
    }

    // Open
    if (!obj.propDevice.get.isActivated) {
      return "The " + obj.name + " is already deactivated."
    } else {
      obj.propDevice.get.isActivated = false
      return "The " + obj.name + " is now deactivated."
    }

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