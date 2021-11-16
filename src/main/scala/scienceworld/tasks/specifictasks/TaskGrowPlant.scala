package scienceworld.tasks.specifictasks

//TODO: Only part-way implemented

import scienceworld.environments.ContainerMaker
import scienceworld.objects.agent.Agent
import scienceworld.objects.devices.Stove
import scienceworld.objects.taskitems.AnswerBox
import scienceworld.objects.{AppleJuice, Caesium, Chocolate, Gallium, Ice, IceCream, Lead, Marshmallow, Mercury, OrangeJuice, Soap, Tin}
import scienceworld.processes.lifestage.PlantLifeStages.{PLANT_STAGE_ADULT_PLANT, PLANT_STAGE_REPRODUCING, PLANT_STAGE_SEED, PLANT_STAGE_SEEDLING}
import scienceworld.properties.LeadProp
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskDisable, TaskMaker, TaskMaker1, TaskModifier, TaskObject}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalChangeStateOfMatter, GoalFind, GoalFocusOnAnimal, GoalFocusOnLivingThing, GoalFocusOnNonlivingThing, GoalFocusOnPlant, GoalIsDifferentStateOfMatter, GoalIsNotStateOfMatter, GoalIsStateOfMatter, GoalLifeStage, GoalObjectInContainer}
import scienceworld.tasks.specifictasks.TaskChangeOfState.{MODE_BOIL, MODE_CHANGESTATE, MODE_FREEZE, MODE_MELT}
import scienceworld.tasks.specifictasks.TaskFindLivingNonLiving.{MODE_ANIMAL, MODE_LIVING, MODE_NONLIVING, MODE_PLANT}
import scienceworld.tasks.specifictasks.TaskGrowPlant.{MODE_GROW_FRUIT, MODE_GROW_PLANT}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}


class TaskGrowPlant(val mode:String = MODE_LIVING) extends TaskParametric {
  val taskName = "task-4-" + mode.replaceAll(" ", "-")

  val answerBoxPossibilities = new ArrayBuffer[ Array[TaskModifier] ]()
  val colours = Array("red", "green", "blue", "orange", "yellow", "purple")
  val locations = Array("kitchen", "bathroom", "living room", "bedroom", "workshop")
  for (location <- locations) {
    for (colour <- colours) {
      val answerBox = new AnswerBox(colour)
      answerBoxPossibilities.append(Array(new TaskObject(answerBox.name, Some(answerBox), roomToGenerateIn = location, Array.empty[String], generateNear = 0)))
    }
  }

  // Combinations
  val combinations = for {
    i <- answerBoxPossibilities
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
    // Step 1: Find substance name
    // NOTE: The first modifier here will be the substance to change the state of.
    val answerBoxModifier = modifiers(0)
    var answerBoxName = "<unknown>"
    var answerBoxLocation = "<unknown>"
    answerBoxModifier match {
      case m:TaskObject => {
        answerBoxName = m.name
        answerBoxLocation = m.roomToGenerateIn
      }
      case _ => {
        throw new RuntimeException("ERROR: Unknown task modifier found, where answer box modifier was expected." + answerBoxModifier.toString)
      }
    }

    var subTask = ""
    val gSequence = new ArrayBuffer[Goal]
    var description:String = "<empty>"
    if (mode == MODE_GROW_PLANT) {
      gSequence.append( new GoalLifeStage(lifeStageName = PLANT_STAGE_SEED) )
      gSequence.append( new GoalLifeStage(lifeStageName = PLANT_STAGE_SEEDLING) )
      gSequence.append( new GoalLifeStage(lifeStageName = PLANT_STAGE_ADULT_PLANT) )
      gSequence.append( new GoalLifeStage(lifeStageName = PLANT_STAGE_REPRODUCING) )

      description = "Your task is to grow a plant from seed. First, focus on a seed. Then, make changes to the environment that grow the plant until it reaches the reproduction life stage."

    } else if (mode == MODE_GROW_FRUIT) {
      // TODO: Currently requires all other apples/fruits to be erased from the environment?  (and possibly all other fruiting trees?)
      gSequence.append( new GoalFind("apple") )

      description = "Your task is to grow an apple. This will require growing several plants, and them being crosspollinated to produce fruit. To complete the task, focus on the apple."
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


object TaskGrowPlant {
  val MODE_GROW_PLANT       = "grow plant"
  val MODE_GROW_FRUIT       = "grow fruit"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskGrowPlant(mode = MODE_GROW_PLANT) )
    taskMaker.addTask( new TaskGrowPlant(mode = MODE_GROW_FRUIT) )
  }

}

