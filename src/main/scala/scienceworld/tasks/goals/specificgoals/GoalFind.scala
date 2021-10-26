package scienceworld.tasks.goals.specificgoals

import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn}

// Find an object with a specific name
class GoalFind(objectName:String = "", failIfWrong:Boolean = true) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):GoalReturn = {
    if (obj.name.toLowerCase == objectName.toLowerCase) {
      // Case: The focus is on an object with the correct name
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    } else {
      // Case: The focus is on an object with a different name
      if (failIfWrong) {
        // Return: Task failure
        return GoalReturn.mkTaskFailure()
      } else {
        // Return: Subgoal not passed
        return GoalReturn.mkSubgoalUnsuccessful()
      }
    }

  }

}
