package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.agent.Agent
import scienceworld.processes.ElectricalConductivity
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn, GoalSequence}

class GoalElectricallyConnected(connectedPartName:String = "", failIfWrong:Boolean = true, _isOptional:Boolean = false, description:String = "", key:String = "", keysMustBeCompletedBefore:Array[String] = Array.empty[String]) extends Goal(description, key, keysMustBeCompletedBefore) {
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    println ("GOAL CHECKING:")

    // Check for a focus object
    if (obj.isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    // Check that the focus object of this step is the same as the focus object of the previous step
    if (gs.getLastSatisfiedObject().isDefined) {
      if (gs.getLastSatisfiedObject().get != obj.get) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Check that the focus object is electrically connected to an object with 'connectedPartName'
    if (ElectricalConductivity.areComponentsElectricallyConnected(obj.get, connectedPartName)) {
      this.satisfiedWithObject = obj
      return GoalReturn.mkSubgoalSuccess()
    } else {
      // Case: The focus object is not electrically connected to an object named 'connectedPartName'
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
