package scienceworld.tasks.goals

import scienceworld.struct.EnvObject
import scala.util.control.Breaks._

// Storage class for a single goal
trait Goal {

  def isGoalConditionSatisfied(obj:EnvObject):Boolean = {
    return false
  }

}


// Storage class for a sequence of goals
class GoalSequence(val subgoals:Array[Goal]) {

  var curSubgoalIdx:Int = 0

  def getCurrentSubgoal():Option[Goal] = {
    if (this.isCompleted()) return None
    return Some(this.subgoals(curSubgoalIdx))
  }

  // Generate a normalized score (0-1) representing progress on this sequence of goals
  def score():Double = {
    return curSubgoalIdx.toDouble / subgoals.length.toDouble
  }

  // Returns true if all the subgoals in this goal sequence are completed
  def isCompleted():Boolean = {
    if (curSubgoalIdx >= subgoals.length) return true
    // Otherwise
    return false
  }

  /*
   * Tick
   */
  // Checks the current subgoal for completeness.  If completed, it increments the subgoals.
  def tick(objMonitor: ObjMonitor): Unit = {
    while (true) {
      val curSubgoal = this.getCurrentSubgoal()
      if (!curSubgoal.isDefined) return

      // Check each object in the set of monitored objects to see if it meets a subgoal condition
      var isConditionSatisfied: Boolean = false
      breakable {
        for (obj <- objMonitor.getMonitoredObjects()) {
          println("Checking obj (" + obj.toStringMinimal() + ") against subgoal " + curSubgoalIdx)
          isConditionSatisfied = curSubgoal.get.isGoalConditionSatisfied(obj)
          if (isConditionSatisfied) break()
        }
      }

      if (isConditionSatisfied) {
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
