package scienceworld.tasks.specifictasks

import scienceworld.environments.ContainerMaker
import scienceworld.objects.agent.Agent
import scienceworld.objects.devices.Stove
import scienceworld.objects.{AppleJuice, Caesium, Chocolate, Gallium, Ice, IceCream, Lead, Marshmallow, Mercury, OrangeJuice, Soap, Tin}
import scienceworld.properties.LeadProp
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker, TaskMaker1}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalChangeStateOfMatter, GoalIsDifferentStateOfMatter, GoalIsNotStateOfMatter, GoalIsStateOfMatter}
import scienceworld.tasks.specifictasks.TaskChangeOfState.{MODE_BOIL, MODE_CHANGESTATE, MODE_FREEZE, MODE_MELT}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}


class TaskChangeOfState(val mode:String = MODE_CHANGESTATE) extends TaskParametric {
  val taskName = "task-1-" + mode.replaceAll(" ", "-")

  val substancePossibilities = new ArrayBuffer[ Array[TaskModifier] ]()
  // Example of water (found in the environment)
  substancePossibilities.append( Array(new TaskObject("water", None, "", Array.empty[String], 0) ))
  // Example of ice (needs to be generated)
  substancePossibilities.append( Array(new TaskObject("ice", Some(new Ice), "kitchen", Array("freezer"), 0) ))
  // Example of something needing to be generated
  substancePossibilities.append( Array(new TaskObject("orange juice", Some(ContainerMaker.mkRandomLiquidCup(new OrangeJuice)), "kitchen", Array("fridge"), 0) ))
  substancePossibilities.append( Array(new TaskObject("apple juice", Some(ContainerMaker.mkRandomLiquidCup(new AppleJuice)), "kitchen", Array("fridge"), 0) ))
  substancePossibilities.append( Array(new TaskObject("chocolate", Some(new Chocolate), "kitchen", Array("fridge"), 0) ))
  substancePossibilities.append( Array(new TaskObject("marshmallow", Some(new Marshmallow), roomToGenerateIn = "kitchen", Array("cupboard", "table", "desk"), generateNear = 0) ))
  substancePossibilities.append( Array(new TaskObject("ice cream", Some(ContainerMaker.mkRandomLiquidCup(new IceCream)), roomToGenerateIn = "kitchen", Array("freezer"), generateNear = 0) ))

  substancePossibilities.append( Array(new TaskObject("soap", Some(new Soap), roomToGenerateIn = "kitchen", Array("cupboard", "table", "desk"), generateNear = 0) ))
  substancePossibilities.append( Array(new TaskObject("rubber", Some(new Soap), roomToGenerateIn = "workshop", Array("table", "desk"), generateNear = 0) ))

  substancePossibilities.append( Array(new TaskObject("lead", Some(new Lead()), "workshop", Array("table", "desk"), 0) ))                // Metals
  substancePossibilities.append( Array(new TaskObject("tin", Some(new Tin()), "workshop", Array("table", "desk"), 0) ))
  substancePossibilities.append( Array(new TaskObject("mercury", Some(new Mercury()), "workshop", Array("table", "desk"), 0) ))
  substancePossibilities.append( Array(new TaskObject("gallium", Some(new Gallium()), "workshop", Array("table", "desk"), 0) ))
  substancePossibilities.append( Array(new TaskObject("caesium", Some(new Caesium()), "workshop", Array("table", "desk"), 0) ))



  val toolPossibilities = new ArrayBuffer[ Array[TaskModifier] ]()
  // Case 1: Normal (stove in kitchen)
  toolPossibilities.append( Array(new TaskObject("stove", Some(new Stove), roomToGenerateIn = "kitchen", Array(""), generateNear = 0) ))
  // Case 2: Disable stove in kitchen (also should add an alternate)
  toolPossibilities.append( Array(new TaskDisable("stove", Array("kitchen") ) ) )


  // Combinations
  val combinations = for {
    i <- substancePossibilities
    j <- toolPossibilities
  } yield List(i, j)

  println("Number of combinations: " + combinations.length)

  def numCombinations():Int = this.combinations.size

  def getCombination(idx:Int):Array[TaskModifier] = {
    val out = new ArrayBuffer[TaskModifier]
    for (elem <- combinations(idx)) {
      out.insertAll(out.length, elem)
    }

    println ("* getCombination: out.length: " + out.length)
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
    // Step 1: Find substance name
    // NOTE: The first modifier here will be the substance to change the state of.
    val substanceModifier = modifiers(0)
    var substanceName = "<unknown>"
    substanceModifier match {
      case m:TaskObject => {
        substanceName = m.name
      }
      case _ => {
        throw new RuntimeException("ERROR: Unknown task modifier found, where substance modifier was expected." + substanceModifier.toString)
      }
    }

    var subTask = ""
    val gSequence = new ArrayBuffer[Goal]
    if (mode == MODE_CHANGESTATE) {
      subTask = "change the state of matter of"
      gSequence.append( new GoalIsStateOfMatter() )             // Be in any state
      gSequence.append( new GoalIsDifferentStateOfMatter() )    // Be in any state but the first state
    } else if (mode == MODE_MELT) {
      subTask = "melt"
      gSequence.append( new GoalChangeStateOfMatter("solid") )
      gSequence.append( new GoalChangeStateOfMatter("liquid") )
    } else if (mode == MODE_BOIL) {
      subTask = "boil"
      gSequence.append( new GoalChangeStateOfMatter("liquid") )
      gSequence.append( new GoalChangeStateOfMatter("gas") )
    } else if (mode == MODE_FREEZE) {
      subTask = "freeze"
      gSequence.append( new GoalChangeStateOfMatter("liquid") )
      gSequence.append( new GoalChangeStateOfMatter("solid") )
    } else {
      throw new RuntimeException("ERROR: Unrecognized task mode: " + mode)
    }

    val taskLabel = taskName + "-variation" + combinationNum
    val description = "Your task is to " + subTask + " " + substanceName + ". First, focus on the substance. Then, take actions that will cause it to change its state of matter. "
    val goalSequence = new GoalSequence(gSequence.toArray)

    val task = new Task(taskName, description, goalSequence)

    // Return
    return task
  }

  def setupGoals(combinationNum:Int): Task = {
    this.setupGoals( this.getCombination(combinationNum), combinationNum )
  }

}


//## DEBUG
object TaskChangeOfState {
  val MODE_CHANGESTATE  = "change the state of matter of"
  val MODE_MELT         = "melt"
  val MODE_BOIL         = "boil"
  val MODE_FREEZE       = "freeze"


  def main(args:Array[String]) = {
    val task = new TaskChangeOfState()
  }

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskChangeOfState(mode = MODE_CHANGESTATE) )
    taskMaker.addTask( new TaskChangeOfState(mode = MODE_MELT) )
    taskMaker.addTask( new TaskChangeOfState(mode = MODE_BOIL) )
    taskMaker.addTask( new TaskChangeOfState(mode = MODE_FREEZE) )
  }

}


// Base class
trait TaskModifier {

  // Run this modifier
  def runModifier(universe:EnvObject, agent:Agent):Boolean

  // Helper function: Find all objects in the environment with a given name
  def findObjectsWithName(queryName:String, envRoot:EnvObject):Set[EnvObject] = {
    val allObjects = envRoot.getContainedObjectsAndPortalsRecursive()
    val queryObjects = allObjects.filter(_.name == queryName)
    return queryObjects
  }

}

// An example of a specific object required for a task:
// (a) it's name,
// (b) if it needs to be generated (and isn't just found in the environment naturally), an example instance of it
// (c) if generation is required, what room to generate it in (set to "" for n/a)
// (d) if generation if required, what container name(s) are valid to place it into (e.g. "desk", "table").  If blank, it will be placed in the root container (e.g. the 'floor')
// (e) generateNear: If non-zero, it will generate the object within 'generateNear' steps of the original location.  e.g. if the generation location is 'kitchen', and generateNear is 2, then the object could be generated in (e.g.) the hallway or bathroom, but not a far-off location.
class TaskObject(val name:String, val exampleInstance:Option[EnvObject], val roomToGenerateIn:String, val possibleContainerNames:Array[String], val generateNear:Int=0, val disableRecursiveCheck:Boolean = false) extends TaskModifier {
  // Does this task object need to be generated in the environment?
  val needsToBeGenerated:Boolean = exampleInstance.isDefined

  // Add a given object to a given room.
  // TODO: Doesn't currently use 'generateNear' parameter.
  override def runModifier(universe: EnvObject, agent: Agent): Boolean = {
    println ("#### TaskObject: Running for (" + name + ")")

    // Step 1: Check to see if the object is marked as required to be generated
    if (!needsToBeGenerated) {
      println ("### TaskObject: Object (" + name + ") does not need to be generated.")
      return true
    }

    // Step 2: Check to see if the object already exists in the environment
    val allObjects:Set[EnvObject] = universe.getContainedObjectsAndPortalsRecursive()
    for (obj <- allObjects) {
      if (obj.name == roomToGenerateIn) {
        // First, check to see if the object already exists
        if ((possibleContainerNames.length == 0) || ((possibleContainerNames.length == 1) && (possibleContainerNames(0).length == 0))) {
          // CASE 1: Just generate in the main container
          var containedObjects = Set.empty[EnvObject]
          if (disableRecursiveCheck) {
            containedObjects = obj.getContainedObjects()
          } else {
            containedObjects = obj.getContainedObjectsRecursive()
          }

          for (cObj <- containedObjects) {
            if (cObj.name == name) {
              // An existing object with this name has been found -- exit
              println("### TaskObject: Existing object with that name (" + name + ") has been found in container (" + obj.name + ")")
              return true
            }
          }
        } else {
          // CASE 2: Generate in a sub-container of the main container
          val containedObjects = obj.getContainedObjects()

          for (cObj <- containedObjects) {
            if (possibleContainerNames.contains(cObj.name)) {
              // A relevant container has been found -- check all the objects in the container to see if an existing one has the same name
              var containedObjects1 = Set.empty[EnvObject]
              if (disableRecursiveCheck) {
                containedObjects1 = cObj.getContainedObjects()
              } else {
                containedObjects1 = cObj.getContainedObjectsRecursive()
              }

              for (ccObj <- containedObjects1) {
                if (ccObj.name == name) {
                  // An existing object with this name has been found -- exit
                  println("### TaskObject: Existing object with that name (" + name + ") has been found in container (" + ccObj.name + ")")
                  return true
                }
              }
            }
          }
        }

        // If we reach here, the object needs to be generated
        if (exampleInstance.isEmpty) throw new RuntimeException("ERROR: Trying to generate TaskObject, but exampleInstance is not defined.")
        if ((possibleContainerNames.length == 0) || ((possibleContainerNames.length == 1) && (possibleContainerNames(0).length == 0))) {
          // CASE 1: Just generate in the main container
          obj.addObject( exampleInstance.get )
          println("### TaskObject: Adding object (" + exampleInstance.get.name + ") to (" + obj.name + ")")
        } else {
          // CASE 2: Generate in a sub-container of the main container
          val containedObjects = Random.shuffle( obj.getContainedObjectsRecursive().toList )

          for (cObj <- containedObjects) {
            if (possibleContainerNames.contains(cObj.name)) {
              // Add to this container
              println("### TaskObject: Adding object (" + exampleInstance.get.name + ") to (" + cObj.name + ")")
              cObj.addObject( exampleInstance.get )
              return true
            }
          }

          // If we reach here, then no valid 'possibleContainer' was found
          println ("ERROR: No valid possibleContainer (" + possibleContainerNames.mkString(", ") + ") was found in (" + obj.name + ").")
          // (But, we'll allow it to keep going, just in case there is another container named 'roomToGenerateIn' in the environment)
        }
        return true
      }
    }

    println("### TaskObject: ERROR: Reached the end without adding an object.")
    return false
  }


}


// Disables a device, with a given name, in a given room.
class TaskDisable(val name:String, val roomIn:Array[String]) extends TaskModifier {

  // Disable all devices with a given name, that are contained within any of the container listed in 'room'.  Looks recursively in containers.
  override def runModifier(universe: EnvObject, agent: Agent): Boolean = {
    // Step 1: Look for the object(s) to disable
    val objsToDisable = new ArrayBuffer[EnvObject]

    // First, look for all objects in the universe with that name
    val objsWithName = this.findObjectsWithName(name, universe)
    // Then, see if those objects are also in a relevant container
    for (obj <- objsWithName) {
      val containers = obj.getContainersRecursive()                   // Get all containers (down to the world tree root) that this object is in
      breakable {
        for (container <- containers) {
          if (roomIn.contains(container.name)) {                      // If one of the containers it's in is listed in the 'roomIn' find list, then add it to the list of devices to disable
            objsToDisable.append(obj)
            break()
          }
        }
      }
    }

    // Step 2: Disable objects
    var numDisabled:Int = 0
    for (obj <- objsToDisable) {
      if (obj.propDevice.isDefined) {
        obj.propDevice.get.isBroken = true      // Disable the device
        println("### TaskDisable: Disabled device (" + obj.name + ") in (" + obj.getContainer().get.name + ").")
        numDisabled += 1
      }
    }

    // Step 3: Return true if we successfully disabled at least one device
    if (numDisabled > 0) {
      return true
    }
    return false
  }

}