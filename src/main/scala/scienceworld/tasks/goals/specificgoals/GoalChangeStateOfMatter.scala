package scienceworld.tasks.goals.specificgoals

import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.Goal

/*
 * Goal for changing to a given state of matter
 */
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


/*
 *  Goal for changing to a given state of matter
 */
class GoalIsStateOfMatter() extends Goal {
  var stateOfMatter:String = ""

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    // Check for material properties to be defined
    if (!obj.propMaterial.isDefined) return false

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return false
    }

    // Store the state of matter of this object
    stateOfMatter = obj.propMaterial.get.stateOfMatter
    this.satisfiedWithObject = Some(obj)
    return true
  }

}


class GoalIsDifferentStateOfMatter() extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    // Check for material properties to be defined
    if (!obj.propMaterial.isDefined) return false

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return false
    }

    // Get the state of matter of this object
    val stateOfMatter = obj.propMaterial.get.stateOfMatter

    // Check against the state of matter of the previous object (stored in a GoalIsStateOfMatter)
    lastGoal.get match {
      case x:GoalIsStateOfMatter => if (x.stateOfMatter != stateOfMatter) {
        this.satisfiedWithObject = Some(obj)
        return true
      } else {
        return false
      }
      case _ => return false
    }

    return false
  }

}

