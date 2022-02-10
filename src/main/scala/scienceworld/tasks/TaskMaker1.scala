package scienceworld.tasks

import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.tasks.specifictasks.{TaskChangeOfState, TaskChemistryMix, TaskChemistryMixPaint, TaskElectricCircuit, TaskElectricalConductivity, TaskElectricalConductivity2, TaskFindLivingNonLiving, TaskGrowPlant, TaskIdentifyLifeStages1, TaskIdentifyLifeStages2, TaskInclinedPlane1, TaskInclinedPlane2, TaskInclinedPlane3, TaskLifeSpan, TaskMendelialGenetics2, TaskMendelianGenetics1, TaskParametric, TaskUseInstrumentThermometer, TaskUseInstrumentThermometer2, TaskUseInstrumentThermometer3}

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
  def doTaskSetup(taskName:String, variationIdx:Int, universe:EnvObject, agent:Agent):(Option[Task], Array[String], String) = {
    val tp = this.getTask(taskName)
    if (tp.isEmpty) return (None, Array.empty[String], "ERROR: Unknown task (" + taskName + ").")

    // First, setup environment
    val (success, errStr) = tp.get.setupCombination(variationIdx, universe, agent)
    if (!success) return (None, Array.empty[String], errStr)

    // Then, get task
    val task = tp.get.setupGoals(variationIdx)

    // Then, get gold action sequence
    val (goldSuccess, goldActions, goldActionStr) = tp.get.mkGoldActionSequence(modifiers = task.taskModifiers, universe, agent)
    if (!goldSuccess) return (None, Array.empty[String], "ERROR: Could not generate gold action sequence.")

    // Return
    (Some(task), goldActionStr, "")
  }

  /*
   * Register tasks
   */
  // TODO: Add list of tasks here
  private def registerTasks(): Unit = {
    TaskChangeOfState.registerTasks(this)
    TaskElectricCircuit.registerTasks(this)
    TaskElectricalConductivity.registerTasks(this)
    TaskElectricalConductivity2.registerTasks(this)
    TaskFindLivingNonLiving.registerTasks(this)
    TaskGrowPlant.registerTasks(this)
    TaskChemistryMix.registerTasks(this)
    TaskChemistryMixPaint.registerTasks(this)
    TaskLifeSpan.registerTasks(this)
    TaskIdentifyLifeStages1.registerTasks(this)
    TaskIdentifyLifeStages2.registerTasks(this)
    TaskInclinedPlane1.registerTasks(this)
    TaskInclinedPlane2.registerTasks(this)
    TaskInclinedPlane3.registerTasks(this)
    TaskMendelianGenetics1.registerTasks(this)
    TaskMendelialGenetics2.registerTasks(this)
    TaskUseInstrumentThermometer.registerTasks(this)
    TaskUseInstrumentThermometer2.registerTasks(this)
    TaskUseInstrumentThermometer3.registerTasks(this)
  }


  /*
   * String methods
   */

}


object TaskMaker1 {

}
