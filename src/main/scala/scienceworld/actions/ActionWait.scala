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
    val waitTime:Int = 10     // Number of iterations to wait for

    // Do checks for valid action
    val (invalidStr, isValid) = this.isValidAction()
    if (!isValid) return (invalidStr, false)

    agent match {
      case a:Agent => {
        a.setWait(waitTime)
        return ("You decide to wait for " + waitTime + " iterations.", true)
      }
    }

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