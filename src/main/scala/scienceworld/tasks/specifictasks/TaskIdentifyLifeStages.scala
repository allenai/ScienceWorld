package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.objects.livingthing.LivingThing
import scienceworld.objects.livingthing.animals.{Ant, Beaver, BrownBear, Chameleon, Chipmunk, Crocodile, Dragonfly, Elephant, GiantTortoise, Hedgehog, Mouse, Parrot, Rabbit, Toad, Wolf}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalFind, GoalFindLivingThingStage}
import scienceworld.tasks.specifictasks.TaskIdentifyLifeStages._

import scala.collection.mutable.ArrayBuffer


class TaskIdentifyLifeStages(val mode:String = MODE_LIFESTAGES) extends TaskParametric {
  val taskName = "task-6-" + mode.replaceAll(" ", "-")

  val locations = Array("outside")

  // Variation 1: Which seeds to grow
  val animalsAndStages = new ArrayBuffer[ Array[TaskModifier] ]()
  for (location <- locations) {

    val elephant = new Elephant()
    val elephantLifeStages = elephant.lifecycle.get.stages.map(_.stageName)
    val lifeStages
    animalsAndStages.append(Array(
        new TaskObject(elephant.name, Some(elephant), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
        new TaskValueStr(key = "animal", value = elephant.name),
        new TaskValueStr(key = "stage1", value = "baby " + elephant.name)      // TODO: Complete this pattern
      ))

  }

  // Combinations
  val combinations = for {
    i <- animalsAndStages
  } yield List(i)

  println("Number of combinations: " + combinations.length)

  def numCombinations():Int = this.combinations.size

  def getCombination(idx:Int):Array[TaskModifier] = {
    val out = new ArrayBuffer[TaskModifier]
    for (elem <- combinations(idx)) {
      out.insertAll(out.length, elem)
    }
    // Return
    out.toArray
  }

  // Setup a particular modifier combination on the universe
  private def setupCombination(modifiers:Array[TaskModifier], universe:EnvObject, agent:Agent):(Boolean, String) = {
    // Run each modifier's change on the universe
    for (mod <- modifiers) {
      println("Running modifier: " + mod.toString)
      val success = mod.runModifier(universe, agent)
      if (!success) {
        return (false, "ERROR: Error running one or more modifiers while setting up task environment.")
      }
    }
    // If we reach here, success
    return (true, "")
  }

  def setupCombination(combinationNum:Int, universe:EnvObject, agent:Agent): (Boolean, String) = {
    if (combinationNum >= this.numCombinations()) {
      return (false, "ERROR: The requested variation (" + combinationNum + ") exceeds the total number of variations (" + this.numCombinations() + ").")
    }
    return this.setupCombination( this.getCombination(combinationNum), universe, agent )
  }


  // Setup a set of subgoals for this task modifier combination.
  private def setupGoals(modifiers:Array[TaskModifier], combinationNum:Int): Task = {
    // Step 1: Find seed type
    val animalName = this.getTaskValueStr(modifiers, "animal")
    if (animalName.isEmpty) throw new RuntimeException("ERROR: Failed to find animal name in task setup.")
    val stage1 = this.getTaskValueStr(modifiers, "stage 1")
    if (stage1.isEmpty) throw new RuntimeException("ERROR: Failed to find lifecycle stage 1 in task setup.")
    val stage2 = this.getTaskValueStr(modifiers, "stage 2")
    if (stage2.isEmpty) throw new RuntimeException("ERROR: Failed to find lifecycle stage 2 in task setup.")
    val stage3 = this.getTaskValueStr(modifiers, "stage 3")
    //if (stage3.isEmpty) throw new RuntimeException("ERROR: Failed to find lifecycle stage 3 in task setup.")
    val stage4 = this.getTaskValueStr(modifiers, "stage 4")
    //if (stage4.isEmpty) throw new RuntimeException("ERROR: Failed to find lifecycle stage 4 in task setup.")
    val stage5 = this.getTaskValueStr(modifiers, "stage 5")
    //if (stage5.isEmpty) throw new RuntimeException("ERROR: Failed to find lifecycle stage 5 in task setup.")


    val gSequence = new ArrayBuffer[Goal]
    var description:String = "<empty>"
    if (mode == MODE_LIFESTAGES) {

      gSequence.append(new GoalFindLivingThingStage(livingThingType = animalName.get, lifeStage = stage1.get, failIfWrong = true, _defocusOnSuccess = true))
      if (stage2.isDefined) gSequence.append(new GoalFindLivingThingStage(livingThingType = animalName.get, lifeStage = stage2.get, failIfWrong = true, _defocusOnSuccess = true))
      if (stage3.isDefined) gSequence.append(new GoalFindLivingThingStage(livingThingType = animalName.get, lifeStage = stage3.get, failIfWrong = true, _defocusOnSuccess = true))
      if (stage4.isDefined) gSequence.append(new GoalFindLivingThingStage(livingThingType = animalName.get, lifeStage = stage4.get, failIfWrong = true, _defocusOnSuccess = true))
      if (stage5.isDefined) gSequence.append(new GoalFindLivingThingStage(livingThingType = animalName.get, lifeStage = stage5.get, failIfWrong = true, _defocusOnSuccess = true))

      description = "Your task is to focus on the life stages of the " + animalName.get + ", starting from earliest to latest." // TODO: Better description?

    } else {
      throw new RuntimeException("ERROR: Unrecognized task mode: " + mode)
    }

    val taskLabel = taskName + "-variation" + combinationNum
    //val description = "Your task is to find a " + subTask + ". First, focus on the thing. Then, move it to the " + answerBoxName + " in the " + answerBoxLocation + "."
    val goalSequence = new GoalSequence(gSequence.toArray)

    val task = new Task(taskName, description, goalSequence)

    // Return
    return task
  }

  def setupGoals(combinationNum:Int): Task = {
    this.setupGoals( this.getCombination(combinationNum), combinationNum )
  }




}


object TaskIdentifyLifeStages {
  val MODE_LIFESTAGES       = "identify life stages"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskIdentifyLifeStages(mode = MODE_LIFESTAGES) )
  }


  /*
   * Helper functinos
   */
  def mkTaskVariation(livingThing: LivingThing, location: String): Unit = {

    // Get living thing life stages
    var lifestages = livingThing.lifecycle.get.stages.map(_.stageName)
    // Remove the last life stage, since it should always assumed to be death
    lifestages = lifestages.slice(0, lifestages.size - 1)
    // Create array of stages
    val stageKeys = new ArrayBuffer[TaskModifier]
    for (i <- 0 until lifestages.length) {
      val lifestageName = lifestages(i)
      stageKeys.append(new TaskValueStr(key = "stage" + i, value = lifestageName))
    }

    // Create task modifier
    val out = Array(new TaskObject(livingThing.name, Some(livingThing), roomToGenerateIn = location, Array.empty[String], generateNear = 0)) ++ stageKeys.toArray

    return out
  }

}
