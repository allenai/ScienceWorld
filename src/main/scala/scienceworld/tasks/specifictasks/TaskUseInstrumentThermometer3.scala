package scienceworld.tasks.specifictasks

import scienceworld.environments.ContainerMaker
import scienceworld.objects.agent.Agent
import scienceworld.objects.devices.Thermometer
import scienceworld.objects.substance.{Gallium, Lead, Mercury, Tin, Water}
import scienceworld.objects.substance.food.{Chocolate, OrangeJuice}
import scienceworld.objects.taskitems.{AnswerBox, UnknownSubstanceThermal}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueDouble, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalActivateDeviceWithName, GoalFind, GoalFindAnswerBox, GoalInRoomWithObject, GoalMoveToLocation, GoalMoveToNewLocation, GoalObjectInContainer, GoalObjectsInSingleContainer, GoalPastActionUseObjectOnObject, GoalSpecificObjectInDirectContainer, GoalTemperatureIncrease, GoalTemperatureOnFire}
import TaskUseInstrumentThermometer3._
import scienceworld.actions.Action
import scienceworld.runtime.pythonapi.PythonInterface

import scala.collection.mutable.ArrayBuffer
import scala.util.Random


class TaskUseInstrumentThermometer3(val mode:String = MODE_MEASURE_MELTING_UNKNOWN) extends TaskParametric {
  val taskName = "task-10-" + mode.replaceAll(" ", "-")

  val locations = Array("kitchen")

  // Variation 0: Always add the thermometer
  val instrument = new ArrayBuffer[ Array[TaskModifier] ]()
  val thermometer = new Thermometer()
  instrument.append( Array( new TaskObject(thermometer.name, Some(thermometer), roomToGenerateIn = "kitchen", Array.empty[String], generateNear = 0),
    new TaskValueStr(key = "instrumentName", value = thermometer.name)
  ))

  // Variation 1: Temperature point (above/below X degrees C)
  val temperaturePointPresets = Array(-50, 10, 50, 150, 250)
  val temperaturePoints = new ArrayBuffer[ Array[TaskModifier] ]()
  for (tempPoint <- temperaturePointPresets) {
    temperaturePoints.append( Array(new TaskValueDouble(key = "temperaturePoint", value = tempPoint)) )
  }

  // Variation 2+3: Substance to measure the temperature of, and temperature of substance
  val objectToTest = new ArrayBuffer[ Array[TaskModifier] ]()
  for (location <- locations) {
    for (tempPoint <- temperaturePointPresets) {

      // Unknown (unnamed) substances)
      for (tempPoint <- temperaturePointPresets) {
        for (i <- 0 until 20) {
          // Randomly generate melting point
          var meltingPoint:Double = 0.0
          if (Random.nextBoolean() == true) meltingPoint = tempPoint + 25 else meltingPoint = tempPoint - 25

          // Create unknown substance
          val unknownSubstance = UnknownSubstanceThermal.mkRandomSubstanceMeltingPoint(letterName = (65+i).toChar.toString)

          //unknownSubstance.propMaterial.get.temperatureC = TaskUseInstrumentThermometer.mkRandTemp(tempPoint)
          objectToTest.append(Array(
            new TaskObject(unknownSubstance.name, Some(unknownSubstance), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
            new TaskValueStr(key = "objectName", value = unknownSubstance.name),
            new TaskValueStr(key = "location", value = location),
            new TaskValueDouble(key = "meltingPoint", value = unknownSubstance.propMaterial.get.meltingPoint)
          ))

        }
      }

    }
  }

  // Variation 4: Answer boxes
  val answerBoxes = new ArrayBuffer[ Array[TaskModifier] ]()
  val answerBoxColors = Array("red", "green", "blue", "orange", "yellow", "purple")
  for (location <- locations) {
    for (i <- 0 until answerBoxColors.length-1) {
      val colorAbove = answerBoxColors(i)
      val colorBelow = answerBoxColors(i+1)
      val boxAbove = new AnswerBox(colorAbove)
      val boxBelow = new AnswerBox(colorBelow)
      answerBoxes.append(Array(
        new TaskObject(boxAbove.name, Some(boxAbove), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
        new TaskObject(boxBelow.name, Some(boxBelow), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
        new TaskValueStr(key = "boxAbove", boxAbove.name),
        new TaskValueStr(key = "boxBelow", boxBelow.name),
        new TaskValueStr(key = "locationAnswerBox", location)
      ))
    }
  }

  // Combinations
  val combinations = for {
    h <- instrument
    j <- objectToTest
    i <- temperaturePoints
    k <- answerBoxes
  } yield List(h, i, j, k)

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
    // Step 1: Extract modifier setup
    val objectName = this.getTaskValueStr(modifiers, "objectName")
    val objectLocation = this.getTaskValueStr(modifiers, "location")
    val tempPoint = this.getTaskValueDouble(modifiers, "temperaturePoint")
    val meltingPoint = this.getTaskValueDouble(modifiers, "meltingPoint")
    val instrumentName = this.getTaskValueStr(modifiers, "instrumentName")
    val boxAbove = this.getTaskValueStr(modifiers, "boxAbove")
    val boxBelow = this.getTaskValueStr(modifiers, "boxBelow")
    val boxLocation = this.getTaskValueStr(modifiers, key = "locationAnswerBox")

    var subTask = ""
    val gSequence = new ArrayBuffer[Goal]
    val gSequenceUnordered = new ArrayBuffer[Goal]
    var description:String = "<empty>"
    if (mode == MODE_MEASURE_MELTING_UNKNOWN) {
      // Figure out the correct answer container based on the object's conductivity

      // Goal sequence
      gSequence.append(new GoalFind(objectName = instrumentName.get, failIfWrong = true, _defocusOnSuccess = true, description = "focus on thermometer"))
      gSequence.append(new GoalFind(objectName = objectName.get, failIfWrong = true, _defocusOnSuccess = false, key = "focusObject", description = "focus on substance"))   // Keep focus
      if (meltingPoint.get >= tempPoint.get) {
        println ("FOCUS 1")
        gSequence.append(new GoalFindAnswerBox(objectName = boxAbove.get, failIfWrong = true, description = "focus on correct answer box"))
      } else {
        println ("FOCUS 2")
        gSequence.append(new GoalFindAnswerBox(objectName = boxBelow.get, failIfWrong = true, description = "focus on correct answer box"))
      }

      // Thermometer
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = "thermometer", _isOptional = true, description = "be in same location as thermometer"))
      gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName = "inventory", validObjectNames = Array("thermometer"), _isOptional = true, description = "have thermometer in inventory"))

      // Moving to helpful locations
      gSequenceUnordered.append(new GoalMoveToNewLocation(_isOptional = true, unlessInLocation = "", description = "move to a new location") )            // Move to any new location
      gSequenceUnordered.append(new GoalMoveToLocation(objectLocation.get, _isOptional = true, key = "move1", description = "move to the location asked by the task (substance location)") )
      gSequenceUnordered.append(new GoalMoveToLocation(boxLocation.get, _isOptional = true, key = "move1", description = "move to the location asked by the task (answer box location)") )
      gSequenceUnordered.append(new GoalMoveToLocation("kitchen", _isOptional = true, key = "move2a", keysMustBeCompletedBefore = Array("move1"), description = "move to a location with a heating device (kitchen)") )
      gSequenceUnordered.append(new GoalMoveToLocation("outside", _isOptional = true, key = "move2b", keysMustBeCompletedBefore = Array("move1"), description = "move to a location with a heating device (outside)") )
      gSequenceUnordered.append(new GoalMoveToLocation("foundry", _isOptional = true, key = "move2c", keysMustBeCompletedBefore = Array("move1"), description = "move to a location with a heating device (foundry)") )

      // Pick up substance (potentially useful)
      gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName = "inventory", validObjectNames = Array(objectName.get), _isOptional = true, description = "have task object in inventory"))

      // Use thermometer on substance
      gSequenceUnordered.append(new GoalPastActionUseObjectOnObject(deviceName = instrumentName.get, patientObjectName = objectName.get, _isOptional = true, description = "use thermometer on substance"))
      gSequenceUnordered.append(new GoalPastActionUseObjectOnObject(deviceName = instrumentName.get, patientObjectName = objectName.get, _isOptional = true, keysMustBeCompletedBefore = Array("heatObject"), description = "use thermometer on substance (after it has been heated)"))

      // Have the substance alone in a single container
      gSequenceUnordered.append(new GoalObjectsInSingleContainer(objectNames = Array(objectName.get), _isOptional = true, description = "have substance alone in a single container"))

      // Activate a heating device
      gSequenceUnordered.append(new GoalActivateDeviceWithName(deviceName = "stove", _isOptional = true, description = "activate heater (stove)"))
      gSequenceUnordered.append(new GoalActivateDeviceWithName(deviceName = "blast furnace", _isOptional = true, description = "activate heater (blast furnace)"))
      gSequenceUnordered.append(new GoalActivateDeviceWithName(deviceName = "oven", _isOptional = true, description = "activate heater (oven)"))
      gSequenceUnordered.append(new GoalActivateDeviceWithName(deviceName = "hot plate", _isOptional = true, description = "activate heater (hot plate)"))
      // Or, build a fire in the fire pit
      gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName = "inventory", validObjectNames = Array("lighter"), _isOptional = true, description = "have lighter in inventory"))
      gSequenceUnordered.append(new GoalSpecificObjectInDirectContainer(containerName = "fire pit", validObjectNames = Array("wood"), _isOptional = true, description = "move wood into fire pit", key = "wood1"))
      gSequenceUnordered.append(new GoalTemperatureOnFire(objectName = "wood", _isOptional = true, description = "ignite wood", key = "ignite", keysMustBeCompletedBefore = Array("wood1")) )

      // Put the substance on a heating device
      gSequenceUnordered.append(new GoalObjectInContainer(containerName = "stove", _isOptional = true, keysMustBeCompletedBefore = Array("focusObject"), description = "have substance on heater (stove)"))
      gSequenceUnordered.append(new GoalObjectInContainer(containerName = "blast furnace", _isOptional = true, keysMustBeCompletedBefore = Array("focusObject"), description = "have substance on heater (blast furnace)"))
      gSequenceUnordered.append(new GoalObjectInContainer(containerName = "oven", _isOptional = true, keysMustBeCompletedBefore = Array("focusObject"), description = "have substance on heater (oven)"))
      gSequenceUnordered.append(new GoalObjectInContainer(containerName = "hot plate", _isOptional = true, keysMustBeCompletedBefore = Array("focusObject"), description = "have substance on heater (hot plate)"))
      gSequenceUnordered.append(new GoalObjectInContainer(containerName = "fire pit", _isOptional = true, keysMustBeCompletedBefore = Array("focusObject"), description = "have substance on heater (fire pit)"))

      // Heat object (when the substance is in focus)
      gSequenceUnordered.append(new GoalTemperatureIncrease(minTempIncreaseC = 20.0, _isOptional = true, key = "heatObject", keysMustBeCompletedBefore = Array("focusObject"), description = "heat substance by at least 20C"))





      // Description
      description = "Your task is to measure the melting point of " + objectName.get + ", which is located around the " + objectLocation.get + ". "
      description += "First, focus on the thermometer. Next, focus on the " + objectName.get + ". "
      description += "If the melting point of " + objectName.get + " is above " + tempPoint.get + " degrees, focus on the " + boxAbove.get + ". "
      description += "If the melting point of " + objectName.get + " is below " + tempPoint.get + " degrees, focus on the " + boxBelow.get + ". "
      description += "The boxes are located around the " + boxLocation.get + ". "

    } else {
      throw new RuntimeException("ERROR: Unrecognized task mode: " + mode)
    }

    val taskLabel = taskName + "-variation" + combinationNum
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


object TaskUseInstrumentThermometer3 {
  val MODE_MEASURE_MELTING_UNKNOWN          = "measure melting point (unknown substance)"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskUseInstrumentThermometer3(mode = MODE_MEASURE_MELTING_UNKNOWN) )
  }

  // Make a random temperature that's 50-150C above/below 'tempPoint'
  def mkRandTemp(tempPoint:Double):Double = {
    var tempDelta = Random.nextInt(100) + 50                      // Randomly generate a number 0-100, then add 50 to it (so 50-150 range)
    if (Random.nextBoolean() == true) tempDelta = -tempDelta      // Randomly change the sign (+/- 50-150)
    val randTemp = tempPoint + tempDelta                          // Create a temperature that's randomly +/- 50-150 above/below the temperature set point

    return math.floor( randTemp )
  }

}

