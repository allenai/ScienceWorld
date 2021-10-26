package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.agent.Agent
import scienceworld.objects.document.Document
import scienceworld.struct.EnvObject

/*
 * Action: Wait
 */
class ActionWait(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val waitTime:Int = 10     // Number of iterations to wait for

    agent match {
      case a:Agent => {
        a.setWait(waitTime)
        return "You decide to wait for " + waitTime + " iterations."
      }
    }

    return "I'm not sure what that means."
  }

}

object ActionWait {
  val ACTION_NAME = "wait"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_WAIT

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("wait")),
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)
  }

}