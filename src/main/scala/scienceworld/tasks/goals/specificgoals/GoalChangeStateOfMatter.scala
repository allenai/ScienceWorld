package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn}

/*
 * Goal for changing to a given state of matter
 */
class GoalIsNotStateOfMatter(val isNotState:String) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal], agent:Agent):GoalReturn = {
    // Check for material properties to be defined
    if (!obj.propMaterial.isDefined) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Check for state of matter to NOT be a specific value
    println ("obj.propMaterial.get.stateOfMatter: " + obj.propMaterial.get.stateOfMatter)
    if (obj.propMaterial.get.stateOfMatter != isNotState) {
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    }
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}

class GoalChangeStateOfMatter(val changeToState:String) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal], agent:Agent):GoalReturn = {
    // Check for material properties to be defined
    if (!obj.propMaterial.isDefined) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Check for state of matter to be set to a specific value
    if (obj.propMaterial.get.stateOfMatter == changeToState) {
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    }
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}


/*
 *  Goal for changing to a given state of matter
 */
class GoalIsStateOfMatter() extends Goal {
  var stateOfMatter:String = ""

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal], agent:Agent):GoalReturn = {
    // Check for material properties to be defined
    if (!obj.propMaterial.isDefined) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Store the state of matter of this object
    stateOfMatter = obj.propMaterial.get.stateOfMatter
    this.satisfiedWithObject = Some(obj)
    return GoalReturn.mkSubgoalSuccess()
  }

}


class GoalIsDifferentStateOfMatter() extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal], agent:Agent):GoalReturn = {
    // Check for material properties to be defined
    if (!obj.propMaterial.isDefined) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Get the state of matter of this object
    val stateOfMatter = obj.propMaterial.get.stateOfMatter

    // Check against the state of matter of the previous object (stored in a GoalIsStateOfMatter)
    lastGoal.get match {
      case x:GoalIsStateOfMatter => if (x.stateOfMatter != stateOfMatter) {
        this.satisfiedWithObject = Some(obj)
        return GoalReturn.mkSubgoalSuccess()
      } else {
        return GoalReturn.mkSubgoalUnsuccessful()
      }
      case _ => return GoalReturn.mkSubgoalUnsuccessful()
    }

    return GoalReturn.mkSubgoalUnsuccessful()
  }

}

