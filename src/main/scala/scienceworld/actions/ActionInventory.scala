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

  override def runAction(): String = {
    val agent = assignments("agent")

    agent match {
      case a:Agent => {
        val os = new StringBuilder
        val inventoryContainer = a.getInventoryContainer()
        os.append("In your inventory, you see:\n")
        os.append( StringHelpers.objectListToStringDescription(inventoryContainer.getContainedObjects(), perspectiveContainer = inventoryContainer, mode = MODE_DETAILED, multiline = true) )
        return os.toString()
      }
      case _ => {
        return "<ERROR> error viewing inventory."
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
