package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn, GoalSequence}



class GoalActivateDevice(deviceName:String = "", sameAsLastDevice:Boolean = false) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // Check for material properties to be defined
    if (!obj.propDevice.isDefined) return GoalReturn.mkSubgoalUnsuccessful()

    if (obj.name != deviceName) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (sameAsLastDevice) {
      if (gs.getLastSatisfiedObject().isDefined) {
        if (gs.getLastSatisfiedObject().get != obj) return GoalReturn.mkSubgoalUnsuccessful()
      }
    }

    // Check for state of matter to NOT be a specific value
    //println ("obj.propMaterial.get.stateOfMatter: " + obj.propMaterial.get.stateOfMatter)
    if (obj.propDevice.get.isActivated == true) {
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    }
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}


class GoalDeactivateDevice(deviceName:String = "", sameAsLastDevice:Boolean = false) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // Check for material properties to be defined
    if (!obj.propDevice.isDefined) return GoalReturn.mkSubgoalUnsuccessful()

    if (obj.name != deviceName) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (sameAsLastDevice) {
      if (gs.getLastSatisfiedObject().isDefined) {
        if (gs.getLastSatisfiedObject().get != obj) return GoalReturn.mkSubgoalUnsuccessful()
      }
    }

    // Check for state of matter to NOT be a specific value
    //println ("obj.propMaterial.get.stateOfMatter: " + obj.propMaterial.get.stateOfMatter)
    if (obj.propDevice.get.isActivated == false) {
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    }
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}