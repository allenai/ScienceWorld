package scienceworld.tasks.specifictasks

import scienceworld.actions.Action
import scienceworld.objects.agent.Agent
import scienceworld.objects.containers.WoodCup
import scienceworld.objects.containers.furniture.Cupboard
import scienceworld.objects.livingthing.animals.{Ant, Beaver, BrownBear, Chameleon, Chipmunk, Crocodile, Dragonfly, Elephant, GiantTortoise, Hedgehog, Mouse, Parrot, Rabbit, Toad, Wolf}
import scienceworld.objects.substance.paint.{BluePaint, RedPaint, YellowPaint}
import scienceworld.objects.substance.{Soap, SodiumChloride}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalFind, GoalMoveToLocation, GoalMoveToNewLocation}
import scienceworld.tasks.specifictasks.TaskLifeSpan._

import scala.collection.mutable.ArrayBuffer


class TaskLifeSpan(val mode:String = MODE_LIFESPAN_LONGEST) extends TaskParametric {
  val taskName = "task-6-" + mode.replaceAll(" ", "-")

  val locations = Array("outside")
  val animalsLongLivedExamples = Array(new GiantTortoise, new Parrot, new Elephant, new Crocodile, new BrownBear)
  val animalsMediumLivedExamples = Array(new Beaver, new Wolf, new Chipmunk, new Toad, new Rabbit)
  val animalsShortLivedExamples = Array(new Hedgehog, new Mouse, new Chameleon, new Dragonfly, new Ant)

  // Variation 1: Which seeds to grow
  val animalsLongLived = new ArrayBuffer[ Array[TaskModifier] ]()
  val animalsMediumLived = new ArrayBuffer[ Array[TaskModifier] ]()
  val animalsShortLived = new ArrayBuffer[ Array[TaskModifier]]()
  for (location <- locations) {

    // Long-lived
    for (animal <- animalsLongLivedExamples) {
      animalsLongLived.append(Array(
        new TaskObject(animal.name, Some(animal), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
        new TaskValueStr(key = "long", value = animal.name),
        new TaskValueStr(key = "location", value = location)      // Also record location name
      ))
    }

    // Medium-lived
    for (animal <- animalsMediumLivedExamples) {
      animalsMediumLived.append(Array(
        new TaskObject(animal.name, Some(animal), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
        new TaskValueStr(key = "medium", value = animal.name)
      ))
    }

    // Short-lived
    for (animal <- animalsShortLivedExamples) {
      animalsShortLived.append(Array(
        new TaskObject(animal.name, Some(animal), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
        new TaskValueStr(key = "short", value = animal.name)
      ))
    }

  }

  // Combinations
  val combinations = for {
    i <- animalsLongLived
    j <- animalsMediumLived
    k <- animalsShortLived
  } yield List(i, j, k)

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
    val animalLong = this.getTaskValueStr(modifiers, "long")
    if (animalLong.isEmpty) throw new RuntimeException("ERROR: Failed to find long-lived animal in task setup.")
    val animalMedium = this.getTaskValueStr(modifiers, "medium")
    if (animalMedium.isEmpty) throw new RuntimeException("ERROR: Failed to find medium-lived animal in task setup.")
    val animalShort = this.getTaskValueStr(modifiers, "short")
    if (animalShort.isEmpty) throw new RuntimeException("ERROR: Failed to find short-lived animal in task setup.")
    val animalLocation = this.getTaskValueStr(modifiers, "location")
    if (animalLocation.isEmpty) throw new RuntimeException("ERROR: Failed to find animal location in task setup.")


    val gSequence = new ArrayBuffer[Goal]
    val gSequenceUnordered = new ArrayBuffer[Goal]

    var description:String = "<empty>"
    if (mode == MODE_LIFESPAN_LONGEST) {

      gSequence.append(new GoalFind(objectName = animalLong.get, failIfWrong = true, description = "focus on animal with longest life span"))

      gSequenceUnordered.append( new GoalMoveToNewLocation(_isOptional = true, unlessInLocation = animalLocation.get, description = "Move to a new location (unless starting in task location)") )            // Move to any new location
      gSequenceUnordered.append( new GoalMoveToLocation(animalLocation.get, _isOptional = true, description = "Move to the location asked by the task") )

      description = "Your task is to find the animal with the longest life span.  The animals are in the '" + animalLocation.get + "' location.  Focus on the animal with the longest life span."

    } else if (mode == MODE_LIFESPAN_SHORTEST) {

      gSequence.append(new GoalFind(objectName = animalShort.get, failIfWrong = true, description = "focus on animal with shortest life span"))

      gSequenceUnordered.append( new GoalMoveToNewLocation(_isOptional = true, unlessInLocation = animalLocation.get, description = "Move to a new location (unless starting in task location)") )            // Move to any new location
      gSequenceUnordered.append( new GoalMoveToLocation(animalLocation.get, _isOptional = true, description = "Move to the location asked by the task") )

      description = "Your task is to find the animal with the shortest life span.  The animals are in the '" + animalLocation.get + "' location.  Focus on the animal with the shortest life span."

    } else if (mode == MODE_LIFESPAN_LONGTHENSHORT) {

      gSequence.append(new GoalFind(objectName = animalLong.get, failIfWrong = true, _defocusOnSuccess = true, description = "focus on animal with longest life span"))
      gSequence.append(new GoalFind(objectName = animalShort.get, failIfWrong = true, description = "focus on animal with shortest life span"))

      gSequenceUnordered.append( new GoalMoveToNewLocation(_isOptional = true, unlessInLocation = animalLocation.get, description = "Move to a new location (unless starting in task location)") )            // Move to any new location
      gSequenceUnordered.append( new GoalMoveToLocation(animalLocation.get, _isOptional = true, description = "Move to the location asked by the task") )

      description = "Your task is to find the animal with the longest life span, then the shortest life span. First, focus on the animal with the longest life span.  Then, focus on the animal with the shortest life span. The animals are in the '" + animalLocation.get + "' location. "

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


}


object TaskLifeSpan {
  val MODE_LIFESPAN_LONGEST       = "lifespan (longest lived)"
  val MODE_LIFESPAN_SHORTEST      = "lifespan (shortest lived)"
  val MODE_LIFESPAN_LONGTHENSHORT = "lifespan (longest lived then shortest lived)"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskLifeSpan(mode = MODE_LIFESPAN_LONGEST) )
    taskMaker.addTask( new TaskLifeSpan(mode = MODE_LIFESPAN_SHORTEST) )
    taskMaker.addTask( new TaskLifeSpan(mode = MODE_LIFESPAN_LONGTHENSHORT) )
  }

}
