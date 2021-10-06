package scienceworld.tasks.goals.specificgoals

import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.Goal

class GoalIsNotStateOfMatter(val isNotState:String) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    // Check for material properties to be defined
    if (!obj.propMaterial.isDefined) return false

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return false
    }

    // Check for state of matter to NOT be a specific value
    println ("obj.propMaterial.get.stateOfMatter: " + obj.propMaterial.get.stateOfMatter)
    if (obj.propMaterial.get.stateOfMatter != isNotState) {
      this.satisfiedWithObject = Some(obj)
      return true
    }
    return false
  }

}

class GoalChangeStateOfMatter(val changeToState:String) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    // Check for material properties to be defined
    if (!obj.propMaterial.isDefined) return false

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return false
    }

    // Check for state of matter to be set to a specific value
    if (obj.propMaterial.get.stateOfMatter == changeToState) {
      this.satisfiedWithObject = Some(obj)
      return true
    }
    return false
  }

}
