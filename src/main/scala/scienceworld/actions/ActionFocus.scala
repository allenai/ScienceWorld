package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{GoalSequence, ObjMonitor}

/*
 * Action: Focus
 */
class ActionFocus(action:ActionRequestDef, assignments:Map[String, EnvObject], objMonitor:ObjMonitor) extends Action(action, assignments) {

  // This action is essentially always valid
  override def isValidAction(): (String, Boolean) = {
    // Check 1: Check that agent is valid
    val agent = assignments("agent")
    agent match {
      case a:Agent => { }
      case _ => return ("I'm not sure what that means", false)
    }

    // Checks complete -- if we reach here, the action is valid
    return ("", true)
  }

  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")
    val obj = assignments("obj")

    // Do checks for valid action
    val (invalidStr, isValid) = this.isValidAction()
    if (!isValid) return (invalidStr, false)

    // Do action
    objMonitor.clearMonitoredObjects()
    objMonitor.addMonitor(obj)

    return ("You focus on the " + obj.name + ".", true)
  }

}

object ActionFocus {
  val ACTION_NAME = "focus on"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_FOCUS

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("focus on", "focus")),
      new ActionExprIdentifier("obj")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)
  }

}

/*
 * Action: Focus
 */
class ActionResetTask(action:ActionRequestDef, assignments:Map[String, EnvObject], objMonitor:ObjMonitor, goalSequence:GoalSequence) extends Action(action, assignments) {
  // This action is essentially always valid
  override def isValidAction(): (String, Boolean) = {
    // Check 1: Check that agent is valid
    val agent = assignments("agent")
    agent match {
      case a:Agent => { }
      case _ => return ("I'm not sure what that means", false)
    }

    // Checks complete -- if we reach here, the action is valid
    return ("", true)
  }


  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")

    // Do checks for valid action
    val (invalidStr, isValid) = this.isValidAction()
    if (!isValid) return (invalidStr, false)

    goalSequence.reset()
    objMonitor.clearMonitoredObjects()

    return ("You reset the goal progress and focus.", true)
  }

}

object ActionResetTask {
  val ACTION_NAME = "reset task"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_RESETTASK

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("reset task")),
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)
  }

}