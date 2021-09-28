package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject

/*
 * Action: Eat
 */
class ActionEat(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    return "You eat the thing."
  }

}

object ActionEat {
  val ACTION_NAME = "eat"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("eat", "consume")),
      new ActionExprIdentifier("food")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}