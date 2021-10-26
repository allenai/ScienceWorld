package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.devices.Device
import scienceworld.struct.EnvObject

/*
 * Action: Use device
 */
class ActionUseDevice(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val device = assignments("device")
    val patientObj = assignments("patient")

    if ((device.propDevice.isDefined) && (device.propDevice.get.isUsable)) {
      val (success, resultStr) = device.useWith(patientObj)
      if (!success) return "I'm not sure how to use those two things together."

      return resultStr
    }

    return "I'm not sure how to use those two things together."
  }

}

object ActionUseDevice {
  val ACTION_NAME = "use object"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_USEDEVICE

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("use")),
      new ActionExprIdentifier("device"),
      new ActionExprOR(List("on", "with")),
      new ActionExprIdentifier("patient")
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)

  }

}