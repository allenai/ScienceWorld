package scienceworld.tasks.goals.specificgoals

import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn}

// Find an object with a specific name
class GoalFind(objectName:String = "") extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):GoalReturn = {
    if (obj.name.toLowerCase == objectName.toLowerCase) {
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    }
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}
