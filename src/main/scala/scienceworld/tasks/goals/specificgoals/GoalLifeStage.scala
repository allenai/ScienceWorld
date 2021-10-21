package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.livingthing.LivingThing
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.Goal

// Object must be in the container
class GoalLifeStage(lifeStageName:String = "", sameAsLastObj:Boolean = true) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    // Check that the focus object of this step is the same as the focus object of the previous step
    if (sameAsLastObj && lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return false
    }

    // Check that the object is in a given life stage
    obj match {
      case x:LivingThing => {
        if (x.lifecycle.isDefined) {
          val curLifeStage = x.lifecycle.get.getCurStageName()
          if (curLifeStage.toLowerCase == lifeStageName.toLowerCase) {
            // If we reach here, the condition is satisfied
            this.satisfiedWithObject = Some(obj)
            return true
          } else {
            return false
          }
        }
      }
      case _ => {
        return false
      }
    }

    return false
  }

}
