package scienceworld.tasks.goals

import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject

import scala.util.control.Breaks._

// Storage class for a single goal
trait Goal {
  var satisfiedWithObject:Option[EnvObject] = None
  var defocusOnSuccess:Boolean = false

  def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal], agent:Agent):GoalReturn = {
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}


// Storage class for a sequence of goals
class GoalSequence(val subgoals:Array[Goal]) {

  var curSubgoalIdx:Int = 0
  var failed:Boolean = false
  this.reset()


  def getCurrentSubgoal():Option[Goal] = {
    if (this.isCompleted()) return None
    return Some(this.subgoals(curSubgoalIdx))
  }

  def getLastSubgoal():Option[Goal] = {
    if (this.curSubgoalIdx == 0) return None
    return Some( this.subgoals(this.curSubgoalIdx-1) )
  }

  // Generate a normalized score (0-1) representing progress on this sequence of goals
  def score():Double = {
    // If the task has failed, return a negative score
    if (this.isFailed()) return -1.0f
    // If the task has not failed, return normally calculated progress score
    return curSubgoalIdx.toDouble / subgoals.length.toDouble
  }

  // Returns true if all the subgoals in this goal sequence are completed
  def isCompleted():Boolean = {
    if (curSubgoalIdx >= subgoals.length) return true
    // Otherwise
    return false
  }

  // Returns true if the goal has failed
  def isFailed():Boolean = {
    return this.failed
  }

  def setFailed() = {
    this.failed = true
  }

  def reset() {
    this.curSubgoalIdx = 0
    this.failed = false
  }

  /*
   * Tick
   */
  // Checks the current subgoal for completeness.  If completed, it increments the subgoals.
  def tick(objMonitor: ObjMonitor, agent:Agent): Unit = {
    while (true) {
      val curSubgoal = this.getCurrentSubgoal()
      var lastSubgoal = this.getLastSubgoal()

      if (!curSubgoal.isDefined) return

      // Check each object in the set of monitored objects to see if it meets a subgoal condition
      var goalReturn:GoalReturn = GoalReturn.mkSubgoalUnsuccessful()
      breakable {
        for (obj <- objMonitor.getMonitoredObjects()) {
          println("Checking obj (" + obj.toStringMinimal() + ") against subgoal " + curSubgoalIdx)
          println("## " + curSubgoal.get.getClass)

          goalReturn = curSubgoal.get.isGoalConditionSatisfied(obj, lastSubgoal, agent)
          if (goalReturn.subgoalSuccess) {
            if (curSubgoal.get.defocusOnSuccess) objMonitor.clearMonitoredObjects()     // Clear focus, if the goal asks to do this
            break()
          }
          if (goalReturn.taskFailure) break()
        }
      }

      print("##>> out of break")

      if (goalReturn.taskFailure) {
        println ("Task failure.")
        this.setFailed()
      }

      if (goalReturn.subgoalSuccess) {
        // Current goal condition is satisfied -- test next goal condition until we find one that we don't satisfy, or complete the list.
        println("Subgoal satisfied.")
        curSubgoalIdx += 1
      } else {
        // Current goal condition not satisfied -- return
        println("Subgoal not satisfied.")
        return
      }

    }
  }

}


// Storage class for return values
class GoalReturn(val subgoalSuccess:Boolean, val taskFailure:Boolean) {

}

object GoalReturn {

  def mkSubgoalSuccess():GoalReturn = {
    new GoalReturn(subgoalSuccess = true, taskFailure = false)
  }

  def mkTaskFailure():GoalReturn = {
    new GoalReturn(subgoalSuccess = false, taskFailure = true)
  }

  def mkSubgoalUnsuccessful():GoalReturn = {
    new GoalReturn(subgoalSuccess = false, taskFailure = false)
  }

}