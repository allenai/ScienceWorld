package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.objects.containers.{CeramicCup, FlowerPot}
import scienceworld.objects.electricalcomponent.{Battery, ElectricBuzzer, ElectricMotor, GasGenerator, LightBulb, NuclearGenerator, SolarPanel, WindGenerator}
import scienceworld.objects.livingthing.plant.{Plant, Soil}
import scienceworld.processes.PlantReproduction
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalActivateDevice, GoalElectricallyConnected, GoalFind, GoalInRoomWithObject, GoalLifeStage, GoalMoveToNewLocation, GoalObjectConnectedToWire, GoalWireConnectsObjectAndAnyPowerSource}
import scienceworld.tasks.specifictasks.TaskElectricCircuit.{MODE_POWER_COMPONENT, MODE_POWER_COMPONENT_RENEWABLE}

import scala.collection.mutable.ArrayBuffer


class TaskElectricCircuit(val mode:String = MODE_POWER_COMPONENT) extends TaskParametric {
  val taskName = "task-2-" + mode.replaceAll(" ", "-")

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

    // Generate array of lights
    val components = new ArrayBuffer[TaskObject]
    for (color <- lightColors) {
      val lightbulb = new LightBulb(color)
      components.append( new TaskObject(lightbulb.name, Some(lightbulb), roomToGenerateIn = location, Array.empty[String], generateNear = 0) )
    }
    // Additional parts (motor)
    val electricMotor = new ElectricMotor()
    components.append(new TaskObject(electricMotor.name, Some(electricMotor), roomToGenerateIn = location, Array.empty[String], generateNear = 0))
    // Additional parts (buzzer)
    val electricBuzzer = new ElectricBuzzer()
    components.append(new TaskObject(electricBuzzer.name, Some(electricBuzzer), roomToGenerateIn = location, Array.empty[String], generateNear = 0))

    // Iterate through all possible components to power
    for (component <- components) {
      partToPower.append(components.toArray ++ Array( new TaskValueStr(key = "componentToPower", value = component.name) ))
    }
  }

  // Variation 3: Renewable power source
  val renewablePowerSource = new ArrayBuffer[ Array[TaskModifier] ]()

  for (location <- locations) {
    // Solar panel
    val solarPanel = new SolarPanel()
    renewablePowerSource.append( Array(
      new TaskObject(solarPanel.name, Some(solarPanel), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "renewableSource", value = solarPanel.name)
    ))

    // Wind generator
    val windGenerator = new WindGenerator()
    renewablePowerSource.append( Array(
      new TaskObject(windGenerator.name, Some(windGenerator), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "renewableSource", value = windGenerator.name)
    ))
  }

  // Variation 4: Non-renewable power source
  val nonrenewablePowerSource = new ArrayBuffer[ Array[TaskModifier] ]()
  for (location <- locations) {
    // Gas generator
    val gasGenerator = new GasGenerator()
    nonrenewablePowerSource.append( Array(
      new TaskObject(gasGenerator.name, Some(gasGenerator), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "nonrenewableSource", value = gasGenerator.name)
    ))

    // Nuclear generator
    val nuclearGenerator = new NuclearGenerator()
    nonrenewablePowerSource.append( Array(
      new TaskObject(nuclearGenerator.name, Some(nuclearGenerator), roomToGenerateIn = location, Array.empty[String], generateNear = 0),
      new TaskValueStr(key = "nonrenewableSource", value = nuclearGenerator.name)
    ))
  }


  // Combinations
  val combinations = for {
    i <- powerSource
    j <- partToPower
    k <- renewablePowerSource
    m <- nonrenewablePowerSource
  } yield List(i, j, k, m)

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
    val partToPower = this.getTaskValueStr(modifiers, key = "componentToPower")
    if (partToPower.isEmpty) throw new RuntimeException("ERROR: Unable to initialize task (componentToPower is undefined).")


    var subTask = ""
    val gSequence = new ArrayBuffer[Goal]
    val gSequenceUnordered = new ArrayBuffer[Goal]
    var description:String = "<empty>"
    if (mode == MODE_POWER_COMPONENT) {
      gSequence.append(new GoalFind(objectName = partToPower.get, failIfWrong = true, description = "focus on task object"))
      gSequence.append(new GoalActivateDevice(deviceName = partToPower.get, description = "power task object"))

      // Unordered
      gSequenceUnordered.append(new GoalMoveToNewLocation(_isOptional = true, unlessInLocation = "", description = "move to a new location") )            // Move to any new location
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = partToPower.get, _isOptional = true, description = "be in same location as part to power"))

      // Connect the component to a wire on either side
      gSequenceUnordered.append(new GoalObjectConnectedToWire(partToPower.get, terminal1 = true, terminal2 = false, anode = true, cathode = false, description = "Connect the task object's (terminal1/anode) to a wire"))
      gSequenceUnordered.append(new GoalObjectConnectedToWire(partToPower.get, terminal1 = false, terminal2 = true, anode = false, cathode = true, description = "Connect the task object's (terminal2/cathode) to a wire"))

      // Connect a wire between at least one side of the component, and one side of a power source (e.g. battery)
      gSequenceUnordered.append(new GoalWireConnectsObjectAndAnyPowerSource(partToPower.get, description = "Task object is at least partially connected to power source through wire"))


      description = "Your task is to turn on the " + partToPower.get + ". First, focus on the " + partToPower.get + ". Then, create an electrical circuit that powers it on. "

    } else if (mode == MODE_POWER_COMPONENT_RENEWABLE) {
      // Randomly pick whether the user should use the renewable or non-renewable power source, based on the combination #
      val renewablePowerSource = this.getTaskValueStr(modifiers, key = "renewableSource")
      val nonrenewablePowerSource = this.getTaskValueStr(modifiers, key = "nonrenewableSource")
      var powerSourceToUse:Option[String] = None
      var powerSourceDescription = ""
      if (combinationNum % 2 == 0) {
        powerSourceDescription = "renewable"
        powerSourceToUse = renewablePowerSource
      } else {
        powerSourceDescription = "nonrenewable"
        powerSourceToUse = nonrenewablePowerSource
      }

      gSequence.append(new GoalFind(objectName = partToPower.get, failIfWrong = true, description = "focus on task object"))
      gSequence.append(new GoalActivateDevice(deviceName = partToPower.get, description = "power task object"))
      gSequence.append(new GoalElectricallyConnected(connectedPartName = powerSourceToUse.get, failIfWrong = true, description = "use correct power source"))

      // Unordered
      gSequenceUnordered.append(new GoalMoveToNewLocation(_isOptional = true, unlessInLocation = "", description = "move to a new location") )            // Move to any new location
      gSequenceUnordered.append(new GoalInRoomWithObject(objectName = partToPower.get, _isOptional = true, description = "be in same location as part to power"))

      // Connect the component to a wire on either side
      gSequenceUnordered.append(new GoalObjectConnectedToWire(partToPower.get, terminal1 = true, terminal2 = false, anode = true, cathode = false, description = "connect the task object's (terminal1/anode) to a wire"))
      gSequenceUnordered.append(new GoalObjectConnectedToWire(partToPower.get, terminal1 = false, terminal2 = true, anode = false, cathode = true, description = "connect the task object's (terminal2/cathode) to a wire"))

      // Connect a wire between at least one side of the component, and one side of the correct power source (e.g. solar panel)
      gSequenceUnordered.append(new GoalWireConnectsObjectAndAnyPowerSource(partToPower.get, powerSourceName = powerSourceToUse.get, description = "task object is at least partially connected to power source through wire"))


      // TODO: Add goal condition that checks that the appropriate power source was used
      description = "Your task is to turn on the " + partToPower.get + " by powering it using a " + powerSourceDescription + " power source. First, focus on the " + partToPower.get + ". Then, create an electrical circuit that powers it on. "

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

}


object TaskElectricCircuit {
  val MODE_POWER_COMPONENT              = "power component"
  val MODE_POWER_COMPONENT_RENEWABLE    = "power component (renewable vs nonrenewable energy)"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskElectricCircuit(mode = MODE_POWER_COMPONENT) )
    taskMaker.addTask( new TaskElectricCircuit(mode = MODE_POWER_COMPONENT_RENEWABLE) )
  }

}

