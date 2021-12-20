package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.agent.Agent
import scienceworld.objects.livingthing.LivingThing
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn}

// Success when an agent moves to the specified location(/container)
class GoalMoveToLocation(locationToBeIn:String) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal], agent:Agent):GoalReturn = {
    // If agent is not in a container, do not continue evaluation
    if (agent.getContainer().isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // If the agent is in the correct location, then success
    val agentLocation = agent.getContainer().get.name
    if (agentLocation == locationToBeIn) {
      // Pass through last focus object
      if (lastGoal.isDefined) {
        this.satisfiedWithObject = lastGoal.get.satisfiedWithObject
      }

      return GoalReturn.mkSubgoalSuccess()
    }

    // Otherwise
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}

// Success when an agent moves to a different location than it started in
class GoalMoveToNewLocation() extends Goal {
  var startingLocation:Option[String] = None

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal], agent:Agent):GoalReturn = {
    // If agent is not in a container, do not continue evaluation
    if (agent.getContainer().isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // Get agent location
    val agentLocation = agent.getContainer().get.name

    // First initialization: Keep track of starting location
    if (startingLocation.isEmpty) {
      startingLocation = Some(agentLocation)
      return GoalReturn.mkSubgoalUnsuccessful()
    }

    // If the agent is a different location than the starting location, then success
    if (agentLocation != startingLocation.get) {
      // Pass through last focus object
      if (lastGoal.isDefined) {
        this.satisfiedWithObject = lastGoal.get.satisfiedWithObject
      }

      return GoalReturn.mkSubgoalSuccess()
    }

    // Otherwise
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}

