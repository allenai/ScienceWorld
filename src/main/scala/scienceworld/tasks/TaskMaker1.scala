package scienceworld.tasks

import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.tasks.specifictasks.{TaskChangeOfState, TaskFindLivingNonLiving, TaskGrowPlant, TaskParametric}

import scala.collection.mutable

class TaskMaker1 {
  val tasks = mutable.Map[String, TaskParametric]()
  // Constructor
  this.registerTasks()

  /*
   * Accessors
   */

  def addTask(parametricTask:TaskParametric) {
    val taskName = parametricTask.taskName
    tasks(taskName) = parametricTask
  }

  def getTask(taskName:String):Option[TaskParametric] = {
    if (this.tasks.contains(taskName)) {
      return Some(this.tasks(taskName))
    }
    // Default return
    None
  }

  def getTaskList():Array[String] = {
    return tasks.keySet.toArray.sorted
  }

  def getMaxVariations(taskName:String):Int = {
    val task = this.getTask(taskName)
    // If the task is unknown, return -1
    if (task.isEmpty) return -1
    // Return number of valid combinations
    return task.get.numCombinations()
  }


  /*
   * Setup
   */
  def doTaskSetup(taskName:String, variationIdx:Int, universe:EnvObject, agent:Agent):(Option[Task], String) = {
    val tp = this.getTask(taskName)
    if (tp.isEmpty) return (None, "ERROR: Unknown task (" + taskName + ").")

    // First, setup environment
    val (success, errStr) = tp.get.setupCombination(variationIdx, universe, agent)
    if (!success) return (None, errStr)

    // Then, get task
    val task = tp.get.setupGoals(variationIdx)

    // Return
    (Some(task), "")
  }

  /*
   * Register tasks
   */
  // TODO: Add list of tasks here
  private def registerTasks(): Unit = {
    TaskChangeOfState.registerTasks(this)
    TaskFindLivingNonLiving.registerTasks(this)
    TaskGrowPlant.registerTasks(this)
  }


  /*
   * String methods
   */

}


object TaskMaker1 {

}
