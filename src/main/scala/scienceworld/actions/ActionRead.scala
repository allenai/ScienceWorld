package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.document.Document
import scienceworld.struct.EnvObject

/*
 * Action: Read
 */
class ActionRead(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val document = assignments("document")

    document match {
      case x:Document => return x.readDocument()
      case _ => return "It's not clear how to read that."
    }

  }

}

object ActionRead {
  val ACTION_NAME = "read"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_READ

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("read")),
      new ActionExprIdentifier("document")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)
  }

}