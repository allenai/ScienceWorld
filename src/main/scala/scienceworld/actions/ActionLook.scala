package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._
import util.StringHelpers

import scala.collection.mutable.ArrayBuffer


/*
 * Action: Look Around
 */
class ActionLookAround(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  // This action is essentially always valid
  override def isValidAction(): (String, Boolean) = {
    // Check 1: Check that agent is valid
    val agent = assignments("agent")
    agent match {
      case a:Agent => {
        if (a.getContainer().isEmpty) return ("<ERROR> The agent is not in a container (this should never happen)", false)
      }
      case _ => return ("<ERROR> Invalid agent.", false)
    }

    // Checks complete -- if we reach here, the action is valid
    return ("", true)
  }

  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")

    // Do checks for valid action
    val (invalidStr, isValid) = this.isValidAction()
    if (!isValid) return (invalidStr, false)

    val container = agent.getContainer().get
    val containerDescription = container.getDescriptionSafe(mode = MODE_DETAILED).getOrElse("<ERROR: attempting to view hidden object>")    //## TODO: Arguable whether the error case here should be caught by checks above
    return (containerDescription, true)

  }

}

object ActionLookAround {
  val ACTION_NAME = "look around"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_LOOK_AROUND

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("look around", "look"))
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)
  }

}



/*
 * Action: Look Around
 */
class ActionLookAt(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  // This action is essentially always valid
  override def isValidAction(): (String, Boolean) = {
    // Check 1: Check that agent is valid
    val agent = assignments("agent")
    agent match {
      case a:Agent => {
        if (a.getContainer().isEmpty) return ("<ERROR> The agent is not in a container (this should never happen)", false)
      }
      case _ => return ("<ERROR> Invalid agent.", false)
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

    val objDescription = obj.getDescriptionSafe(mode = MODE_DETAILED).getOrElse("<ERROR: attempting to view hidden object>")    //## TODO: Arguable whether the error case here should be caught by checks above
    return (objDescription, true)

  }

}

object ActionLookAt {
  val ACTION_NAME = "look at"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_LOOK_AT

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("look at", "look on", "examine")),
      new ActionExprIdentifier("obj")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)
  }

}


/*
 * Action: Look Around
 */
class ActionLookIn(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  // This action is essentially always valid
  override def isValidAction(): (String, Boolean) = {
    // Check 1: Check that agent is valid
    val agent = assignments("agent")
    val obj = assignments("obj")

    agent match {
      case a:Agent => {
        if (a.getContainer().isEmpty) return ("<ERROR> The agent is not in a container (this should never happen)", false)
      }
      case _ => return ("<ERROR> Invalid agent.", false)
    }

    // Check 2: If it's a container, check that it's open
    if (obj.propContainer.isDefined) {
      if (!obj.propContainer.get.isOpen) {
        // Unopen container -- fail
        return ("The " + obj.name + " isn't open, so you can't see inside.", false)
      }
      // Open container -- OK
      return ("", true)
    }

    // If we reach here, it's not a container, so we can't look in it
    return ("It's not clear how to look inside of that.", false)
  }

  override def runAction(): (String, Boolean) = {
    val agent = assignments("agent")
    val obj = assignments("obj")
    val os = new StringBuilder()

    // Do checks for valid action
    val (invalidStr, isValid) = this.isValidAction()
    if (!isValid) return (invalidStr, false)

    // Run action
    if (obj.propContainer.isDefined) {
      if (obj.propContainer.get.isOpen) {
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
    }

    // Return
    return (os.toString, true)
  }

}

object ActionLookIn {
  val ACTION_NAME = "look in"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_LOOK_IN

  def registerAction(actionHandler:ActionHandler) {
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("look in")),
      new ActionExprIdentifier("obj")
    ))

    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)
  }

}