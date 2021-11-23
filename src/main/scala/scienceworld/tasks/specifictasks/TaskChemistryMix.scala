package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.objects.containers.{CeramicCup, FlowerPot}
import scienceworld.objects.livingthing.plant.{Plant, Soil}
import scienceworld.objects.substance.{Soap, SodiumChloride}
import scienceworld.processes.PlantReproduction
import scienceworld.processes.lifestage.PlantLifeStages.{PLANT_STAGE_ADULT_PLANT, PLANT_STAGE_REPRODUCING, PLANT_STAGE_SEED, PLANT_STAGE_SEEDLING}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalFind, GoalLifeStage}
import scienceworld.tasks.specifictasks.TaskChemistryMix.MODE_CHEMISTRY_MIX
import scienceworld.tasks.specifictasks.TaskFindLivingNonLiving.MODE_LIVING

import scala.collection.mutable.ArrayBuffer

class TaskChemistryMix(val mode:String = MODE_LIVING) extends TaskParametric {
  val taskName = "task-5-" + mode.replaceAll(" ", "-")


  // Variation 1: Which seeds to grow
  val baseChemicals = new ArrayBuffer[ Array[TaskModifier] ]()
  val locations = Array("kitchen", "workshop")
  //val locations = Array("green house")
  for (location <- locations) {

    val salt = new SodiumChloride()
    // Other is water
    baseChemicals.append( Array(
      new TaskObject(salt.name, Some(salt), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "result", value = "salt water")
    ))

    val soap = new Soap()
    // Other is water
    baseChemicals.append( Array(
      new TaskObject(soap.name, Some(soap), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "result", value = "soapy water")
    ))

    // TODO: Add more here

  }



  // Combinations
  val combinations = for {
    i <- baseChemicals
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
    val resultChemical = this.getTaskValueStr(modifiers, key = "result")
    if (resultChemical.isEmpty) throw new RuntimeException("ERROR: Failed to find resultant chemical in task setup.")

    var subTask = ""
    val gSequence = new ArrayBuffer[Goal]
    var description:String = "<empty>"
    if (mode == MODE_CHEMISTRY_MIX) {
      gSequence.append( new GoalFind(objectName = resultChemical.get, failIfWrong = true) )

      description = "Your task is to use chemistry to create the substance '" + resultChemical.get + "'. When you are done, focus on the " + resultChemical.get + "."

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


object TaskChemistryMix {
  val MODE_CHEMISTRY_MIX       = "chemistry mix"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskChemistryMix(mode = MODE_CHEMISTRY_MIX) )
  }

}