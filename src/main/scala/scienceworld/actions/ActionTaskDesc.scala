package scienceworld.actions

import language.model.{ActionExpr, ActionExprOR, ActionExprText, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject.MODE_DETAILED
import util.StringHelpers


/*
 * Action: Task Description
 */
class ActionTaskDesc(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")

    // Do checks for valid action
    val (invalidStr, isValid) = ActionInventory.isValidAction(assignments)
    if (!isValid) return (invalidStr, false)

    agent match {
      case a:Agent => {
        val os = new StringBuilder
        os.append("Task Description:\n")
        os.append( a.getTaskDescription() )
        return (os.toString(), true)
      }
    }

  }

}

object ActionTaskDesc {
  val ACTION_NAME = "task description"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_TASKDESC

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("task")),
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)

  }

  // This action is essentially always valid
  def isValidAction(assignments:Map[String, EnvObject]): (String, Boolean) = {
    // Check 1: Check that agent is valid
    val agent = assignments("agent")
    agent match {
      case a:Agent => { }
      case _ => return ("<ERROR> Error viewing inventory - invalid agent.", false)
    }

    // Checks complete -- if we reach here, the action is valid
    return ("", true)
  }


  def generatePossibleValidActions(agent:EnvObject, visibleObjects:Array[EnvObject], uuid2referentLUT:Map[Long, String]):Array[PossibleAction] = {
    // Single possible valid action
    val pa = new PossibleAction(Array[ActionExpr](
      new ActionExprText("task")
    ), this.ACTION_ID)
    return Array( pa )
  }

}
