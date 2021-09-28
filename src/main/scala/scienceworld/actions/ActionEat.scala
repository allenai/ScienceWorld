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
    val agent = assignments("agent")
    val food = assignments("food")

    // Case 1: Food is not edible
    if ((food.propEdibility.isEmpty) || (food.propEdibility.get.isEdible == false)) {
      return "The " + food.name + " is not edible."
    }

    // Case 2: Poisonous?
    if (food.propEdibility.get.isPoisonous) {
      food.removeAndResetContainer()
      return "You don't feel well."
      //TODO: Health?
    }

    // Case 3: Food is edible
    food.removeAndResetContainer()
    return "The " + food.name + " was delicious."
    // TODO: Increase health/nutrients/etc

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