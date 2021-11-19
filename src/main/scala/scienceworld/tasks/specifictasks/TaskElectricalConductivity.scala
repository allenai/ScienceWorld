package scienceworld.tasks.specifictasks

import scienceworld.objects.{SodiumChloride, Water}
import scienceworld.objects.agent.Agent
import scienceworld.objects.electricalcomponent.{Battery, ElectricBuzzer, ElectricMotor, GasGenerator, LightBulb, NuclearGenerator, SolarPanel, WindGenerator}
import scienceworld.objects.misc.{ForkMetal, ForkPlastic}
import scienceworld.objects.taskitems.{AnswerBox, UnknownSubstanceElectricalConductivity}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueBool, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalActivateDevice, GoalElectricallyConnected, GoalFind, GoalObjectInContainer, GoalObjectInContainerByName}
import scienceworld.tasks.specifictasks.TaskElectricalConductivity.{MODE_TEST_CONDUCTIVITY, MODE_TEST_CONDUCTIVITY_UNKNOWN}

import scala.collection.mutable.ArrayBuffer


class TaskElectricalConductivity(val mode:String = MODE_TEST_CONDUCTIVITY) extends TaskParametric {
  val taskName = "task-2a-" + mode.replaceAll(" ", "-")

  val locations = Array("workshop")

  // Variation 1: Battery
  val powerSource = new ArrayBuffer[ Array[TaskModifier] ]()
  for (location <- locations) {
    val battery = new Battery()
    powerSource.append(Array(
      new TaskObject(battery.name, Some(battery), roomToGenerateIn = location, Array.empty[String], generateNear = 0)
    ))
  }

  // Variation 2: Part to power
  val partToPower = new ArrayBuffer[ Array[TaskModifier] ]()
  val lightColors = Array("red", "green", "blue")

  for (location <- locations) {
    for (color <- lightColors) {
      val lightbulb = new LightBulb(color)
      partToPower.append( Array( new TaskObject(lightbulb.name, Some(lightbulb), roomToGenerateIn = location, Array.empty[String], generateNear = 0) ))
    }
    // Additional parts (motor)
    val electricMotor = new ElectricMotor()
    partToPower.append( Array(new TaskObject(electricMotor.name, Some(electricMotor), roomToGenerateIn = location, Array.empty[String], generateNear = 0) ))
    // Additional parts (buzzer)
    val electricBuzzer = new ElectricBuzzer()
    partToPower.append( Array(new TaskObject(electricBuzzer.name, Some(electricBuzzer), roomToGenerateIn = location, Array.empty[String], generateNear = 0) ))
  }

  // Variation 3: Substance to test
  val substanceToTest = new ArrayBuffer[ Array[TaskModifier] ]()
  for (location <- locations) {
    // Substance 1: Salt
    val salt = new SodiumChloride()
    substanceToTest.append(Array(
      new TaskObject(salt.name, Some(salt), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = salt.name),
      new TaskValueBool(key = "isConductive", value = salt.propMaterial.get.electricallyConductive)
    ))

    // Substance 1: Water
    val water = new Water()
    substanceToTest.append(Array(
      //new TaskObject(salt.name, Some(salt), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = water.name),
      new TaskValueBool(key = "isConductive", value = water.propMaterial.get.electricallyConductive)
    ))

    // Substance 1: Plastic fork
    val plasticfork = new ForkPlastic()
    substanceToTest.append(Array(
      new TaskObject(plasticfork.name, Some(plasticfork), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = plasticfork.name),
      new TaskValueBool(key = "isConductive", value = plasticfork.propMaterial.get.electricallyConductive)
    ))

    // Substance 1: Plastic fork
    val metalfork = new ForkMetal()
    substanceToTest.append(Array(
      new TaskObject(metalfork.name, Some(metalfork), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = metalfork.name),
      new TaskValueBool(key = "isConductive", value = metalfork.propMaterial.get.electricallyConductive)
    ))
  }

  val unknownSubstances = new ArrayBuffer[ Array[TaskModifier] ]()
  for (location <- locations) {
    for (i <- 0 until 20) {
      val unknownSubstance = UnknownSubstanceElectricalConductivity.mkRandomSubstanceElectricalConductive()
      unknownSubstances.append(Array(
        new TaskObject(unknownSubstance.name, Some(unknownSubstance), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
        new TaskValueStr(key = "unknownSubstance", value = unknownSubstance.name),
        new TaskValueBool(key = "unknownIsConductive", value = unknownSubstance.propMaterial.get.electricallyConductive)
      ))
    }
  }

  // Variation 4: Answer boxes
  val answerBoxes = new ArrayBuffer[ Array[TaskModifier] ]()
  val answerBoxColors = Array("red", "green", "blue", "orange", "yellow", "purple")
  for (location <- locations) {
    for (i <- 0 until answerBoxColors.length-1) {
      val colorConductive = answerBoxColors(i)
      val colorNonconductive = answerBoxColors(i+1)
      val boxC = new AnswerBox(colorConductive)
      val boxNC = new AnswerBox(colorNonconductive)
      answerBoxes.append(Array(
        new TaskObject(boxC.name, Some(boxC), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
        new TaskObject(boxNC.name, Some(boxNC), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
        new TaskValueStr(key = "conductive", boxC.name),
        new TaskValueStr(key = "nonconductive", boxNC.name)
      ))
    }
  }

  // Combinations
  val combinations = for {
    i <- powerSource
    j <- partToPower
    k <- substanceToTest
    m <- unknownSubstances
    n <- answerBoxes
  } yield List(i, j, k, m, n)

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
    val boxNameConductive = this.getTaskValueStr(modifiers, "conductive")
    val boxNameNonconductive = this.getTaskValueStr(modifiers, "nonconductive")
    val unknownSubstanceName = this.getTaskValueStr(modifiers, "unknownSubstance")
    val unknownSubstanceConductive = this.getTaskValueBool(modifiers, "unknownIsConductive")
    val specificSubstanceName = this.getTaskValueStr(modifiers, "substance")
    val specificSubstanceConductive = this.getTaskValueBool(modifiers, key = "isConductive")


    var subTask = ""
    val gSequence = new ArrayBuffer[Goal]
    var description:String = "<empty>"
    if (mode == MODE_TEST_CONDUCTIVITY) {
      // Figure out the correct answer container based on the object's conductivity
      var correctContainerName: String = ""
      var incorrectContainerName: String = ""
      if (specificSubstanceConductive.get == true) {
        // Object is conductive
        correctContainerName = boxNameConductive.get
        incorrectContainerName = boxNameNonconductive.get
      } else {
        // Object is non-conductive
        correctContainerName = boxNameNonconductive.get
        incorrectContainerName = boxNameConductive.get
      }

      // Goal sequence
      gSequence.append(new GoalFind(objectName = specificSubstanceName.get, failIfWrong = true))
      gSequence.append(new GoalObjectInContainerByName(containerName = correctContainerName, failureContainers = List(incorrectContainerName))) // Then, make sure it's in the correct answer container

      // Description
      description = "Your task is to determine if " + specificSubstanceName.get + " is electrically conductive. "
      description += "First, focus on the " + specificSubstanceName.get + ". "
      description += "If it is electrically conductive, place it in the " + boxNameConductive.get + ". "
      description += "If it is electrically nonconductive, place it in the " + boxNameNonconductive.get + ". "

    } else if (mode == MODE_TEST_CONDUCTIVITY_UNKNOWN) {
      // Figure out the correct answer container based on the object's conductivity
      var correctContainerName: String = ""
      var incorrectContainerName: String = ""
      if (unknownSubstanceConductive.get == true) {
        // Object is conductive
        correctContainerName = boxNameConductive.get
        incorrectContainerName = boxNameNonconductive.get
      } else {
        // Object is non-conductive
        correctContainerName = boxNameNonconductive.get
        incorrectContainerName = boxNameConductive.get
      }

      // Goal sequence
      gSequence.append(new GoalFind(objectName = unknownSubstanceName.get, failIfWrong = true))
      gSequence.append(new GoalObjectInContainerByName(containerName = correctContainerName, failureContainers = List(incorrectContainerName))) // Then, make sure it's in the correct answer container

      // Description
      description = "Your task is to determine if " + unknownSubstanceName.get + " is electrically conductive. "
      description += "First, focus on the " + unknownSubstanceName.get + ". "
      description += "If it is electrically conductive, place it in the " + boxNameConductive.get + ". "
      description += "If it is electrically nonconductive, place it in the " + boxNameNonconductive.get + ". "

    } else {
      throw new RuntimeException("ERROR: Unrecognized task mode: " + mode)
    }

    val taskLabel = taskName + "-variation" + combinationNum
    val goalSequence = new GoalSequence(gSequence.toArray)

    val task = new Task(taskName, description, goalSequence)

    // Return
    return task
  }

  def setupGoals(combinationNum:Int): Task = {
    this.setupGoals( this.getCombination(combinationNum), combinationNum )
  }

}


object TaskElectricalConductivity {
  val MODE_TEST_CONDUCTIVITY            = "test conductivity"
  val MODE_TEST_CONDUCTIVITY_UNKNOWN    = "test conductivity of unknown substances"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskElectricalConductivity(mode = MODE_TEST_CONDUCTIVITY) )
    taskMaker.addTask( new TaskElectricalConductivity(mode = MODE_TEST_CONDUCTIVITY_UNKNOWN) )
  }

}

