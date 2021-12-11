package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.objects.devices.StopWatch
import scienceworld.objects.misc.InclinedPlane
import scienceworld.objects.substance.{Brick, SteelBlock, WoodBlock}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalFind, GoalFindInclinedPlaneNamed}
import TaskMendelialGenetics1._
import scienceworld.objects.containers.{CeramicCup, FlowerPot}
import scienceworld.objects.livingthing.plant.{PeaPlant, Soil}
import scienceworld.objects.taskitems.AnswerBox
import scienceworld.processes.genetics.{Chromosomes, GeneticTrait, GeneticTraitPeas}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}


class TaskMendelialGenetics1(val mode:String = MODE_MENDEL_KNOWN) extends TaskParametric {
  val taskName = "task-9-" + mode.replaceAll(" ", "-")

  val locations = Array("green house")

  // Variation 1: Genetic Trait
  val genetics = new ArrayBuffer[ Array[TaskModifier] ]()
  val traitNames = Array(GeneticTrait.TRAIT_PLANT_HEIGHT, GeneticTrait.TRAIT_SEED_SHAPE, GeneticTrait.TRAIT_SEED_COLOR, GeneticTrait.TRAIT_SEED_SHAPE)

  // Generate a random set of traits, just to determine which are dominant/recessive.
  val traits = new Chromosomes(GeneticTraitPeas.mkRandomTraits())

  for (location <- locations) {

    for (traitName <- traitNames) {
      for (domOrRec <- Array(GeneticTrait.DOMINANT, GeneticTrait.RECESSIVE)) {
        var specificTraitValue = ""
        if (domOrRec == GeneticTrait.DOMINANT) {
          specificTraitValue = traits.getTrait(traitName).get.valueDominant
        } else {
          specificTraitValue = traits.getTrait(traitName).get.valueRecessive
        }

        val seedJar = TaskMendelialGenetics1.mkPeaPlantSeedJar(traitName)

        genetics.append(Array(new TaskObject(seedJar.name, Some(seedJar), roomToGenerateIn = location, Array.empty[String], generateNear = 0, forceAdd = true),
                              new TaskValueStr(key = "domOrRec", value = domOrRec),
                              new TaskValueStr(key = "traitName", value = traitName),
                              new TaskValueStr(key = "traitValue", value = specificTraitValue)
                              ) )

      }
    }

  }

  // Variation 2: Flower pot names
  val flowerpots = new ArrayBuffer[ Array[TaskModifier] ]()
  for (location <- locations) {

    for (i <- 0 until 5) {      // 5 different variations of pot names
      val out = new ArrayBuffer[TaskModifier]()
      val pots = TaskMendelialGenetics1.mkFlowerPots(numPots = 6)
      for (pot <- pots) {
        out.append(new TaskObject(pot.name, Some(pot), roomToGenerateIn = location, Array.empty[String], generateNear = 0, forceAdd = false))
      }
      flowerpots.append( out.toArray )
    }

  }

  // Variation 3: Answer boxes
  val answerBoxes = new ArrayBuffer[ Array[TaskModifier] ]()
  val answerBoxColors = Array("red", "green", "blue", "orange")
  for (location <- locations) {
    for (i <- 0 until answerBoxColors.length-1) {
      val colorDom = answerBoxColors(i)
      val colorRec = answerBoxColors(i+1)
      val boxDom = new AnswerBox(colorDom)
      val boxRec = new AnswerBox(colorRec)
      answerBoxes.append(Array(
        new TaskObject(boxDom.name, Some(boxDom), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
        new TaskObject(boxRec.name, Some(boxRec), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
        new TaskValueStr(key = "answerBoxDom", boxDom.name),
        new TaskValueStr(key = "answerBoxRec", boxRec.name)
      ))
    }
  }


  // Combinations
  var combinations = for {
    i <- genetics
    j <- flowerpots
    k <- answerBoxes
  } yield List(i, j, k)

  println("Genetics: " + genetics.length)
  println("Flowerpots: " + flowerpots.length)
  println("AB: " + answerBoxes.length)

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
    val traitName = this.getTaskValueStr(modifiers, "traitName")
    if (traitName.isEmpty) throw new RuntimeException("ERROR: Failed to find trait name.")
    val traitValue = this.getTaskValueStr(modifiers, "traitValue")
    if (traitValue.isEmpty) throw new RuntimeException("ERROR: Failed to find trait value.")
    val domOrRec = this.getTaskValueStr(modifiers, "domOrRec")
    if (domOrRec.isEmpty) throw new RuntimeException("ERROR: Failed to find whether trait is dominant or recessive.")
    val answerBoxDom = this.getTaskValueStr(modifiers, "answerBoxDom")
    if (answerBoxDom.isEmpty) throw new RuntimeException("ERROR: Failed to find answer box (dominant).")
    val answerBoxRec = this.getTaskValueStr(modifiers, "answerBoxRec")
    if (answerBoxRec.isEmpty) throw new RuntimeException("ERROR: Failed to find answer box (recessive).")


    val gSequence = new ArrayBuffer[Goal]
    var description:String = "<empty>"
    if (mode == MODE_MENDEL_KNOWN) {

      if (domOrRec.get == GeneticTrait.DOMINANT) {
        // Dominant
        gSequence.append(new GoalFind(objectName = answerBoxDom.get, failIfWrong = true, _defocusOnSuccess = true))
      } else {
        // Recessive
        gSequence.append(new GoalFind(objectName = answerBoxRec.get, failIfWrong = true, _defocusOnSuccess = true))
      }

      description = "Your task is to determine whether " + traitValue.get + " " + traitName.get + " is a dominant or recessive trait in the pea plant. "
      description += "If the trait is dominant, focus on the " + answerBoxDom.get + ". "
      description += "If the trait is recessive, focus on the " + answerBoxRec.get + ". "

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


object TaskMendelialGenetics1 {
  val MODE_MENDEL_KNOWN             = "mendellian genetics (known plant)"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskMendelialGenetics1(mode = MODE_MENDEL_KNOWN) )
  }


  /*
   * Helper functions
   */

  // Make a set of inclined planes
  def mkPeaPlantSeedJar(traitName:String):EnvObject = {
    // Double-dominant
    val dom = GeneticTraitPeas.mkRandomChromosomePairExcept(traitName, GeneticTrait.DOMINANT, GeneticTrait.DOMINANT)
    val plantDom = new PeaPlant(_chromosomePairs = Some(dom) )

    // Double-recessive
    val rec = GeneticTraitPeas.mkRandomChromosomePairExcept(traitName, GeneticTrait.RECESSIVE, GeneticTrait.RECESSIVE)
    val plantRec = new PeaPlant(_chromosomePairs = Some(rec) )

    // Seed jar
    val jar = new CeramicCup()      // TODO, make jar
    jar.name = "seed jar"
    jar.addObject(plantDom)
    jar.addObject(plantRec)

    // Return
    jar
  }

  // Make N flower pots
  def mkFlowerPots(numPots:Int): Array[EnvObject] = {
    val maxIdx = 10

    // Make N uniquely-named flower pots
    val pots = new ArrayBuffer[EnvObject]()
    for (i <- 0 until numPots) {
      val flowerpot = new FlowerPot()
      flowerpot.name = "flower pot " + i
      flowerpot.addObject( new Soil() )
      pots.append(flowerpot)
    }

    // Shuffle them, and take N
    val shuffled = Random.shuffle(pots).toArray

    // Return
    return shuffled.slice(0, numPots)
  }


}
