package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.objects.devices.StopWatch
import scienceworld.objects.misc.InclinedPlane
import scienceworld.objects.substance.{Brick, SteelBlock, WoodBlock}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalContainerOpen, GoalFind, GoalFindInclinedPlaneNamed, GoalInRoomWithObject, GoalLifeStageAnywhere, GoalMoveToLocation, GoalMoveToNewLocation, GoalSpecificObjectInDirectContainer}
import TaskMendelianGenetics1._
import scienceworld.objects.containers.{CeramicCup, FlowerPot}
import scienceworld.objects.livingthing.plant.{PeaPlant, Soil}
import scienceworld.objects.taskitems.AnswerBox
import scienceworld.processes.genetics.{Chromosomes, GeneticTrait, GeneticTraitPeas}
import scienceworld.processes.lifestage.PlantLifeStages.{PLANT_STAGE_ADULT_PLANT, PLANT_STAGE_REPRODUCING, PLANT_STAGE_SEED, PLANT_STAGE_SEEDLING}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}


class TaskMendelianGenetics1(val mode:String = MODE_MENDEL_KNOWN) extends TaskParametric {
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

        val seedJar = TaskMendelianGenetics1.mkPeaPlantSeedJar(traitName)

        genetics.append(Array(new TaskObject(seedJar.name, Some(seedJar), roomToGenerateIn = location, Array.empty[String], generateNear = 0, forceAdd = true),
                              new TaskValueStr(key = "domOrRec", value = domOrRec),
                              new TaskValueStr(key = "traitName", value = traitName),
                              new TaskValueStr(key = "traitValue", value = specificTraitValue),
                              new TaskValueStr(key = "seedType", value = "pea")
                              ) )

      }
    }

  }

  // Variation 2: Flower pot names
  val flowerpots = new ArrayBuffer[ Array[TaskModifier] ]()
  for (location <- locations) {

    for (i <- 0 until 5) {      // 5 different variations of pot names
      val out = new ArrayBuffer[TaskModifier]()
      val pots = TaskMendelianGenetics1.mkFlowerPots(numPots = 6)
      var potNames = new ArrayBuffer[String]
      for (pot <- pots) {
        out.append(new TaskObject(pot.name, Some(pot), roomToGenerateIn = location, Array.empty[String], generateNear = 0, forceAdd = false))
        potNames.append(pot.name)
      }
      out.append( new TaskValueStr("flowerPotNames", potNames.mkString(",")) )
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

    val flowerPotNames = this.getTaskValueStr(modifiers, "flowerPotNames").get.split(",")
    val seedType = this.getTaskValueStr(modifiers, "seedType").get


    val gSequence = new ArrayBuffer[Goal]
    val gSequenceUnordered = new ArrayBuffer[Goal]
    var description:String = "<empty>"
    if (mode == MODE_MENDEL_KNOWN) {

      if (domOrRec.get == GeneticTrait.DOMINANT) {
        // Dominant
        gSequence.append(new GoalFind(objectName = answerBoxDom.get, failIfWrong = true, _defocusOnSuccess = true, description = "focus on the correct answer box"))
      } else {
        // Recessive
        gSequence.append(new GoalFind(objectName = answerBoxRec.get, failIfWrong = true, _defocusOnSuccess = true, description = "focus on the correct answer box"))
      }

      // Seed Jar
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = "seed jar", _isOptional = true, description = "be in same location as seed jar"))
      gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName = "inventory", validObjectNames = Array("seed jar"), _isOptional = true, key = "haveSeedJar", description = "have seed jar in inventory"))

      // Moving to helpful locations
      gSequenceUnordered.append(new GoalMoveToNewLocation(_isOptional = true, unlessInLocation = "", description = "move to a new location") )            // Move to any new location
      gSequenceUnordered.append(new GoalMoveToLocation("green house", _isOptional = true, key = "move1", description = "move to the green house") )
      gSequenceUnordered.append(new GoalMoveToLocation("green house", _isOptional = true, key = "move2", keysMustBeCompletedBefore = Array("haveSeedJar"), description = "move to the green house (after having seed jar)") )

      // Have soil in flower pots
      var cIdx:Int = 1
      for (containerName <- flowerPotNames) {
        gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName, validObjectNames = Array("soil"), _isOptional = true, description = "have soil in flower pot (" + cIdx + ")"))
        cIdx += 1
      }

      // Have water in flower pots
      var wIdx:Int = 1
      for (containerName <- flowerPotNames) {
        gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName, validObjectNames = Array("water"), _isOptional = true, description = "have water in flower pot (" + wIdx + ")"))
        wIdx += 1
      }

      // Have seeds in flower pots
      var sIdx:Int = 1
      for (containerName <- flowerPotNames) {
        gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName, validObjectNames = Array(seedType + " seed"), _isOptional = true, description = "have seed in flower pot (" + sIdx + ")"))
        sIdx += 1
      }

      // Have plants grow through life stages
      for (numToFind <- 1 to 6) {
        gSequenceUnordered.append(new GoalLifeStageAnywhere(lifeFormType = seedType, lifeStageName = PLANT_STAGE_SEED, minNumToFind = numToFind, description = "have at least " + numToFind + " plants as seeds"))
        gSequenceUnordered.append(new GoalLifeStageAnywhere(lifeFormType = seedType, lifeStageName = PLANT_STAGE_SEEDLING, minNumToFind = numToFind, description = "have at least " + numToFind + " plants as seedlings"))
        gSequenceUnordered.append(new GoalLifeStageAnywhere(lifeFormType = seedType, lifeStageName = PLANT_STAGE_ADULT_PLANT, minNumToFind = numToFind, description = "have at least " + numToFind + " plants as adult plants"))
        gSequenceUnordered.append(new GoalLifeStageAnywhere(lifeFormType = seedType, lifeStageName = PLANT_STAGE_REPRODUCING, minNumToFind = numToFind, key = "atLeast" + numToFind + "Reproducing", description = "have at least " + numToFind + " plants as reproducing plants"))
      }

      // Have pollinators in the same room as plants
      gSequenceUnordered.append(new GoalContainerOpen(containerName = "bee hive", _isOptional = true, keysMustBeCompletedBefore = Array("atLeast2Reproducing"), description = "bee hive open (after reprod. life stage)"))
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = "adult bee", _isOptional = true, keysMustBeCompletedBefore = Array("atLeast2Reproducing"), description = "be in same location as pollinator (after reprod. life stage)"))

      // Have a seed grow on the plant (i.e., be in the same location as that seed, on the tree)
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = seedType, _isOptional = true, keysMustBeCompletedBefore = Array("atLeast2Reproducing"), description = "be in same location as grown seed"))


      description = "Your task is to determine whether " + traitValue.get + " " + traitName.get + " is a dominant or recessive trait in the pea plant. "
      description += "If the trait is dominant, focus on the " + answerBoxDom.get + ". "
      description += "If the trait is recessive, focus on the " + answerBoxRec.get + ". "

    } else {
      throw new RuntimeException("ERROR: Unrecognized task mode: " + mode)
    }

    val taskLabel = taskName + "-variation" + combinationNum
    //val description = "Your task is to find a " + subTask + ". First, focus on the thing. Then, move it to the " + answerBoxName + " in the " + answerBoxLocation + "."
    val goalSequence = new GoalSequence(gSequence.toArray, gSequenceUnordered.toArray)

    val task = new Task(taskName, description, goalSequence)

    // Return
    return task
  }

  def setupGoals(combinationNum:Int): Task = {
    this.setupGoals( this.getCombination(combinationNum), combinationNum )
  }




}


object TaskMendelianGenetics1 {
  val MODE_MENDEL_KNOWN             = "mendellian genetics (known plant)"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskMendelianGenetics1(mode = MODE_MENDEL_KNOWN) )
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
