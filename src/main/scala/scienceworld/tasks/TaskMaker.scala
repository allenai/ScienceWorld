package scienceworld.tasks

import scienceworld.tasks.goals.GoalSequence
import scienceworld.tasks.goals.specificgoals.{GoalActivateDevice, GoalChangeStateOfMatter, GoalFind, GoalFocusOnAnimal, GoalFocusOnLivingThing, GoalFocusOnNonlivingThing, GoalFocusOnPlant, GoalIsDifferentStateOfMatter, GoalIsNotStateOfMatter, GoalIsStateOfMatter, GoalLifeStage, GoalObjectInContainer}
import scienceworld.processes.lifestage.PlantLifeStages._

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
    val taskName = "task-1a-any-change-of-state"
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
    val taskName = "task-1b-freeze"
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
    val taskName = "task-1c-melt"
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
    val taskName = "task-1d-boil"
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
    val taskName = "task-2a-circuit-lightbulb"
    val description = "Your task is to turn on light bulb 1.  First, focus on light bulb 1, which is in the workshop.  Then, create an electrical circuit that powers it on.  When the light bulb is on, the score will switch to 1.  To reset, type 'reset task'. "

    val goalSequence = new GoalSequence(Array(
      new GoalActivateDevice(deviceName = "light bulb 1"),              // Be in any state but a gas
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }


  /*
   * Living things (categorization)
   */
  // Test goal sequence: Change the state of some matter into gas
  def mkTaskFindLivingThing():Task = {
    val taskName = "task-3a-find-living-thing"
    val description = "Your task is to find a living thing.  First, focus on a living thing.  Then, move that living thing into the blue box.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalFocusOnLivingThing(),                             // Focus on a living thing
      new GoalObjectInContainer(containerName = "blue box")     // Move it into the blue box
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }

  // Test goal sequence: Change the state of some matter into gas
  def mkTaskFindNonlivingThing():Task = {
    val taskName = "task-3b-find-nonliving-thing"
    val description = "Your task is to find a non-living thing.  First, focus on a non-living thing.  Then, move that non-living thing into the blue box.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalFocusOnNonlivingThing(),                             // Focus on a living thing
      new GoalObjectInContainer(containerName = "blue box")     // Move it into the blue box
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }


  // Test goal sequence: Change the state of some matter into gas
  def mkTaskFindAnimal():Task = {
    val taskName = "task-3c-find-animal"
    val description = "Your task is to find an animal.  First, focus on an animal.  Then, move that animal into the blue box.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalFocusOnAnimal(),                             // Focus on a living thing
      new GoalObjectInContainer(containerName = "blue box")     // Move it into the blue box
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }

  // Test goal sequence: Change the state of some matter into gas
  def mkTaskFindPlant():Task = {
    val taskName = "task-3d-find-plant"
    val description = "Your task is to find a plant.  First, focus on a plant.  Then, move that plant the blue box.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalFocusOnPlant(),                             // Focus on a living thing
      new GoalObjectInContainer(containerName = "blue box")     // Move it into the blue box
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }


  /*
   * Living things (grow a plant from seed)
   */
  // Test goal sequence: Change the state of some matter into gas
  def mkTaskGrowPlant():Task = {
    val taskName = "task-4a-grow-plant"
    val description = "Your task is to grow a plant from seed. First, focus on a seed.  Then, make changes to the environment that grow the plant until it reaches the reproduction life stage.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalLifeStage(lifeStageName = PLANT_STAGE_SEED),
      new GoalLifeStage(lifeStageName = PLANT_STAGE_SEEDLING),
      new GoalLifeStage(lifeStageName = PLANT_STAGE_ADULT_PLANT),
      new GoalLifeStage(lifeStageName = PLANT_STAGE_REPRODUCING)
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }

  /*
   * Living things (grow a fruit from seed)
   */
  def mkTaskGrowFruit():Task = {
    val taskName = "task-4b-grow-fruit-pollinator"
    val description = "Your task is to grow an apple.  This will require growing several plants, and them being crosspollinated to produce fruit.  To complete the ask, focus on the apple.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalFind("apple")
    ))

    // Return
    new Task(taskName, description, goalSequence)
  }


  /*
   * Helper functions
   */

  def getAllTaskNames():Array[String] = {
    tasks.map(_._1).toArray.sorted
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

    this.addTask( mkTaskFindLivingThing() )
    this.addTask( mkTaskFindNonlivingThing() )
    this.addTask( mkTaskFindAnimal() )
    this.addTask( mkTaskFindPlant() )

    this.addTask( mkTaskGrowPlant() )
    this.addTask( mkTaskGrowFruit() )

  }


}