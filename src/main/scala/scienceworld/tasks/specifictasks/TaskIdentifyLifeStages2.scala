package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.objects.containers.furniture.Cupboard
import scienceworld.objects.livingthing.LivingThing
import scienceworld.objects.livingthing.animals.{Ant, Beaver, BlueJay, BrownBear, Butterfly, Chameleon, Chipmunk, Crocodile, Dove, Dragonfly, Elephant, Frog, GiantTortoise, Hedgehog, Moth, Mouse, Parrot, Rabbit, Toad, Turtle, Wolf}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalFind, GoalFindLivingThingStage}
import scienceworld.tasks.specifictasks.TaskIdentifyLifeStages2._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random


class TaskIdentifyLifeStages2(val mode:String = MODE_LIFESTAGES) extends TaskParametric {
  val taskName = "task-7-" + mode.replaceAll(" ", "-")

  val locations = Array("outside")

  // Variation 1: Which seeds to grow
  val numDistractors = 2
  val animalsAndStages = new ArrayBuffer[ Array[TaskModifier] ]()
  for (location <- locations) {
    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new Butterfly(), animalsToAdd = Butterfly.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 0))
    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new Moth(), animalsToAdd = Moth.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 1))

    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new Frog(), animalsToAdd = Frog.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 2))
    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new Toad(), animalsToAdd = Toad.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 3))

    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new GiantTortoise(), animalsToAdd = GiantTortoise.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 4))
    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new Turtle(), animalsToAdd = Turtle.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 5))
    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new Crocodile(), animalsToAdd = Crocodile.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 6))

    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new Parrot(), animalsToAdd = Parrot.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 7))
    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new Dove(), animalsToAdd = Dove.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 8))
    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new BlueJay(), animalsToAdd = BlueJay.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 9))

    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new Elephant(), animalsToAdd = Elephant.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 10))
    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new BrownBear(), animalsToAdd = BrownBear.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 11))
    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new Beaver(), animalsToAdd = Beaver.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 12))
    animalsAndStages.append( TaskIdentifyLifeStages2.mkTaskVariation(livingThing = new Wolf(), animalsToAdd = Wolf.mkExamplesAtLifeStages(), location = location) ++ TaskIdentifyLifeStages2.mkDistractorAnimals(location, numDistractors, 13))
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
    val stage1 = this.getTaskValueStr(modifiers, "stage1")
    if (stage1.isEmpty) throw new RuntimeException("ERROR: Failed to find lifecycle stage 1 in task setup.")
    val stage2 = this.getTaskValueStr(modifiers, "stage2")
    if (stage2.isEmpty) throw new RuntimeException("ERROR: Failed to find lifecycle stage 2 in task setup.")
    val stage3 = this.getTaskValueStr(modifiers, "stage3")
    //if (stage3.isEmpty) throw new RuntimeException("ERROR: Failed to find lifecycle stage 3 in task setup.")
    val stage4 = this.getTaskValueStr(modifiers, "stage4")
    //if (stage4.isEmpty) throw new RuntimeException("ERROR: Failed to find lifecycle stage 4 in task setup.")
    val stage5 = this.getTaskValueStr(modifiers, "stage5")
    //if (stage5.isEmpty) throw new RuntimeException("ERROR: Failed to find lifecycle stage 5 in task setup.")


    val gSequence = new ArrayBuffer[Goal]
    var description:String = "<empty>"
    if (mode == MODE_LIFESTAGES) {

      gSequence.append(new GoalFindLivingThingStage(livingThingType = animalName.get, lifeStage = stage1.get, failIfWrong = true, _defocusOnSuccess = true))
      if (stage2.isDefined) gSequence.append(new GoalFindLivingThingStage(livingThingType = animalName.get, lifeStage = stage2.get, failIfWrong = true, _defocusOnSuccess = true))
      if (stage3.isDefined) gSequence.append(new GoalFindLivingThingStage(livingThingType = animalName.get, lifeStage = stage3.get, failIfWrong = true, _defocusOnSuccess = true))
      if (stage4.isDefined) gSequence.append(new GoalFindLivingThingStage(livingThingType = animalName.get, lifeStage = stage4.get, failIfWrong = true, _defocusOnSuccess = true))
      if (stage5.isDefined) gSequence.append(new GoalFindLivingThingStage(livingThingType = animalName.get, lifeStage = stage5.get, failIfWrong = true, _defocusOnSuccess = true))

      val numLifeStages = gSequence.length

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


object TaskIdentifyLifeStages2 {
  val MODE_LIFESTAGES       = "identify life stages 2"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskIdentifyLifeStages2(mode = MODE_LIFESTAGES) )
  }


  /*
   * Helper functinos
   */

  // Randomly choose a set of N distractor animals to include in the environment
  // This version also instantiates the different life stages of those distractor animals.
  def mkDistractorAnimals(location:String, numAnimals:Int = 3, variationIdx:Int):Array[TaskModifier] = {
    val allAnimals = List(Butterfly.mkExamplesAtLifeStages(), Moth.mkExamplesAtLifeStages(), Frog.mkExamplesAtLifeStages(), Toad.mkExamplesAtLifeStages(), GiantTortoise.mkExamplesAtLifeStages(), Turtle.mkExamplesAtLifeStages(), Crocodile.mkExamplesAtLifeStages(), Parrot.mkExamplesAtLifeStages(), Dove.mkExamplesAtLifeStages(), BlueJay.mkExamplesAtLifeStages(), Elephant.mkExamplesAtLifeStages(), BrownBear.mkExamplesAtLifeStages(), Beaver.mkExamplesAtLifeStages(), Wolf.mkExamplesAtLifeStages() )
    val rand = new Random(variationIdx)     // Use variationIdx for seed
    // Shuffle
    val shuffled = rand.shuffle(allAnimals)

    val out = new ArrayBuffer[TaskModifier]
    for (i <- 0 until numAnimals) {
      for (animal <- shuffled(i)) {
        out.append(new TaskObject(animal.name, Some(animal), roomToGenerateIn = location, Array.empty[String], generateNear = 0, forceAdd = true))
      }
    }

    out.toArray
  }

  // Make a task variation that includes (a) adding the living thing to the environment, (b) recording it's life stages in key/value pairs in the task modifiers
  def mkTaskVariation(livingThing:LivingThing, animalsToAdd:Array[EnvObject], location: String): Array[TaskModifier] = {

    // Get living thing life stages
    var lifestages = livingThing.lifecycle.get.stages.map(_.stageName)
    lifestages = lifestages.slice(0, lifestages.length - 1)               // Remove identifying the last life stage from the task, since it's usually always 'dead'
    val stageKeys = new ArrayBuffer[TaskModifier]
    for (i <- 0 until lifestages.length) {
      val lifestageName = lifestages(i)
      stageKeys.append(new TaskValueStr(key = "stage" + (i + 1), value = lifestageName))
      //println("stage" + i + "\t" + lifestageName)
    }

    // Create task modifier
    val out = new ArrayBuffer[TaskModifier]()
    // Add each animal
    for (animal <- animalsToAdd) {
      out.append( new TaskObject(animal.name, Some(animal), roomToGenerateIn = location, Array.empty[String], generateNear = 0, forceAdd = true) )
    }

    // Add a key describing which animal this task is for
    out.append( new TaskValueStr(key = "animal", value = livingThing.name) )
    // Add the life stage keys
    out.insertAll(out.length, stageKeys)

    return out.toArray
  }

}
