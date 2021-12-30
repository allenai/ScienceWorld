package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn, GoalSequence}


class GoalContainerByTemperature(tempThreshold:Double, containerNameAbove:String = "", containerNameBelow:String = "") extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // Check that the focus object of this step is the same as the focus object of the previous step
    if (gs.getLastSatisfiedObject().isDefined) {
      if (gs.getLastSatisfiedObject().get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Get the object's current temperature
    if (obj.propMaterial.isEmpty) return GoalReturn.mkSubgoalUnsuccessful()
    val objTemp = obj.propMaterial.get.temperatureC

    // Get the object's current container
    val curContainer = obj.getContainer()
    if (curContainer.isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // If the container is not one of the two answer containers, then exit
    if ((curContainer.get.name != containerNameAbove) && (curContainer.get.name != containerNameBelow)) return GoalReturn.mkSubgoalUnsuccessful()

    // Success conditions
    if ((curContainer.get.name == containerNameAbove) && (objTemp >= tempThreshold)) {
      // Above temp container
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()

    }
    if ((curContainer.get.name == containerNameBelow) && (objTemp <= tempThreshold)) {
      // Below temp container
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    }

    // If we reach here, then the object is in one of the two answer containers, but not the correct one.
    return GoalReturn.mkTaskFailure()
  }

}