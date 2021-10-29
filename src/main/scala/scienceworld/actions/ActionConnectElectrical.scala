package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.electricalcomponent.{PolarizedElectricalComponent, Terminal, UnpolarizedElectricalComponent}
import scienceworld.struct.EnvObject


/*
 * Action: Connect (Electrically)
 */
class ActionConnectElectrical(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {
  override def isValidAction(): (String, Boolean) = {
    // Unimplemented
    return ("", true)
  }

  override def runAction(): String = {
    val agent = assignments("agent")
    var terminalA = assignments("terminalA")
    var terminalB = assignments("terminalB")

    // Find a connection point on object passed as TerminalA
    terminalA match {
      case x:Terminal => { }    // Valid/Good
      case x:PolarizedElectricalComponent => { return "Connections must specify specific points of contact on polarized electrical components (e.g. anode, cathode) "}
      case x:UnpolarizedElectricalComponent => { return "Connections must specify specific points of contact on unpolarized electrical components (e.g. terminal 1, terminal 2) "}
      case x:EnvObject => {
        // Case: Not an object with terminals to connect to
        if (!x.hasUnpolarizedElectricalTerminals()) return "It's not clear how to connect something to " + x.name
        val unconnectedTerminal = x.getUnconnectedElectricalTerminal()
        // Case: An object with terminals that are already used
        if (unconnectedTerminal.isEmpty) return x.name + " is already connected, and must be disconnected before it can be connected to something else"
        // Case: An object with a free terminal
        terminalA = unconnectedTerminal.get
      }
    }

    // Find a connection point on object passed as TerminalA
    terminalB match {
      case x:Terminal => { }    // Valid/Good
      case x:PolarizedElectricalComponent => { return "Connections must specify specific points of contact on polarized electrical components (e.g. anode, cathode) "}
      case x:UnpolarizedElectricalComponent => { return "Connections must specify specific points of contact on unpolarized electrical components (e.g. terminal 1, terminal 2) "}
      case x:EnvObject => {
        // Case: Not an object with terminals to connect to
        if (!x.hasUnpolarizedElectricalTerminals()) return "It's not clear how to connect something to " + x.name
        val unconnectedTerminal = x.getUnconnectedElectricalTerminal()
        // Case: An object with terminals that are already used
        if (unconnectedTerminal.isEmpty) return x.name + " is already connected, and must be disconnected before it can be connected to something else"
        // Case: An object with a free terminal
        terminalB = unconnectedTerminal.get
      }
    }

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
    val terminalAObj:String = terminalA.asInstanceOf[Terminal].parentObject.name
    val terminalBObj:String = terminalA.asInstanceOf[Terminal].parentObject.name

    return terminalA.name + " on " + terminalAObj + " is now connected to " + terminalB.name + " on " + terminalBObj

  }

}

object ActionConnectElectrical {
  val ACTION_NAME = "connect electrically"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_CONNECT

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("connect")),
      new ActionExprIdentifier("terminalA"),
      new ActionExprOR(List("to", "in", "into")),
      new ActionExprIdentifier("terminalB")
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)

  }

}


class ActionDisconnectElectrical(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def isValidAction(): (String, Boolean) = {
    // Unimplemented
    return ("", true)
  }

  override def runAction(): String = {
    val agent = assignments("agent")
    val obj = assignments("obj")

    obj match {
      case x:Terminal => {
        return "You must disconnect entire objects (such as " + x.parentObject.name + ") instead of just single terminals"
      }
      case x:EnvObject => {
        x.disconnectElectricalTerminals()
        return x.name + " has been disconnected"
      }
      case _ => "It's not clear how to disconnect that"
    }


  }

}

object ActionDisconnectElectrical {
  val ACTION_NAME = "disconnect electrically"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_DISCONNECT

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("disconnect")),
      new ActionExprIdentifier("obj"),
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)

  }

}