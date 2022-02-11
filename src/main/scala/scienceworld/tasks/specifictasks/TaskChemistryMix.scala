package scienceworld.tasks.specifictasks

import scienceworld.actions.Action
import scienceworld.objects.agent.Agent
import scienceworld.objects.containers.{CeramicCup, FlowerPot}
import scienceworld.objects.document.{Paper, Recipe}
import scienceworld.objects.livingthing.plant.{Plant, Soil}
import scienceworld.objects.substance.food.{Almond, Apple, Banana, Bread, Cachew, Chocolate, Flour, Jam, Marshmallow, Orange, Peanut}
import scienceworld.objects.substance.paint.{BluePaint, GreenPaint}
import scienceworld.objects.substance.{AceticAcid, IronBlock, Soap, SodiumBicarbonate, SodiumChloride, Sugar, Water}
import scienceworld.processes.PlantReproduction
import scienceworld.processes.lifestage.PlantLifeStages.{PLANT_STAGE_ADULT_PLANT, PLANT_STAGE_REPRODUCING, PLANT_STAGE_SEED, PLANT_STAGE_SEEDLING}
import scienceworld.runtime.pythonapi.PythonInterface
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalFind, GoalInRoomWithObject, GoalLifeStage, GoalMoveToNewLocation, GoalObjectsInSingleContainer, GoalPastActionExamineObject, GoalPastActionReadObject}
import scienceworld.tasks.specifictasks.TaskChemistryMix.MODE_CHEMISTRY_MIX
import scienceworld.tasks.specifictasks.TaskFindLivingNonLiving.MODE_LIVING

import scala.collection.mutable.ArrayBuffer

class TaskChemistryMix(val mode:String = MODE_LIVING) extends TaskParametric {
  val taskName = "task-5-" + mode.replaceAll(" ", "-")


  // Variation 1: Which seeds to grow
  val baseChemicals = new ArrayBuffer[ Array[TaskModifier] ]()
  val locations = Array("kitchen", "workshop")

  // Train
  for (location <- locations) {
    // Salt + water = salt water
    val salt = new SodiumChloride()
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "salt water", inputObjects = Array(salt, new Water()), generateLocation = location, excludeFromAdding = Array("water")))

    // Soap + water = soapy water
    val soap = new Soap()
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "soapy water", inputObjects = Array(soap, new Water()), generateLocation = location, excludeFromAdding = Array("water")))

    // Mixed nuts
    val peanut = new Peanut()
    val almond = new Almond()
    val cachew = new Cachew()
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "mixed nuts", inputObjects = Array(peanut, almond, cachew), generateLocation = location, excludeFromAdding = Array("water")))

    // Volcano
    val vinegar = new AceticAcid()
    val bakingsoda = new SodiumBicarbonate()
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "sodium acetate", inputObjects = Array(vinegar, bakingsoda), generateLocation = location, excludeFromAdding = Array("water")))

    // banana + bread = banana sandwhich
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "banana sandwhich", inputObjects = Array(new Banana(), new Bread()), generateLocation = location))

    // Paper + blue paint = blue paper  (paint should be found in art room)
    val paper1 = new Paper()
    val bluepaint = new BluePaint()
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "blue paper", inputObjects = Array(paper1, bluepaint), generateLocation = location, excludeFromAdding = Array(bluepaint.name)))

    // peanut + bread = peanut butter sandwhich
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "peanut butter sandwhich", inputObjects = Array(new Peanut(), new Bread()), generateLocation = location))

    // peanut + jam + bread = peanut butter with jam sandwhich
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "peanut butter with jam sandwhich", inputObjects = Array(new Peanut(), new Jam(), new Bread()), generateLocation = location))
  }

  // Dev
  for (location <- locations) {
    // chocolate + marshmallow = smores
    val chocolate = new Chocolate()
    val marshmallow = new Marshmallow()
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "smores", inputObjects = Array(chocolate, marshmallow), generateLocation = location))

    // Flour + water = dough
    val flour = new Flour()
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "dough", inputObjects = Array(flour, new Water()), generateLocation = location, excludeFromAdding = Array("water")))

    // Apple + Orange + Banana
    val apple = new Apple()
    val orange = new Orange()
    val banana = new Banana()
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "fruit salad", inputObjects = Array(apple, orange, banana), generateLocation = location))

    // jam + bread = peanut butter with jam sandwhich
    baseChemicals.append(TaskChemistryMix.setupRecipeTask(resultObject = "jam sandwhich", inputObjects = Array(new Peanut(), new Bread()), generateLocation = location))
  }

  // Test
  for (location <- locations) {
    // sugar + water = sugar water
    val sugar = new Sugar()
    baseChemicals.append( TaskChemistryMix.setupRecipeTask(resultObject = "sugar water", inputObjects = Array(sugar, new Water()), generateLocation = location, excludeFromAdding = Array("water")) )

    // peanut + banana + bread = banana sandwhich
    baseChemicals.append( TaskChemistryMix.setupRecipeTask(resultObject = "peanut butter with banana sandwhich", inputObjects = Array(new Peanut(), new Banana(), new Bread()), generateLocation = location) )

    // Iron + water = rusty iron
    val ironblock = new IronBlock()
    baseChemicals.append( TaskChemistryMix.setupRecipeTask(resultObject = "rust", inputObjects = Array(ironblock, new Water()), generateLocation = location, excludeFromAdding = Array("water")) )

    // Paper + green paint = green paper  (paint should be found in art room)
    val paper2 = new Paper()
    val greenpaint = new GreenPaint()
    baseChemicals.append( TaskChemistryMix.setupRecipeTask(resultObject = "green paper", inputObjects = Array(paper2, greenpaint), generateLocation = location, excludeFromAdding = Array(greenpaint.name)) )
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
    val inputChemicals = this.getTaskValueStr(modifiers, key = "inputChemicals").get.split(",")
    val location = this.getTaskValueStr(modifiers, key = "location")

    var subTask = ""
    val gSequence = new ArrayBuffer[Goal]
    val gSequenceUnordered = new ArrayBuffer[Goal]

    var description:String = "<empty>"
    if (mode == MODE_CHEMISTRY_MIX) {
      gSequence.append( new GoalFind(objectName = resultChemical.get, failIfWrong = true, description = "focus on result chemical (" + resultChemical.get + ")") )

      gSequenceUnordered.append(new GoalMoveToNewLocation(_isOptional = true, unlessInLocation = "", description = "move to a new location") )            // Move to any new location

      for (inputChemical <- inputChemicals) {
        gSequenceUnordered.append(new GoalInRoomWithObject(objectName = inputChemical, _isOptional = true, description = "be in same location as " + inputChemical))
      }
      gSequenceUnordered.append(new GoalObjectsInSingleContainer(objectNames = inputChemicals, _isOptional = true, description = "have all ingredients alone in a single container"))
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = resultChemical.get, _isOptional = true, description = "be in same location as " + resultChemical.get))
      gSequenceUnordered.append(new GoalPastActionReadObject(documentName = "recipe", _isOptional = true, description = "read recipe"))


      description = "Your task is to use chemistry to create the substance '" + resultChemical.get + "'. "
      description += "A recipe and some of the ingredients might be found near the " + location.get + ". "
      description += "When you are done, focus on the " + resultChemical.get + "."

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


  def mkGoldActionSequence(modifiers:Array[TaskModifier], runner:PythonInterface): (Boolean, Array[String]) = {
    // TODO: Unimplemented
    return (false, Array.empty[String])
  }


}


object TaskChemistryMix {
  val MODE_CHEMISTRY_MIX       = "chemistry mix"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskChemistryMix(mode = MODE_CHEMISTRY_MIX) )
  }


  // Create task modifiers for adding the objects, adding a recipe, and including task keys
  def setupRecipeTask(resultObject:String, inputObjects:Array[EnvObject], generateLocation:String, excludeFromAdding:Array[String] = Array.empty[String]):Array[TaskModifier] = {
    val out = new ArrayBuffer[TaskModifier]

    // Add objects
    for (inputObject <- inputObjects) {
      if (!excludeFromAdding.contains(inputObject.name) ) {
        out.append(new TaskObject(inputObject.name, Some(inputObject), roomToGenerateIn = generateLocation, Array.empty[String], generateNear = 0))
      }
    }

    // Keys
    out.append( new TaskValueStr(key = "inputChemicals", value = inputObjects.map(_.name).toArray.mkString(",")) )
    out.append( new TaskValueStr(key = "result", value = resultObject) )
    out.append( new TaskValueStr(key = "location", value = generateLocation) )

    // Add recipe
    val recipe = Recipe.mkRecipe(resultObject, thingsToMix = inputObjects.map(_.name))
    out.append(new TaskObject(recipe.name, Some(recipe), roomToGenerateIn = generateLocation, Array.empty[String], generateNear = 0))

    out.toArray
  }

}