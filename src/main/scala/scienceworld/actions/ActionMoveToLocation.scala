package scienceworld.actions

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import scienceworld.objects.portal.Door
import scienceworld.input.ActionDefinitions.mkActionRequest
import scienceworld.input.{ActionDefinitions, ActionHandler}
import scienceworld.objects.location.Location
import scienceworld.struct.EnvObject

import scala.util.control.Breaks._


/*
 * Action: Move through door
 */
class ActionMoveThroughDoor(action:ActionRequestDef, assignments:Map[String, EnvObject]) extends Action(action, assignments) {

  override def isValidAction(): (String, Boolean) = {
    // Unimplemented
    return ("", true)
  }

  override def runAction(): String = {
    val agent = assignments("agent")
    val doorOrLocation = assignments("doorOrLocation")

    doorOrLocation match {
      // Case 1: Moving through door
      case door:Door => {
        if (!door.isCurrentlyPassable()) {
          // Return a human-readable message for why the door isn't passable
          return door.getUnpassableErrorMessage()
        }

        // Move the agent through door

        // First, check which side of the door we're on
        val door1 = door.asInstanceOf[Door]
        val agentContainer = agent.getContainer().get
        var connectsTo = door1.getConnectsTo(agentContainer)
        if (connectsTo.isEmpty) {
          return "The door doesn't appear to go anywhere."
        }

        // Then, move the agent to the other side
        connectsTo.get.addObject(agent)
        return "You move through the " + door.name + " to the " + connectsTo.get.name + "."

      }
      case location:Location => {
        // Try to find a portal that goes from the current location to the requested location
        val agentLocation = agent.getContainerRecursiveOfType[Location]()
        if (agentLocation.isEmpty) return "<ERROR> The agent doesn't appear to be in a valid location."

        for (portal <- agentLocation.get.getPortals()) {
          breakable {
            // Find a list of the referent names that this portal connects to
            val connectsTo = portal.getConnectsTo(perspectiveContainer = agentLocation.get)
            if (connectsTo.isEmpty) break
            val connectsToLocationReferents = connectsTo.get.getReferents()
            // If one of the referents is the same as the location we're looking for, then try to go through that poral
            if (connectsToLocationReferents.contains(location.name)) {
              if (!portal.isCurrentlyPassable()) {
                // Return a human-readable message for why the door isn't passable
                return portal.getUnpassableErrorMessage()
              }

              // If we reach here, move the agent through the portal
              location.addObject(agent)
              return "You move to the " + location.name + "."
            }
          }
        }

        return "It's not clear how to get there from here."
      }
      case _ => {
        return "Its not clear how to go to/through a " + doorOrLocation.name + "."
      }
    }

  }

}

object ActionMoveThroughDoor {
  val ACTION_NAME = "move through door"
  val ACTION_ID   = ActionDefinitions.ACTION_ID_MOVETHRUDOOR

  def registerAction(actionHandler:ActionHandler) {
    // Action: Move through door
    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("go through", "walk through", "move through", "go to", "walk to", "move to", "go into", "move into")),
      new ActionExprIdentifier("doorOrLocation")
    ))
    val action = mkActionRequest(ACTION_NAME, triggerPhrase, ACTION_ID)
    actionHandler.addAction(action)
  }

}
