package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject

/*
 * Action: Look Around
 */
class ActionLookAround(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")

    if (agent.getContainer().isEmpty) {
      return "The agent is not in a container (this should never happen)."
    }

    val container = agent.getContainer().get
    return container.getDescription()

  }

}

object ActionLookAround {
  val ACTION_NAME = "look around"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("look", "look around"))
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}



/*
 * Action: Look Around
 */
class ActionLookAt(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val obj = assignments("obj")

    return obj.getDescription()
  }

}

object ActionLookAt {
  val ACTION_NAME = "look at"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("look at")),
      new ActionExprIdentifier("obj")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}


/*
 * Action: Look Around
 */
class ActionLookIn(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val obj = assignments("obj")

    val containedObjs = obj.getContainedObjects()
    if (containedObjs.size == 0) {
      return "There is nothing in the " + obj.name + "."
    } else {
      val objNames = containedObjs.map(_.name)
      return "Inside the " + obj.name + " is: " + objNames.mkString(", ") + "."
    }
  }

}

object ActionLookIn {
  val ACTION_NAME = "look in"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("look in")),
      new ActionExprIdentifier("obj")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)
  }

}