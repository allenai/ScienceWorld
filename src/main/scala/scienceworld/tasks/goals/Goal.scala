package scienceworld.tasks.goals

import scienceworld.struct.EnvObject

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
  def tick(): Unit = {
    while (true) {
      val curSubgoal = this.getCurrentSubgoal()
      if (curSubgoal.isDefined) {
        val isConditionSatisfied = curSubgoal.get.isGoalConditionSatisfied()      //## TODO: Add object monitor
        if (isConditionSatisfied) {
          // Current goal condition is satisfied -- test next goal condition until we find one that we don't satisfy, or complete the list.
          curSubgoalIdx += 1
        } else {
          // Current goal condition not satisfied -- return
          return
        }
      }
    }
  }

}
