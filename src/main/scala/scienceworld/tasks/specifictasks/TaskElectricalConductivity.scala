package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.objects.containers.{GlassJar, MetalPot}
import scienceworld.objects.devices.Shovel
import scienceworld.objects.document.BookOriginOfSpecies
import scienceworld.objects.electricalcomponent.{Battery, ElectricBuzzer, ElectricMotor, GasGenerator, LightBulb, NuclearGenerator, SolarPanel, WindGenerator}
import scienceworld.objects.misc.{AluminumFoil, ForkMetal, ForkPlastic, PaperClip}
import scienceworld.objects.substance.food.Apricot
import scienceworld.objects.substance.{SodiumChloride, Water, WoodBlock}
import scienceworld.objects.taskitems.{AnswerBox, UnknownSubstanceElectricalConductivity}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueBool, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalActivateDevice, GoalElectricallyConnected, GoalFind, GoalInRoomWithObject, GoalMoveToNewLocation, GoalObjectConnectedToWire, GoalObjectInContainer, GoalObjectInContainerByName, GoalWireConnectsObjectAndAnyLightBulb, GoalWireConnectsObjectAndAnyPowerSource, GoalWireConnectsPowerSourceAndAnyLightBulb}
import scienceworld.tasks.specifictasks.TaskElectricalConductivity.MODE_TEST_CONDUCTIVITY

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

    // Substance 2: Water
    val water = new Water()
    substanceToTest.append(Array(
      //new TaskObject(salt.name, Some(salt), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = water.name),
      new TaskValueBool(key = "isConductive", value = water.propMaterial.get.electricallyConductive)
    ))

    // Substance 3: Plastic fork
    val plasticfork = new ForkPlastic()
    substanceToTest.append(Array(
      new TaskObject(plasticfork.name, Some(plasticfork), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = plasticfork.name),
      new TaskValueBool(key = "isConductive", value = plasticfork.propMaterial.get.electricallyConductive)
    ))

    // Substance 4: Metal fork
    val metalfork = new ForkMetal()
    substanceToTest.append(Array(
      new TaskObject(metalfork.name, Some(metalfork), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = metalfork.name),
      new TaskValueBool(key = "isConductive", value = metalfork.propMaterial.get.electricallyConductive)
    ))

    // Substance 5: Apricot
    val apricot = new Apricot()
    substanceToTest.append(Array(
      new TaskObject(apricot.name, Some(apricot), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = apricot.name),
      new TaskValueBool(key = "isConductive", value = apricot.propMaterial.get.electricallyConductive)
    ))

    // Substance 6: Shovel
    val shovel = new Shovel()
    substanceToTest.append(Array(
      new TaskObject(shovel.name, Some(shovel), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = shovel.name),
      new TaskValueBool(key = "isConductive", value = shovel.propMaterial.get.electricallyConductive)
    ))

    // Substance 7: Glass jar
    val glassjar = new GlassJar()
    substanceToTest.append(Array(
      new TaskObject(glassjar.name, Some(glassjar), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = glassjar.name),
      new TaskValueBool(key = "isConductive", value = glassjar.propMaterial.get.electricallyConductive)
    ))

    // Substance 8: Metal pot
    val metalpot = new MetalPot()
    substanceToTest.append(Array(
      new TaskObject(metalpot.name, Some(metalpot), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = metalpot.name),
      new TaskValueBool(key = "isConductive", value = metalpot.propMaterial.get.electricallyConductive)
    ))

    // Substance 9: Drawing
    val book = new BookOriginOfSpecies()
    substanceToTest.append(Array(
      new TaskObject(book.name, Some(book), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = book.name),
      new TaskValueBool(key = "isConductive", value = book.propMaterial.get.electricallyConductive)
    ))

    // Substance 10: Paper Clip
    val paperclip = new PaperClip()
    substanceToTest.append(Array(
      new TaskObject(paperclip.name, Some(paperclip), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = paperclip.name),
      new TaskValueBool(key = "isConductive", value = paperclip.propMaterial.get.electricallyConductive)
    ))

    // Substance 11: Wood Block
    val woodblock = new WoodBlock()
    substanceToTest.append(Array(
      new TaskObject(woodblock.name, Some(woodblock), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = woodblock.name),
      new TaskValueBool(key = "isConductive", value = woodblock.propMaterial.get.electricallyConductive)
    ))

    // Substance 12: Aluminum Foil
    val aluminumfoil = new AluminumFoil()
    substanceToTest.append(Array(
      new TaskObject(aluminumfoil.name, Some(aluminumfoil), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "substance", value = aluminumfoil.name),
      new TaskValueBool(key = "isConductive", value = aluminumfoil.propMaterial.get.electricallyConductive)
    ))

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
    n <- substanceToTest
    i <- powerSource
    j <- partToPower
    k <- answerBoxes
  } yield List(i, j, k, n)

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
    val gSequenceUnordered = new ArrayBuffer[Goal]()
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
      gSequence.append(new GoalFind(objectName = specificSubstanceName.get, failIfWrong = true, description = "focus on task object"))
      gSequence.append(new GoalObjectInContainerByName(containerName = correctContainerName, failureContainers = List(incorrectContainerName), description = "put object in correct container")) // Then, make sure it's in the correct answer container


      // Unordered
      gSequenceUnordered.append(new GoalMoveToNewLocation(_isOptional = true, unlessInLocation = "", description = "move to a new location") )            // Move to any new location
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = specificSubstanceName.get, _isOptional = true, description = "be in same location as part to power"))

      // Connect the component to a wire on either side
      gSequenceUnordered.append(new GoalObjectConnectedToWire(specificSubstanceName.get, terminal1 = true, terminal2 = false, anode = true, cathode = false, description = "connect the task object's (terminal1/anode) to a wire"))
      gSequenceUnordered.append(new GoalObjectConnectedToWire(specificSubstanceName.get, terminal1 = false, terminal2 = true, anode = false, cathode = true, description = "connect the task object's (terminal2/cathode) to a wire"))

      // Connect a wire between at least one side of the component, and one side of the correct power source (e.g. solar panel)
      gSequenceUnordered.append(new GoalWireConnectsObjectAndAnyPowerSource(specificSubstanceName.get, "", description = "task object is at least partially connected to power source through wire"))
      gSequenceUnordered.append(new GoalWireConnectsObjectAndAnyLightBulb(specificSubstanceName.get, "", description = "task object is at least partially connected to a light bulb through wire"))
      gSequenceUnordered.append(new GoalWireConnectsPowerSourceAndAnyLightBulb(description = "light bulb is at least partially connected to a power source through wire"))


      // Description
      description = "Your task is to determine if " + specificSubstanceName.get + " is electrically conductive. "
      description += "First, focus on the " + specificSubstanceName.get + ". "
      description += "If it is electrically conductive, place it in the " + boxNameConductive.get + ". "
      description += "If it is electrically nonconductive, place it in the " + boxNameNonconductive.get + ". "

    } else {
      throw new RuntimeException("ERROR: Unrecognized task mode: " + mode)
    }

    val taskLabel = taskName + "-variation" + combinationNum
    val goalSequence = new GoalSequence(gSequence.toArray, gSequenceUnordered.toArray)

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

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskElectricalConductivity(mode = MODE_TEST_CONDUCTIVITY) )
  }

}

