package scienceworld.tasks.goals

import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject

import scala.collection.mutable
import scala.util.control.Breaks._

// Storage class for a single goal
trait Goal {
  var satisfiedWithObject:Option[EnvObject] = None
  var defocusOnSuccess:Boolean = false
  var isOptional:Boolean = false

  def isGoalConditionSatisfied(obj:EnvObject, isFirstGoal:Boolean, gs:GoalSequence, agent:Agent):GoalReturn = {
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}


// Storage class for a sequence of goals
class GoalSequence(val subgoals:Array[Goal]) {

  var curSubgoalIdx:Int = 0
  var failed:Boolean = false
  var lastSatisfiedWithObject:Option[EnvObject] = None
  val subgoalsCompleted = Array.fill[Boolean](subgoals.length)(false)
  val storedValues = mutable.Map[String, String]()
  this.reset()

  /*
   * Subgoals
   */
  def getCurrentSubgoal():Option[Goal] = {
    if (this.isCompleted()) return None
    return Some(this.subgoals(curSubgoalIdx))
  }

  def getSubgoalAtIdx(idx:Int):Option[Goal] = {
    if (this.isCompleted()) return None
    if ((idx < 0) || (idx > this.subgoals.length)) return None
    return Some( this.subgoals(idx) )
  }

  def getLastSubgoal():Option[Goal] = {
    if (this.curSubgoalIdx == 0) return None
    return Some( this.subgoals(this.curSubgoalIdx-1) )
  }

  def getLastSatisfiedObject():Option[EnvObject] = {
    return this.lastSatisfiedWithObject
  }

  def getNumCompletedSubgoals():Int = {
    var sum:Int = 0
    for (sgc <- subgoalsCompleted) {
      if (sgc == true) sum += 1
    }
    return sum
  }

  /*
   * Keys
   */
  def setKey(key:String, value:String): Unit = {
    storedValues(key) = value
  }

  def getKey(key:String):String = {
    if (!storedValues.contains(key)) return ""
    return storedValues(key)
  }

  /*
   * Scoring
   */

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
    this.lastSatisfiedWithObject = None
    this.failed = false
  }

  /*
   * Tick
   */
  // Checks the current subgoal for completeness.  If completed, it increments the subgoals.
  def tick(objMonitor: ObjMonitor, agent:Agent): Unit = {
    var firstSubgoalIdx = -1

    while (true) {
      var curSubgoal = this.getCurrentSubgoal()
      var subgoalIdx = this.curSubgoalIdx
      if (firstSubgoalIdx == -1) firstSubgoalIdx = subgoalIdx

      if (curSubgoal.isEmpty) return

      // Check each object in the set of monitored objects to see if it meets a subgoal condition
      var goalReturn:GoalReturn = GoalReturn.mkSubgoalUnsuccessful()
      breakable {
        while (true) {
          for (obj <- objMonitor.getMonitoredObjects()) {
            println("Checking obj (" + obj.toStringMinimal() + ") against subgoal " + curSubgoalIdx)
            println("## " + curSubgoal.get.getClass)

            val isFirstGoal = if (this.getNumCompletedSubgoals() == 0) true else false

            goalReturn = curSubgoal.get.isGoalConditionSatisfied(obj, isFirstGoal, this, agent)
            if (goalReturn.subgoalSuccess) {
              if (curSubgoal.get.satisfiedWithObject != None) this.lastSatisfiedWithObject = curSubgoal.get.satisfiedWithObject
              if (curSubgoal.get.defocusOnSuccess) objMonitor.clearMonitoredObjects() // Clear focus, if the goal asks to do this
              break()
            }
            if (goalReturn.taskFailure) break()
          }

          println("## No success or failure")

          // If we reach here, the subgoal hasn't been satisfied, and the task hasn't failed -- check if the subgoal is optional
          if (curSubgoal.get.isOptional) {
            println("curSubgoal: " + curSubgoal)
            println("curSubgoalIdx: " + curSubgoalIdx)
            curSubgoal = getSubgoalAtIdx(subgoalIdx + 1)
            if (curSubgoal.isEmpty) break()   // This condition should only happen if there are no more subgoals left (e.g. at end, or isComplete=True)
            subgoalIdx += 1
          } else {
            // If the subgoal isn't optional, then break
            break()
          }
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
        //curSubgoalIdx += 1
        this.curSubgoalIdx = subgoalIdx + 1
      } else {
        // Current goal condition not satisfied -- return
        println("Subgoal not satisfied.")
        return
      }

      // If we're not making progress solving progressive subgoals, break
      if (firstSubgoalIdx == this.curSubgoalIdx) return
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