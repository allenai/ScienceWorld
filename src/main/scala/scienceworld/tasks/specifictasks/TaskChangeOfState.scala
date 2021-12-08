package scienceworld.tasks.specifictasks

import scienceworld.environments.ContainerMaker
import scienceworld.objects.agent.Agent
import scienceworld.objects.devices.Stove
import scienceworld.objects.substance.food.{AppleJuice, Chocolate, IceCream, Marshmallow, OrangeJuice}
import scienceworld.objects.substance.{Caesium, Gallium, Ice, Lead, Mercury, Soap, Tin}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskDisable, TaskMaker1, TaskModifier, TaskObject}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalChangeStateOfMatter, GoalIsDifferentStateOfMatter, GoalIsNotStateOfMatter, GoalIsStateOfMatter}
import scienceworld.tasks.specifictasks.TaskChangeOfState.{MODE_BOIL, MODE_CHANGESTATE, MODE_FREEZE, MODE_MELT}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}


class TaskChangeOfState(val mode:String = MODE_CHANGESTATE) extends TaskParametric {
  val taskName = "task-1-" + mode.replaceAll(" ", "-")

  val substancePossibilities = new ArrayBuffer[ Array[TaskModifier] ]()
  // Example of water (found in the environment)
  substancePossibilities.append( Array(new TaskObject("water", None, "", Array.empty[String], 0) ))

  // Water, but disable the common source (the sink in the kitchen)
  substancePossibilities.append( Array(new TaskObject("water", None, "", Array.empty[String], 0),
    new TaskDisable(name="sink", Array("kitchen") )))

  // Example of ice (needs to be generated)
  substancePossibilities.append( Array(new TaskObject("ice", Some(new Ice), "kitchen", Array("freezer"), 0) ))
  // Example of something needing to be generated
  substancePossibilities.append( Array(new TaskObject("orange juice", Some(ContainerMaker.mkRandomLiquidCup(new OrangeJuice)), "kitchen", Array("fridge"), 0) ))
  substancePossibilities.append( Array(new TaskObject("apple juice", Some(ContainerMaker.mkRandomLiquidCup(new AppleJuice)), "kitchen", Array("fridge"), 0) ))
  substancePossibilities.append( Array(new TaskObject("chocolate", Some(new Chocolate), "kitchen", Array("fridge"), 0) ))
  substancePossibilities.append( Array(new TaskObject("marshmallow", Some(new Marshmallow), roomToGenerateIn = "kitchen", Array("cupboard", "table", "desk"), generateNear = 0) ))
  substancePossibilities.append( Array(new TaskObject("ice cream", Some(ContainerMaker.mkRandomLiquidCup(new IceCream)), roomToGenerateIn = "kitchen", Array("freezer"), generateNear = 0) ))

  substancePossibilities.append( Array(new TaskObject("soap", Some(new Soap), roomToGenerateIn = "kitchen", Array("cupboard", "table", "desk"), generateNear = 0) ))
  substancePossibilities.append( Array(new TaskObject("rubber", Some(new Soap), roomToGenerateIn = "workshop", Array("table", "desk"), generateNear = 0) ))

  substancePossibilities.append( Array(new TaskObject("lead", Some(new Lead()), "workshop", Array("table", "desk"), 0) ))                // Metals
  substancePossibilities.append( Array(new TaskObject("tin", Some(new Tin()), "workshop", Array("table", "desk"), 0) ))
  substancePossibilities.append( Array(new TaskObject("mercury", Some(new Mercury()), "workshop", Array("table", "desk"), 0) ))
  substancePossibilities.append( Array(new TaskObject("gallium", Some(new Gallium()), "workshop", Array("table", "desk"), 0) ))
  substancePossibilities.append( Array(new TaskObject("caesium", Some(new Caesium()), "workshop", Array("table", "desk"), 0) ))



  val toolPossibilities = new ArrayBuffer[ Array[TaskModifier] ]()
  // Case 1: Normal (stove in kitchen)
  toolPossibilities.append( Array(new TaskObject("stove", Some(new Stove), roomToGenerateIn = "kitchen", Array(""), generateNear = 0) ))
  // Case 2: Disable stove in kitchen (also should add an alternate)
  toolPossibilities.append( Array(new TaskDisable("stove", Array("kitchen") ) ) )


  // Combinations
  val combinations = for {
    i <- substancePossibilities
    j <- toolPossibilities
  } yield List(i, j)

  println("Number of combinations: " + combinations.length)

  def numCombinations():Int = this.combinations.size

  def getCombination(idx:Int):Array[TaskModifier] = {
    val out = new ArrayBuffer[TaskModifier]
    for (elem <- combinations(idx)) {
      out.insertAll(out.length, elem)
    }

    println ("* getCombination: out.length: " + out.length)
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
    val substanceModifier = modifiers(0)
    var substanceName = "<unknown>"
    substanceModifier match {
      case m:TaskObject => {
        substanceName = m.name
      }
      case _ => {
        throw new RuntimeException("ERROR: Unknown task modifier found, where substance modifier was expected." + substanceModifier.toString)
      }
    }

    var subTask = ""
    val gSequence = new ArrayBuffer[Goal]
    if (mode == MODE_CHANGESTATE) {
      subTask = "change the state of matter of"
      gSequence.append( new GoalIsStateOfMatter() )             // Be in any state
      gSequence.append( new GoalIsDifferentStateOfMatter() )    // Be in any state but the first state
    } else if (mode == MODE_MELT) {
      subTask = "melt"
      gSequence.append( new GoalChangeStateOfMatter("solid") )
      gSequence.append( new GoalChangeStateOfMatter("liquid") )
    } else if (mode == MODE_BOIL) {
      subTask = "boil"
      gSequence.append( new GoalChangeStateOfMatter("liquid") )
      gSequence.append( new GoalChangeStateOfMatter("gas") )
    } else if (mode == MODE_FREEZE) {
      subTask = "freeze"
      gSequence.append( new GoalChangeStateOfMatter("liquid") )
      gSequence.append( new GoalChangeStateOfMatter("solid") )
    } else {
      throw new RuntimeException("ERROR: Unrecognized task mode: " + mode)
    }

    val taskLabel = taskName + "-variation" + combinationNum
    val description = "Your task is to " + subTask + " " + substanceName + ". First, focus on the substance. Then, take actions that will cause it to change its state of matter. "
    val goalSequence = new GoalSequence(gSequence.toArray)

    val task = new Task(taskName, description, goalSequence)

    // Return
    return task
  }

  def setupGoals(combinationNum:Int): Task = {
    this.setupGoals( this.getCombination(combinationNum), combinationNum )
  }

}


object TaskChangeOfState {
  val MODE_CHANGESTATE  = "change the state of matter of"
  val MODE_MELT         = "melt"
  val MODE_BOIL         = "boil"
  val MODE_FREEZE       = "freeze"


  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskChangeOfState(mode = MODE_CHANGESTATE) )
    taskMaker.addTask( new TaskChangeOfState(mode = MODE_MELT) )
    taskMaker.addTask( new TaskChangeOfState(mode = MODE_BOIL) )
    taskMaker.addTask( new TaskChangeOfState(mode = MODE_FREEZE) )
  }

}


