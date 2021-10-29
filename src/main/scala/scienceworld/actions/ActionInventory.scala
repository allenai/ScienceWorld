package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._
import util.StringHelpers

/*
 * Action: Inventory
 */
class ActionInventory(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  // This action is essentially always valid
  override def isValidAction(): (String, Boolean) = {
    // Check 1: Check that agent is valid
    val agent = assignments("agent")
    agent match {
      case a:Agent => { }
      case _ => return ("<ERROR> Error viewing inventory - invalid agent.", false)
    }

    // Checks complete -- if we reach here, the action is valid
    return ("", true)
  }

  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")

    // Do checks for valid action
    val (invalidStr, isValid) = this.isValidAction()
    if (!isValid) return (invalidStr, false)

    agent match {
      case a:Agent => {
        val os = new StringBuilder
        val inventoryContainer = a.getInventoryContainer()
        os.append("In your inventory, you see:\n")
        os.append( StringHelpers.objectListToStringDescription(inventoryContainer.getContainedObjects(), perspectiveContainer = inventoryContainer, mode = MODE_DETAILED, multiline = true) )
        return (os.toString(), true)
      }
    }

  }

}

object ActionInventory {
  val ACTION_NAME = "view inventory"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_INVENTORY

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("inventory")),
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)

  }

}
