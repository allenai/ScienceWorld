package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{GoalSequence, ObjMonitor}

/*
 * Action: Focus
 */
class ActionFocus(action:ActionRequestDef, assignments:Map[String, EnvObject], objMonitor:ObjMonitor) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val obj = assignments("obj")

    objMonitor.clearMonitoredObjects()
    objMonitor.addMonitor(obj)

    return "You focus on the " + obj.name + "."
  }

}

object ActionFocus {
  val ACTION_NAME = "focus on"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("focus on", "focus")),
      new ActionExprIdentifier("obj")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}

/*
 * Action: Focus
 */
class ActionResetTask(action:ActionRequestDef, assignments:Map[String, EnvObject], objMonitor:ObjMonitor, goalSequence:GoalSequence) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")

    goalSequence.reset()
    objMonitor.clearMonitoredObjects()

    return "You reset the goal progress and focus."
  }

}

object ActionResetTask {
  val ACTION_NAME = "reset task"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("reset task")),
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}