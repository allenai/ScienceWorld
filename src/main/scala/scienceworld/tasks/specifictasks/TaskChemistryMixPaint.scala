package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.objects.containers.WoodCup
import scienceworld.objects.containers.furniture.Cupboard
import scienceworld.objects.substance.paint.{BluePaint, RedPaint, YellowPaint}
import scienceworld.objects.substance.{Soap, SodiumChloride}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalFind, GoalInRoomWithObject, GoalObjectsInSingleContainer}
import scienceworld.tasks.specifictasks.TaskChemistryMixPaint._

import scala.collection.mutable.ArrayBuffer


class TaskChemistryMixPaint(val mode:String = MODE_CHEMISTRY_MIX_PAINT_SECONDARY) extends TaskParametric {
  val taskName = "task-5-" + mode.replaceAll(" ", "-")


  // Variation 1: Which seeds to grow
  val additionalPaint = new ArrayBuffer[ Array[TaskModifier] ]()

  val locations = Array("art studio", "workshop")
  //val locations = Array("green house")
  for (location <- locations) {

    // Add an extra cupboard full of paint in some location
    val cupboard = new Cupboard()
    val paints2 = Array(new RedPaint, new BluePaint, new YellowPaint) //, new GreenPaint, new VioletPaint, new OrangePaint)
    for (paint <- paints2) {
      val woodcup = new WoodCup()
      woodcup.addObject(paint)
      cupboard.addObject(woodcup)
    }
    cupboard.name = "paint cupboard"

    additionalPaint.append( Array(
      new TaskObject(cupboard.name, Some(cupboard), roomToGenerateIn = location, Array.empty[String], generateNear = 0)
    ))

  }


  // Variation 2: Which paint to manufacture
  val paintNames = new ArrayBuffer[ Array[TaskModifier] ]()

  // Green
  paintNames.append(Array(
    new TaskValueStr(key = "inputChemicalsSecondary", value = "blue paint,yellow paint"),
    new TaskValueStr(key = "inputChemicalsTertiary", value = "green paint,blue paint"),
    new TaskValueStr(key = "secondaryColor", value = "green paint"),
    new TaskValueStr(key = "tertiaryColor", value = "green-blue paint")
  ))

  paintNames.append(Array(
    new TaskValueStr(key = "inputChemicalsSecondary", value = "blue paint,yellow paint"),
    new TaskValueStr(key = "inputChemicalsTertiary", value = "green paint,yellow paint"),
    new TaskValueStr(key = "secondaryColor", value = "green paint"),
    new TaskValueStr(key = "tertiaryColor", value = "yellow-green paint")
  ))

  // Orange
  paintNames.append(Array(
    new TaskValueStr(key = "inputChemicalsSecondary", value = "red paint,yellow paint"),
    new TaskValueStr(key = "inputChemicalsTertiary", value = "orange paint,red paint"),
    new TaskValueStr(key = "secondaryColor", value = "orange paint"),
    new TaskValueStr(key = "tertiaryColor", value = "red-orange paint")
  ))

  paintNames.append(Array(
    new TaskValueStr(key = "inputChemicalsSecondary", value = "red paint,yellow paint"),
    new TaskValueStr(key = "inputChemicalsTertiary", value = "orange paint,yellow paint"),
    new TaskValueStr(key = "secondaryColor", value = "orange paint"),
    new TaskValueStr(key = "tertiaryColor", value = "yellow-orange paint")
  ))

  // Violet
  paintNames.append(Array(
    new TaskValueStr(key = "inputChemicalsSecondary", value = "red paint,blue paint"),
    new TaskValueStr(key = "inputChemicalsTertiary", value = "violet paint,red paint"),
    new TaskValueStr(key = "secondaryColor", value = "violet paint"),
    new TaskValueStr(key = "tertiaryColor", value = "violet-red paint")
  ))

  paintNames.append(Array(
    new TaskValueStr(key = "inputChemicalsSecondary", value = "red paint,blue paint"),
    new TaskValueStr(key = "inputChemicalsTertiary", value = "violet paint,blue paint"),
    new TaskValueStr(key = "secondaryColor", value = "violet paint"),
    new TaskValueStr(key = "tertiaryColor", value = "blue-violet paint")
  ))





  // Combinations
  val combinations = for {
    i <- additionalPaint
    j <- paintNames
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
    val secondaryColor = this.getTaskValueStr(modifiers, "secondaryColor")
    if (secondaryColor.isEmpty) throw new RuntimeException("ERROR: Failed to find secondary color in task setup.")
    val tertiaryColor = this.getTaskValueStr(modifiers, "tertiaryColor")
    if (tertiaryColor.isEmpty) throw new RuntimeException("ERROR: Failed to find tertiary color in task setup.")
    val inputColorsSecondary = this.getTaskValueStr(modifiers, "inputChemicalsSecondary").get.split(",")
    val inputColorsTertiary = this.getTaskValueStr(modifiers, "inputChemicalsTertiary").get.split(",")


    var subTask = ""
    val gSequence = new ArrayBuffer[Goal]
    val gSequenceUnordered = new ArrayBuffer[Goal]

    var description:String = "<empty>"
    if (mode == MODE_CHEMISTRY_MIX_PAINT_SECONDARY) {

      gSequence.append(new GoalFind(objectName = secondaryColor.get, failIfWrong = true, description = "focus on the mixing result (" + secondaryColor.get + ")"))

      for (inputChemical <- inputColorsSecondary) {
        gSequenceUnordered.append(new GoalInRoomWithObject(objectName = inputChemical, _isOptional = true, description = "be in same location as " + inputChemical))
      }
      gSequenceUnordered.append(new GoalObjectsInSingleContainer(objectNames = inputColorsSecondary, _isOptional = true, description = "have all ingredients alone in a single container"))
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = secondaryColor.get, _isOptional = true, description = "be in same location as " + secondaryColor.get))

      description = "Your task is to use chemistry to create " + secondaryColor.get + ". When you are done, focus on the " + secondaryColor.get + "."

    } else if (mode == MODE_CHEMISTRY_MIX_PAINT_TERTIARY) {

      gSequence.append(new GoalFind(objectName = secondaryColor.get, failIfWrong = true, _defocusOnSuccess = true, description = "focus on the intermediate mixing result (" + secondaryColor.get + ")"))
      gSequence.append(new GoalFind(objectName = tertiaryColor.get, failIfWrong = true, description = "focus on the final mixing result (" + tertiaryColor.get + ")"))

      // Secondary
      for (inputChemical <- inputColorsSecondary) {
        gSequenceUnordered.append(new GoalInRoomWithObject(objectName = inputChemical, _isOptional = true, description = "be in same location as " + inputChemical))
      }
      gSequenceUnordered.append(new GoalObjectsInSingleContainer(objectNames = inputColorsSecondary, _isOptional = true, description = "have all ingredients (secondary) alone in a single container"))
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = secondaryColor.get, _isOptional = true, description = "be in same location as " + secondaryColor.get))
      // Tertiary
      gSequenceUnordered.append(new GoalObjectsInSingleContainer(objectNames = inputColorsTertiary, _isOptional = true, description = "have all ingredients (tertiary) alone in a single container"))
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = tertiaryColor.get, _isOptional = true, description = "be in same location as " + tertiaryColor.get))


      description = "Your task is to use chemistry to create " + tertiaryColor.get + ". When you are part-way done, focus on the intermediate (secondary color) paint you created.  When you are completely done, focus on the " + secondaryColor.get + "."

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


object TaskChemistryMixPaint {
  val MODE_CHEMISTRY_MIX_PAINT_SECONDARY       = "chemistry mix paint (secondary color)"
  val MODE_CHEMISTRY_MIX_PAINT_TERTIARY        = "chemistry mix paint (tertiary color)"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskChemistryMixPaint(mode = MODE_CHEMISTRY_MIX_PAINT_SECONDARY) )
    taskMaker.addTask( new TaskChemistryMixPaint(mode = MODE_CHEMISTRY_MIX_PAINT_TERTIARY) )
  }

}