package scienceworld.tasks.specifictasks

//TODO: Only part-way implemented

import scienceworld.actions.Action
import scienceworld.environments.ContainerMaker
import scienceworld.objects.agent.Agent
import scienceworld.objects.containers.{CeramicCup, FlowerPot}
import scienceworld.objects.devices.Stove
import scienceworld.objects.livingthing.plant.{AppleTree, Plant, Soil}
import scienceworld.objects.taskitems.AnswerBox
import scienceworld.processes.PlantReproduction
import scienceworld.processes.lifestage.PlantLifeStages.{PLANT_STAGE_ADULT_PLANT, PLANT_STAGE_REPRODUCING, PLANT_STAGE_SEED, PLANT_STAGE_SEEDLING}
import scienceworld.properties.LeadProp
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskDisable, TaskMaker1, TaskModifier, TaskObject, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalActivateDeviceWithName, GoalChangeStateOfMatter, GoalContainerOpen, GoalFind, GoalFocusOnAnimal, GoalFocusOnLivingThing, GoalFocusOnNonlivingThing, GoalFocusOnPlant, GoalInRoomWithObject, GoalIsDifferentStateOfMatter, GoalIsNotStateOfMatter, GoalIsStateOfMatter, GoalLifeStage, GoalLifeStageAnywhere, GoalMoveToLocation, GoalMoveToNewLocation, GoalObjectInContainer, GoalObjectsInSingleContainer, GoalPastActionUseObjectOnObject, GoalSpecificObjectInDirectContainer, GoalTemperatureIncrease, GoalTemperatureOnFire}
import scienceworld.tasks.specifictasks.TaskChangeOfState.{MODE_BOIL, MODE_CHANGESTATE, MODE_FREEZE, MODE_MELT}
import scienceworld.tasks.specifictasks.TaskFindLivingNonLiving.{MODE_ANIMAL, MODE_LIVING, MODE_NONLIVING, MODE_PLANT}
import scienceworld.tasks.specifictasks.TaskGrowPlant.{MODE_GROW_FRUIT, MODE_GROW_PLANT}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}


class TaskGrowPlant(val mode:String = MODE_LIVING) extends TaskParametric {
  val taskName = "task-4-" + mode.replaceAll(" ", "-")


  // Variation 1: Which seeds to grow
  val seeds = new ArrayBuffer[ Array[TaskModifier] ]()
  val locations = Array("kitchen", "bathroom", "living room", "bedroom", "workshop", "green house")
  //val locations = Array("green house")
  for (location <- locations) {

    val seedJarApple = this.mkSeedJar(PlantReproduction.PLANT_APPLE)
    seeds.append(Array(new TaskObject(seedJarApple.name, Some(seedJarApple), roomToGenerateIn = location, Array.empty[String], generateNear = 0), new TaskValueStr("seedType", PlantReproduction.PLANT_APPLE), new TaskValueStr("location", location)))

    val seedJarAvocado = this.mkSeedJar(PlantReproduction.PLANT_AVOCADO)
    seeds.append(Array(new TaskObject(seedJarAvocado.name, Some(seedJarAvocado), roomToGenerateIn = location, Array.empty[String], generateNear = 0), new TaskValueStr("seedType", PlantReproduction.PLANT_AVOCADO), new TaskValueStr("location", location)))

    val seedJarBanana = this.mkSeedJar(PlantReproduction.PLANT_BANANA)
    seeds.append(Array(new TaskObject(seedJarBanana.name, Some(seedJarBanana), roomToGenerateIn = location, Array.empty[String], generateNear = 0), new TaskValueStr("seedType", PlantReproduction.PLANT_BANANA), new TaskValueStr("location", location)))

    val seedJarCherry = this.mkSeedJar(PlantReproduction.PLANT_CHERRY)
    seeds.append(Array(new TaskObject(seedJarCherry.name, Some(seedJarCherry), roomToGenerateIn = location, Array.empty[String], generateNear = 0), new TaskValueStr("seedType", PlantReproduction.PLANT_CHERRY), new TaskValueStr("location", location)))

    val seedJarLemon = this.mkSeedJar(PlantReproduction.PLANT_LEMON)
    seeds.append(Array(new TaskObject(seedJarLemon.name, Some(seedJarLemon), roomToGenerateIn = location, Array.empty[String], generateNear = 0), new TaskValueStr("seedType", PlantReproduction.PLANT_LEMON), new TaskValueStr("location", location)))

    val seedJarOrange = this.mkSeedJar(PlantReproduction.PLANT_ORANGE)
    seeds.append(Array(new TaskObject(seedJarOrange.name, Some(seedJarOrange), roomToGenerateIn = location, Array.empty[String], generateNear = 0), new TaskValueStr("seedType", PlantReproduction.PLANT_ORANGE), new TaskValueStr("location", location)))

    val seedJarPeach = this.mkSeedJar(PlantReproduction.PLANT_PEACH)
    seeds.append(Array(new TaskObject(seedJarApple.name, Some(seedJarPeach), roomToGenerateIn = location, Array.empty[String], generateNear = 0), new TaskValueStr("seedType", PlantReproduction.PLANT_PEACH), new TaskValueStr("location", location)))

  }


  // Variation 2: What containers are available to grow the seeds in
  val plantContainers = new ArrayBuffer[ Array[TaskModifier] ]()
  val numContainers = 3

  // Case 1: N flower pots, with soil inside
  val case1tm = new ArrayBuffer[TaskModifier]()
  val containerNames1 = new ArrayBuffer[String]
  for (i <- 0 until numContainers) {
    val container = new FlowerPot()
    container.name = "flower pot " + (i+1)
    container.addObject( new Soil() )
    case1tm.append( new TaskObject(container.name, Some(container), roomToGenerateIn = "green house", Array.empty[String], generateNear = 0))
    containerNames1.append(container.name)
  }
  case1tm.append( new TaskValueStr("containerNames", containerNames1.mkString(",")))
  plantContainers.append( case1tm.toArray )


  // Case 2: N flower pots, no soil inside (but, soil nearby)
  val case2tm = new ArrayBuffer[TaskModifier]()
  val containerNames2 = new ArrayBuffer[String]
  for (i <- 0 until numContainers) {
    val container = new FlowerPot()
    container.name = "flower pot " + (i+1)
    case2tm.append( new TaskObject(container.name, Some(container), roomToGenerateIn = "green house", Array.empty[String], generateNear = 0))
    containerNames2.append(container.name)

    val soil = new Soil()
    case2tm.append( new TaskObject(soil.name, Some(soil), roomToGenerateIn = "green house", Array.empty[String], generateNear = 0))
  }
  case2tm.append( new TaskValueStr("containerNames", containerNames2.mkString(",")))
  plantContainers.append( case2tm.toArray )

  // Case 3: N flower pots, no soil nearby
  val case3tm = new ArrayBuffer[TaskModifier]()
  val containerNames3 = new ArrayBuffer[String]
  for (i <- 0 until numContainers) {
    val container = new FlowerPot()
    container.name = "flower pot " + (i+1)
    case3tm.append( new TaskObject(container.name, Some(container), roomToGenerateIn = "green house", Array.empty[String], generateNear = 0))
    containerNames3.append(container.name)
  }
  case3tm.append( new TaskValueStr("containerNames", containerNames3.mkString(",")))
  plantContainers.append( case3tm.toArray )


  // Case 4: No flower pots.  Only shovel outside?



  // Combinations
  val combinations = for {
    i <- seeds
    j <- plantContainers
  } yield List(i, j)

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

    // The first modifier will be the seed jar.
    val seedType = this.getTaskValueStr(modifiers, "seedType").get
    val seedLocation = this.getTaskValueStr(modifiers, "location").get
    val containerNames = this.getTaskValueStr(modifiers, "containerNames").get.split(",")


    var subTask = ""
    val gSequence = new ArrayBuffer[Goal]
    val gSequenceUnordered = new ArrayBuffer[Goal]

    var description:String = "<empty>"
    if (mode == MODE_GROW_PLANT) {
      gSequence.append( new GoalLifeStage(lifeFormType = seedType, lifeStageName = PLANT_STAGE_SEED, description = "focus plant is in seed stage") )
      gSequence.append( new GoalLifeStage(lifeFormType = seedType, lifeStageName = PLANT_STAGE_SEEDLING, description = "focus plant is in seedling stage") )
      gSequence.append( new GoalLifeStage(lifeFormType = seedType, lifeStageName = PLANT_STAGE_ADULT_PLANT, description = "focus plant is in adult stage") )
      gSequence.append( new GoalLifeStage(lifeFormType = seedType, lifeStageName = PLANT_STAGE_REPRODUCING, description = "focus plant is in reproducing stage") )

      // Seed Jar
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = "seed jar", _isOptional = true, description = "be in same location as seed jar"))
      gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName = "inventory", validObjectNames = Array("seed jar"), _isOptional = true, key = "haveSeedJar", description = "have seed jar in inventory"))

      // Shovel/Soil
      gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName = "inventory", validObjectNames = Array("shovel"), _isOptional = true, description = "have shovel in inventory"))
      gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName = "inventory", validObjectNames = Array("soil"), _isOptional = true, description = "have soil in inventory"))
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = "soil", _isOptional = true, description = "be in same location as soil"))

      // Moving to helpful locations
      gSequenceUnordered.append(new GoalMoveToNewLocation(_isOptional = true, unlessInLocation = "", description = "move to a new location") )            // Move to any new location
      gSequenceUnordered.append(new GoalMoveToLocation("green house", _isOptional = true, key = "move1", description = "move to the green house") )
      gSequenceUnordered.append(new GoalMoveToLocation("green house", _isOptional = true, key = "move2", keysMustBeCompletedBefore = Array("haveSeedJar"), description = "move to the green house (after having seed jar)") )

      // Have soil in flower pots
      var cIdx:Int = 1
      for (containerName <- containerNames) {
        gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName, validObjectNames = Array("soil"), _isOptional = true, description = "have soil in flower pot (" + cIdx + ")"))
        cIdx += 1
      }

      // Have water in flower pots
      var wIdx:Int = 1
      for (containerName <- containerNames) {
        gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName, validObjectNames = Array("water"), _isOptional = true, description = "have water in flower pot (" + wIdx + ")"))
        wIdx += 1
      }

      // Have seeds in flower pots
      var sIdx:Int = 1
      for (containerName <- containerNames) {
        gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName, validObjectNames = Array(seedType + " seed"), _isOptional = true, description = "have seed in flower pot (" + sIdx + ")"))
        sIdx += 1
      }

      description = "Your task is to grow a " + seedType + " plant from seed. Seeds can be found in the " + seedLocation + ". First, focus on a seed. Then, make changes to the environment that grow the plant until it reaches the reproduction life stage."

    } else if (mode == MODE_GROW_FRUIT) {
      // TODO: Currently requires all other apples/fruits to be erased from the environment?  (and possibly all other fruiting trees?)
      gSequence.append( new GoalFind(objectName = seedType, description = "focus on the grown fruit") )     // e.g. "seedtype" will be "apple"

      description = "Your task is to grow a " + seedType + ". This will require growing several plants, and them being crosspollinated to produce fruit.  Seeds can be found in the " + seedLocation + ". To complete the task, focus on the " + seedType + "."

      // Seed Jar
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = "seed jar", _isOptional = true, description = "be in same location as seed jar"))
      gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName = "inventory", validObjectNames = Array("seed jar"), _isOptional = true, key = "haveSeedJar", description = "have seed jar in inventory"))

      // Shovel/Soil
      gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName = "inventory", validObjectNames = Array("shovel"), _isOptional = true, description = "have shovel in inventory"))
      gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName = "inventory", validObjectNames = Array("soil"), _isOptional = true, description = "have soil in inventory"))
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = "soil", _isOptional = true, description = "be in same location as soil"))

      // Moving to helpful locations
      gSequenceUnordered.append(new GoalMoveToNewLocation(_isOptional = true, unlessInLocation = "", description = "move to a new location") )            // Move to any new location
      gSequenceUnordered.append(new GoalMoveToLocation("green house", _isOptional = true, key = "move1", description = "move to the green house") )
      gSequenceUnordered.append(new GoalMoveToLocation("green house", _isOptional = true, key = "move2", keysMustBeCompletedBefore = Array("haveSeedJar"), description = "move to the green house (after having seed jar)") )

      // Have soil in flower pots
      var cIdx:Int = 1
      for (containerName <- containerNames) {
        gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName, validObjectNames = Array("soil"), _isOptional = true, description = "have soil in flower pot (" + cIdx + ")"))
        cIdx += 1
      }

      // Have water in flower pots
      var wIdx:Int = 1
      for (containerName <- containerNames) {
        gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName, validObjectNames = Array("water"), _isOptional = true, description = "have water in flower pot (" + wIdx + ")"))
        wIdx += 1
      }

      // Have seeds in flower pots
      var sIdx:Int = 1
      for (containerName <- containerNames) {
        gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName, validObjectNames = Array(seedType + " seed"), _isOptional = true, description = "have seed in flower pot (" + sIdx + ")"))
        sIdx += 1
      }

      // Have plants grow through life stages
      for (numToFind <- 1 to 2) {
        gSequenceUnordered.append(new GoalLifeStageAnywhere(lifeFormType = seedType, lifeStageName = PLANT_STAGE_SEED, minNumToFind = numToFind, description = "have at least " + numToFind + " plants as seeds"))
        gSequenceUnordered.append(new GoalLifeStageAnywhere(lifeFormType = seedType, lifeStageName = PLANT_STAGE_SEEDLING, minNumToFind = numToFind, description = "have at least " + numToFind + " plants as seedlings"))
        gSequenceUnordered.append(new GoalLifeStageAnywhere(lifeFormType = seedType, lifeStageName = PLANT_STAGE_ADULT_PLANT, minNumToFind = numToFind, description = "have at least " + numToFind + " plants as adult plants"))
        gSequenceUnordered.append(new GoalLifeStageAnywhere(lifeFormType = seedType, lifeStageName = PLANT_STAGE_REPRODUCING, minNumToFind = numToFind, key = "atLeast" + numToFind + "Reproducing", description = "have at least " + numToFind + " plants as reproducing plants"))
      }

      // Have pollinators in the same room as plants
      gSequenceUnordered.append(new GoalContainerOpen(containerName = "bee hive", _isOptional = true, description = "bee hive open (after reprod. life stage)"))
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = "adult bee", _isOptional = true, keysMustBeCompletedBefore = Array("atLeast2Reproducing"), description = "be in same location as pollinator (after reprod. life stage)"))

      // Have a fruit grow on the plant (i.e., be in the same location as that fruit, on the tree)
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = seedType, _isOptional = true, description = "be in same location as fruit"))


    } else {
      throw new RuntimeException("ERROR: Unrecognized task mode: " + mode)
    }

    val taskLabel = taskName + "-variation" + combinationNum
    //val description = "Your task is to find a " + subTask + ". First, focus on the thing. Then, move it to the " + answerBoxName + " in the " + answerBoxLocation + "."
    val goalSequence = new GoalSequence(gSequence.toArray, gSequenceUnordered.toArray)

    val task = new Task(taskName, description, goalSequence, taskModifiers = modifiers)

    // Return
    return task
  }

  def setupGoals(combinationNum:Int): Task = {
    this.setupGoals( this.getCombination(combinationNum), combinationNum )
  }


  def mkGoldActionSequence(modifiers:Array[TaskModifier], universe:EnvObject, agent:Agent): (Boolean, Array[Action], Array[String]) = {
    // TODO: Unimplemented
    return (false, Array.empty[Action], Array.empty[String])
  }

  /*
   * Helpers
   */

  // Make a jar containing a number of seeds of the same type
  def mkSeedJar(plantType:String, numSeeds:Int = 5):EnvObject = {
    val jar = new CeramicCup()      // TODO, make jar
    jar.name = "seed jar"

    for (i <- 0 until numSeeds) {
      val seed = PlantReproduction.createSeed(plantType)
      if (seed.isDefined) jar.addObject(seed.get)
    }

    return jar
  }

  // Takes a seedJar as input, and determines what kind of seed is inside
  def getSeedType(seedJar:EnvObject): String = {
    val contents = seedJar.getContainedObjects()
    for (obj <- contents) {
      obj match {
        case x:Plant => return x.propLife.get.lifeformType
        case _ => { // do nothing
        }
      }
    }
    // We should never reach here
    return ""
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

