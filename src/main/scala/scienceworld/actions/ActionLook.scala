package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._
import util.StringHelpers

import scala.collection.mutable.ArrayBuffer


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
    return container.getDescriptionSafe(mode = MODE_DETAILED).getOrElse("<ERROR: attempting to view hidden object>")

  }

}

object ActionLookAround {
  val ACTION_NAME = "look around"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("look around", "look"))
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

    return obj.getDescriptionSafe(mode = MODE_DETAILED).getOrElse("<ERROR: attempting to view hidden object>")

  }

}

object ActionLookAt {
  val ACTION_NAME = "look at"

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("look at", "look on", "examine")),
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
    val os = new StringBuilder()

    if (obj.propContainer.isDefined) {
      if (!obj.propContainer.get.isOpen) {
        return "The " + obj.name + " isn't open, so you can't see inside."
      } else {
        // Normal case -- look inside the container
        val containedObjs = obj.getContainedObjects()
        if (containedObjs.size == 0) {
          os.append ("There is nothing in the " + obj.name + ".")
        } else {
          val objNames = containedObjs.map(_.name)
          os.append ("Inside the " + obj.name + " is: \n")
          os.append( StringHelpers.objectListToStringDescription(obj.getContainedObjects(), perspectiveContainer=agent, multiline = true)  )
        }
        os.append("\n")
      }
    }

    if (obj.getPortals().size > 0) {
      os.append(" You also see: ")
      val descriptions = new ArrayBuffer[String]
      for (portal <- obj.getPortals()) {
        val desc = portal.getDescriptionSafe(mode = MODE_CURSORY_DETAIL, perspectiveContainer = obj)
        if (desc.isDefined) descriptions.append(desc.get)
      }
      os.append(descriptions.mkString(", "))
      os.append(".")
    }
    if (os.length > 0) return os.toString

    // Otherwise
    return "It's not clear how to look inside of that."
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