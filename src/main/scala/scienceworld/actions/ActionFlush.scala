package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.devices.Toilet
import scienceworld.struct.EnvObject

/*
 * Action: Activate
 */
class ActionFlush(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def isValidAction(): (String, Boolean) = {
    // Unimplemented
    return ("", true)
  }

  override def runAction(): String = {
    val agent = assignments("agent")
    val obj = assignments("device")

    // Check that the object is flushable
    if (!obj.isInstanceOf[Toilet]) return "It's not clear how to flush that."

    // Open
    if (obj.propDevice.get.isActivated) {
      return "That is already flushing."
    } else {
      obj.propDevice.get.isActivated = true
      return "The " + obj.name + " is now flushing."
    }

  }

}

object ActionFlush {
  val ACTION_NAME = "flush"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_FLUSH

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("flush")),
      new ActionExprIdentifier("device")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)
  }

}