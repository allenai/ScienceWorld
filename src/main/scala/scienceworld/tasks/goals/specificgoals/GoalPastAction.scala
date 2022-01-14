package scienceworld.tasks.goals.specificgoals

import scienceworld.actions.ActionLookAt
import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn, GoalSequence}

import scala.util.control.Breaks._


// Success if the agent has examined an object with a specific name.
// This is evaluated by checking the agents action history.
class GoalPastActionExamineObject(objectName:String, _isOptional:Boolean = false, description:String = "") extends Goal(description) {
  this.isOptional = _isOptional

  override def isGoalConditionSatisfied(obj:Option[EnvObject], isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    // NOTE: Focus object not required

    val actionHistory = agent.getActionHistory()

    var found = false
    breakable {
      for (action <- actionHistory) {
        action match {
          case a: ActionLookAt => {
            val examinedObj = a.assignments("obj")
            if ((examinedObj.name.toLowerCase == objectName.toLowerCase) || (examinedObj.getDescriptName().toLowerCase == objectName.toLowerCase)) {
              found = true
              break()
            }
          }
          case _ => {
            // Do nothing
          }
        }
      }
    }

    // First initialization: Keep track of starting location
    if (found) {
      return GoalReturn.mkSubgoalSuccess()
    }

    // Otherwise
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}