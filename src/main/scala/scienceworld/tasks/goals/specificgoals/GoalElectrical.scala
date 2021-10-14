package scienceworld.tasks.goals.specificgoals

import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.Goal

class GoalElectrical {

}


class GoalActivateDevice(deviceName:String = "", sameAsLastDevice:Boolean = false) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    // Check for material properties to be defined
    if (!obj.propDevice.isDefined) return false

    if (obj.name != deviceName) return false

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (sameAsLastDevice) {
      if (lastGoal.isDefined) {
        if (lastGoal.get.satisfiedWithObject.get != obj) return false
      }
    }

    // Check for state of matter to NOT be a specific value
    //println ("obj.propMaterial.get.stateOfMatter: " + obj.propMaterial.get.stateOfMatter)
    if (obj.propDevice.get.isActivated == true) {
      this.satisfiedWithObject = Some(obj)
      return true
    }
    return false
  }

}


class GoalDeactivateDevice(deviceName:String = "", sameAsLastDevice:Boolean = false) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    // Check for material properties to be defined
    if (!obj.propDevice.isDefined) return false

    if (obj.name != deviceName) return false

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (sameAsLastDevice) {
      if (lastGoal.isDefined) {
        if (lastGoal.get.satisfiedWithObject.get != obj) return false
      }
    }

    // Check for state of matter to NOT be a specific value
    //println ("obj.propMaterial.get.stateOfMatter: " + obj.propMaterial.get.stateOfMatter)
    if (obj.propDevice.get.isActivated == false) {
      this.satisfiedWithObject = Some(obj)
      return true
    }
    return false
  }

}