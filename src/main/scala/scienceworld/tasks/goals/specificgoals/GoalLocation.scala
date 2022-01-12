package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.agent.Agent
import scienceworld.objects.livingthing.LivingThing
import scienceworld.objects.portal.Door
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn, GoalSequence}
import scala.util.control.Breaks._


// Success when an agent moves to the specified location(/container)
class GoalMoveToLocation(locationToBeIn:String, _isOptional:Boolean = false, description:String = "") extends Goal(description) {
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // NOTE: Focus object not required

    // If agent is not in a container, do not continue evaluation
    if (agent.getContainer().isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // If the agent is in the correct location, then success
    val agentLocation = agent.getContainer().get.name
    if (agentLocation == locationToBeIn) {
      return GoalReturn.mkSubgoalSuccess()
    }

    // Otherwise
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}

// Success when an agent moves to a different location than it started in
class GoalMoveToNewLocation(_isOptional:Boolean = false, description:String = "") extends Goal(description) {
  var startingLocation:Option[String] = None
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // NOTE: Focus object not required

    // If agent is not in a container, do not continue evaluation
    if (agent.getContainer().isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // Get agent location
    val agentLocation = agent.getContainer().get.name

    // First initialization: Keep track of starting location
    if (startingLocation.isEmpty) {
      gs.setKey("startingLocation", agentLocation)      // PJ: Added, just in case we need this information later
      startingLocation = Some(agentLocation)
      return GoalReturn.mkSubgoalUnsuccessful()
    }

    // If the agent is a different location than the starting location, then success
    if (agentLocation != startingLocation.get) {
      return GoalReturn.mkSubgoalSuccess()
    }

    // Otherwise
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}


// Success when an agent is in a room with an open door (principally, by opening that door)
class GoalInRoomWithOpenDoor(_isOptional:Boolean = false, description:String = "") extends Goal(description) {
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // NOTE: Focus object not required

    // If agent is not in a container, do not continue evaluation
    if (agent.getContainer().isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // Get agent location
    val agentLocation = agent.getContainer().get
    val portals = agentLocation.getPortals()

    var isAtLeastOneOpenDoor:Boolean = false
    for (portal <- portals) {
      //println ("Checking: " + portal.toStringMinimal())
      portal match {
        case d:Door => {
          if (d.isCurrentlyPassable()) {
            //print("\tIS OPEN!")
            isAtLeastOneOpenDoor = true
          }
        }
      }
    }

    // First initialization: Keep track of starting location
    if (isAtLeastOneOpenDoor) {
      return GoalReturn.mkSubgoalSuccess()
    }

    // Otherwise
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}

// Success when an agent is in a room with a particular object being visible.
// Searches object names, object descript names, and object material names.
class GoalInRoomWithObject(objectName:String, _isOptional:Boolean = false, description:String = "") extends Goal(description) {
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // NOTE: Focus object not required

    // If agent is not in a container, do not continue evaluation
    if (agent.getContainer().isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // Get agent location
    val agentLocation = agent.getContainer().get
    val visibleObjects = agentLocation.getContainedObjectsAndPortalsRecursive(includeHidden = false, includePortalConnections = false)

    var found:Boolean = false
    breakable {
      for (obj <- visibleObjects) {
        if ((obj.name.toLowerCase == objectName.toLowerCase) ||
            (obj.getDescriptName().toLowerCase == objectName)) {
            //((obj.propMaterial.isDefined) && (obj.propMaterial.get.substanceName == objectName))) {
          found = true
          break
        }
      }
    }

    // First initialization: Keep track of starting location
    if (found) {
      return GoalReturn.mkSubgoalSuccess()
    }

    // Otherwise
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}


// Success when an agent is in a room with a particular object being visible.
// Searches object names, object descript names, and object material names.
class GoalObjectsInSingleContainer(objectNames:Array[String], _isOptional:Boolean = false, description:String = "") extends Goal(description) {
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // NOTE: Focus object not required

    // If agent is not in a container, do not continue evaluation
    if (agent.getContainer().isEmpty) return GoalReturn.mkSubgoalUnsuccessful()
    // If no objects to find, then do not continue evaluation
    if (objectNames.size == 0) return GoalReturn.mkSubgoalUnsuccessful()

    // Get agent location
    val agentLocation = agent.getContainer().get
    val visibleObjects = agentLocation.getContainedObjectsAndPortalsRecursive(includeHidden = false, includePortalConnections = false)

    var found:Boolean = false
    breakable {
      for (obj <- visibleObjects) {
        val contents = obj.getContainedObjects(includeHidden = false)
        println("Obj: " + obj.name + "\t contents: " + contents.map(_.name).mkString(", "))
        if (contents.size == objectNames.size) {
          println("\tChecking")
          var success:Boolean = true
          // Check contents
          for (cObj <- contents) {
            if (objectNames.contains(cObj.name) || objectNames.contains(cObj.getDescriptName())) {
              // So far, so good

            } else {
              success = false
            }
          }

          println("\tSuccess:" + success)
          if (success) {
            found = true
            break()
          }

        }
      }
    }

    // First initialization: Keep track of starting location
    if (found) {
      return GoalReturn.mkSubgoalSuccess()
    }

    // Otherwise
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}
