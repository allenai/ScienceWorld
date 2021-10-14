package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.ActionHandler
import scienceworld.objects.electricalcomponent.{PolarizedElectricalComponent, Terminal, UnpolarizedElectricalComponent}
import scienceworld.struct.EnvObject


/*
 * Action: Connect (Electrically)
 */
class ActionConnectElectrical(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val terminalA = assignments("terminalA")
    val terminalB = assignments("terminalB")

    // Check that the objects are connectable
    if (terminalA.propElectricalConnection.isEmpty) return "It's not clear how to connect something to " + terminalA.name
    if (terminalB.propElectricalConnection.isEmpty) return "It's not clear how to connect something to " + terminalB.name

    // Check that the objects aren't already connected
    if (terminalA.propElectricalConnection.get.isAtMaxConnections()) {
      terminalA match {
        case t:Terminal => { return t.name + " on " + t.parentObject.name + " is already connected, and must be disconnected before it can be connected to something else" }
        case _ => { return terminalA.name + " is already connected, and must be disconnected before it can be connected to something else" }
      }
    }
    if (terminalB.propElectricalConnection.get.isAtMaxConnections()) {
      terminalB match {
        case t:Terminal => { return t.name + " on " + t.parentObject.name + " is already connected, and must be disconnected before it can be connected to something else" }
        case _ => { return terminalB.name + " is already connected, and must be disconnected before it can be connected to something else" }
      }
    }

    // Check that A and B are in the same container?
    // TODO

    // Do connection
    terminalA.propElectricalConnection.get.addConnection(terminalB)
    terminalB.propElectricalConnection.get.addConnection(terminalA)
    return terminalA.name + " is now connected to " + terminalB.name

  }

}

object ActionConnectElectrical {
  val ACTION_NAME = "connect electrically"

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("connect")),
      new ActionExprIdentifier("terminalA"),
      new ActionExprOR(List("to", "in", "into")),
      new ActionExprIdentifier("terminalB")
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)

  }

}


class ActionDisconnectElectrical(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def runAction(): String = {
    val agent = assignments("agent")
    val obj = assignments("obj")

    obj match {
      case x:PolarizedElectricalComponent => {
        x.disconnect()
        return obj.name + " has been disconnected"
      }
      case x:UnpolarizedElectricalComponent => {
        x.disconnect()
        return obj.name + " has been disconnected"
      }
      case x:Terminal => {
        return "You must disconnect entire objects (such as " + x.parentObject.name + ") instead of just single terminals"
      }
      case _ => "It's not clear how to disconnect that"
    }


  }

}

object ActionDisconnectElectrical {
  val ACTION_NAME = "disconnect electrically"

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("disconnect")),
      new ActionExprIdentifier("obj"),
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase)
    actionHandler.addAction(action)

  }

}