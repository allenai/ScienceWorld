package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.agent.Agent
import scienceworld.objects.livingthing.LivingThing
import scienceworld.objects.misc.InclinedPlane
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn}

// Find an object with a specific name
class GoalFind(objectName:String = "", failIfWrong:Boolean = true, _defocusOnSuccess:Boolean = false) extends Goal {
  this.defocusOnSuccess = _defocusOnSuccess

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal], agent:Agent):GoalReturn = {
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


class GoalFindLivingThingStage(livingThingType:String = "", lifeStage:String = "", failIfWrong:Boolean = true, _defocusOnSuccess:Boolean = false) extends Goal {
  this.defocusOnSuccess = _defocusOnSuccess

  override def isGoalConditionSatisfied(obj: EnvObject, lastGoal: Option[Goal], agent:Agent): GoalReturn = {

    // Case: The focus is on a living thing of the correct species type
    if ((obj.propLife.isDefined) && (obj.propLife.get.lifeformType == livingThingType.toLowerCase)) {

      // Step 2: Check that it's in the correct life cycle stage
      obj match {
        case x:LivingThing => {
          // Check that the object has a Life Cycle defined
          if (x.lifecycle.isDefined) {
            // Check that the life cycle stage is the one we're looking for
            if (x.lifecycle.get.getCurStageName() == lifeStage) {
              this.satisfiedWithObject = Some(obj)
              return GoalReturn.mkSubgoalSuccess()
            }
          }
        }
        case _ => {
          // Do nothing
        }
      }

    }
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


class GoalFindInclinedPlane(surfaceName:String = "", failIfWrong:Boolean = true, _defocusOnSuccess:Boolean = false) extends Goal {
  this.defocusOnSuccess = _defocusOnSuccess

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal], agent:Agent):GoalReturn = {
    if (obj.name.toLowerCase == "inclined plane") {
      obj match {
        case x:InclinedPlane => {
          if (x.surfaceMaterial.substanceName == surfaceName) {
            // Case: The focus is on an object with the correct name
            this.satisfiedWithObject = Some(obj)
            return GoalReturn.mkSubgoalSuccess()
          }
        }
        case _ => { }
      }
    }

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

class GoalFindInclinedPlaneNamed(additionalName:String = "", failIfWrong:Boolean = true, _defocusOnSuccess:Boolean = false) extends Goal {
  this.defocusOnSuccess = _defocusOnSuccess

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal], agent:Agent):GoalReturn = {
    if (obj.name.toLowerCase == "inclined plane") {
      obj match {
        case x:InclinedPlane => {
          if (x.additionalName == additionalName) {
            // Case: The focus is on an object with the correct name
            this.satisfiedWithObject = Some(obj)
            return GoalReturn.mkSubgoalSuccess()
          }
        }
        case _ => { }
      }
    }

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