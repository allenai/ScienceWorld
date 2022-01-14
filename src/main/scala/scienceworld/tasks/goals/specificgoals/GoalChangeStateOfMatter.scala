package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn, GoalSequence}

/*
 * Goal for changing to a given state of matter
 */
class GoalIsNotStateOfMatter(val isNotState:String, _isOptional:Boolean = false, description:String = "", key:String = "", keysMustBeCompletedBefore:Array[String] = Array.empty[String]) extends Goal(description, key, keysMustBeCompletedBefore) {
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // Check for a focus object
    if (obj.isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // Check for material properties to be defined
    if (!obj.get.propMaterial.isDefined) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (gs.getLastSatisfiedObject().isDefined) {
      if (gs.getLastSatisfiedObject().get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Check for state of matter to NOT be a specific value
    println ("obj.propMaterial.get.stateOfMatter: " + obj.get.propMaterial.get.stateOfMatter)
    if (obj.get.propMaterial.get.stateOfMatter != isNotState) {
      this.satisfiedWithObject = obj
      return GoalReturn.mkSubgoalSuccess()
    }
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}

class GoalChangeStateOfMatter(val changeToState:String, _isOptional:Boolean = false, description:String = "", key:String = "", keysMustBeCompletedBefore:Array[String] = Array.empty[String]) extends Goal(description, key, keysMustBeCompletedBefore) {
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // Check for a focus object
    if (obj.isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // Check for material properties to be defined
    if (!obj.get.propMaterial.isDefined) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (gs.getLastSatisfiedObject().isDefined) {
      if (gs.getLastSatisfiedObject().get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Check for state of matter to be set to a specific value
    if (obj.get.propMaterial.get.stateOfMatter == changeToState) {
      this.satisfiedWithObject = obj
      return GoalReturn.mkSubgoalSuccess()
    }
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}


/*
 *  Goal for changing to a given state of matter
 */
class GoalIsStateOfMatter(_isOptional:Boolean = false, description:String = "", key:String = "", keysMustBeCompletedBefore:Array[String] = Array.empty[String]) extends Goal(description, key, keysMustBeCompletedBefore) {
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // Check for a focus object
    if (obj.isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // Check for material properties to be defined
    if (!obj.get.propMaterial.isDefined) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (gs.getLastSatisfiedObject().isDefined) {
      if (gs.getLastSatisfiedObject().get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Store the state of matter of this object
    gs.setKey("stateOfMatter", obj.get.propMaterial.get.stateOfMatter)
    this.satisfiedWithObject = obj

    return GoalReturn.mkSubgoalSuccess()
  }

}


class GoalIsDifferentStateOfMatter(_isOptional:Boolean = false, description:String = "", key:String = "", keysMustBeCompletedBefore:Array[String] = Array.empty[String]) extends Goal(description, key, keysMustBeCompletedBefore) {
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // Check for a focus object
    if (obj.isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // Check for material properties to be defined
    if (!obj.get.propMaterial.isDefined) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (gs.getLastSatisfiedObject().isDefined) {
      if (gs.getLastSatisfiedObject().get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Get the state of matter of this object
    val stateOfMatter = obj.get.propMaterial.get.stateOfMatter

    // Check against the state of matter of the previous object (stored in 'stateOfMatter' key)
    val lastStateOfMatter = gs.getKey("stateOfMatter")
    if (lastStateOfMatter.length == 0) GoalReturn.mkSubgoalUnsuccessful()     // key not present

    if (lastStateOfMatter == stateOfMatter) {                                 // key present
      this.satisfiedWithObject = obj
      return GoalReturn.mkSubgoalSuccess()
    }

    return GoalReturn.mkSubgoalUnsuccessful()
  }

}

