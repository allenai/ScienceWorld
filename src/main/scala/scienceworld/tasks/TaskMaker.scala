package scienceworld.tasks

import scienceworld.tasks.goals.GoalSequence
import scienceworld.tasks.goals.specificgoals.{GoalActivateDevice, GoalChangeStateOfMatter, GoalIsDifferentStateOfMatter, GoalIsNotStateOfMatter, GoalIsStateOfMatter}

import scala.collection.mutable
import scala.util.Random

class TaskMaker {

}


object TaskMaker {
  val tasks = mutable.Map[String, Task]()
  // Constructor
  this.registerTasks()


  /*
   *  Changes of state
   */

  // Test goal sequence: Change the state of some matter into any other state
  def mkTaskChangeOfState():Task = {
    val taskName = "task-1-any"
    val description = "Your task is to change the state of matter of a substance.  First, focus on a substance.  Then, make changes that will cause it to change its state of matter.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalIsStateOfMatter(),              // Be in any state
      new GoalIsDifferentStateOfMatter()      // Be in any state but the first state
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }

  // Test goal sequence: Change the state of some matter into gas
  def mkTaskChangeOfStateSolid():Task = {
    val taskName = "task-1-freeze"
    val description = "Your task is to freeze a substance.  First, focus on a substance that is in liquid or gas form (e.g. 'focus <substanceName>').  Then, make changes to the environment that will cause it to boil.  When the substance changes to a solid state, the score will switch to 1.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalIsNotStateOfMatter("solid"),              // Be in any state but a solid
      new GoalChangeStateOfMatter("solid")          // Be in the solid state
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }

  // Test goal sequence: Change the state of some matter into gas
  def mkTaskChangeOfStateLiquid():Task = {
    val taskName = "task-1-melt"
    val description = "Your task is to melt or condense a substance.  First, focus on a substance that is in solid or gas form (e.g. 'focus <substanceName>').  Then, make changes to the environment that will cause it to melt or condense.  When the substance changes to a liquid state, the score will switch to 1.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalIsNotStateOfMatter("liquid"),              // Be in any state but a liquid
      new GoalChangeStateOfMatter("liquid")          // Be in the liquid state
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }

  // Test goal sequence: Change the state of some matter into gas
  def mkTaskChangeOfStateGas():Task = {
    val taskName = "task-1-boil"
    val description = "Your task is to boil a substance.  First, focus on a substance that is in solid or liquid form (e.g. 'focus <substanceName>').  Then, make changes to the environment that will cause it to boil.  When the substance changes to a gas state, the score will switch to 1.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalIsNotStateOfMatter("gas"),              // Be in any state but a gas
      new GoalChangeStateOfMatter("gas")          // Be in the gas state
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }



  /*
   * Electrical
   */
  def mkTaskTurnOnLightbulb():Task = {
    val taskName = "task-2-lightbulb"
    val description = "Your task is to turn on light bulb 1.  First, focus on light bulb 1, which is in the workshop.  Then, create an electrical circuit that powers it on.  When the light bulb is on, the score will switch to 1.  To reset, type 'reset task'. "

    val goalSequence = new GoalSequence(Array(
      new GoalActivateDevice(deviceName = "light bulb 1"),              // Be in any state but a gas
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }



  /*
   * Helper functions
   */

  def getAllTaskNames():Array[String] = {
    tasks.map(_._1).toArray
  }

  // Add a task
  def addTask(task:Task): Unit = {
    tasks(task.taskName) = task
  }

  // Get a task
  def getTask(taskName:String):Option[Task] = {
    if (!tasks.contains(taskName)) return None
    return Some(tasks(taskName))
  }

  // Get a random task
  def getRandomTask():Task = {
    val taskNames = tasks.keySet.toArray
    val randIdx = Random.nextInt( taskNames.size )
    return tasks( taskNames(randIdx) )
  }

  // Register Tasks
  def registerTasks(): Unit = {
    this.addTask( mkTaskChangeOfState() )
    this.addTask( mkTaskChangeOfStateSolid() )
    this.addTask( mkTaskChangeOfStateLiquid() )
    this.addTask( mkTaskChangeOfStateGas() )

    this.addTask( mkTaskTurnOnLightbulb() )
  }


}