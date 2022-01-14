package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.agent.Agent
import scienceworld.objects.livingthing.LivingThing
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn, GoalSequence}

// Object must be in the container
class GoalLifeStage(lifeFormType:String = "", lifeStageName:String = "", sameAsLastObj:Boolean = true, _isOptional:Boolean = false, description:String = "", key:String = "", keysMustBeCompletedBefore:Array[String] = Array.empty[String]) extends Goal(description, key, keysMustBeCompletedBefore) {
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // Check for a focus object
    if (obj.isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (sameAsLastObj) {
      if (gs.getLastSatisfiedObject().isDefined) {
        if (gs.getLastSatisfiedObject().get != obj) return GoalReturn.mkSubgoalUnsuccessful()
      }
    }

    // Check that this is the correct life form type.  (Note, disabled if lifeformType is empty string)
    if (lifeFormType.length > 0) {
      if ((obj.get.propLife.isDefined) && (obj.get.propLife.get.lifeformType != lifeFormType)) {
        return GoalReturn.mkSubgoalUnsuccessful()
      }
    }

    // Check that the object is in a given life stage
    obj.get match {
      case x:LivingThing => {
        if (x.lifecycle.isDefined) {
          val curLifeStage = x.lifecycle.get.getCurStageName()
          if (curLifeStage.toLowerCase == lifeStageName.toLowerCase) {
            // If we reach here, the condition is satisfied
            this.satisfiedWithObject = obj
            return GoalReturn.mkSubgoalSuccess()
          } else {
            return GoalReturn.mkSubgoalUnsuccessful()
          }
        }
      }
      case _ => {
        return GoalReturn.mkSubgoalUnsuccessful()
      }
    }

    return GoalReturn.mkSubgoalUnsuccessful()
  }

}
