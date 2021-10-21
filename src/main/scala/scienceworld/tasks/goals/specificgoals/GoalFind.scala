package scienceworld.tasks.goals.specificgoals

import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.Goal

// Find an object with a specific name
class GoalFind(objectName:String = "") extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    if (obj.name.toLowerCase == objectName.toLowerCase) {
      this.satisfiedWithObject = Some(obj)
      return true
    }
    return false
  }

}
