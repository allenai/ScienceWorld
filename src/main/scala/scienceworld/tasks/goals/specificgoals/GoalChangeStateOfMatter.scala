package scienceworld.tasks.goals.specificgoals

import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.Goal

class GoalChangeStateOfMatter(val changeToState:String) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject):Boolean = {
    // Check for material properties to be defined
    if (!obj.propMaterial.isDefined) return false

    // Check for state of matter to be set to a specific value
    if (obj.propMaterial.get.stateOfMatter == changeToState) {
      return true
    }
    return false
  }

}
