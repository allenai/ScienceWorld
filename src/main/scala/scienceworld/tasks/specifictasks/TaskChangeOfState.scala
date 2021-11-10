package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.objects.devices.Stove
import scienceworld.objects.{AppleJuice, Caesium, Chocolate, Gallium, Ice, IceCream, Lead, Marshmallow, Mercury, OrangeJuice, Soap, Tin}
import scienceworld.properties.LeadProp
import scienceworld.struct.EnvObject

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}


class TaskChangeOfState(val seed:Int) {

  val substancePossibilities = new ArrayBuffer[TaskModifier]()
  // Example of water (found in the environment)
  substancePossibilities.append( new TaskObject("water", None, "", Array.empty[String], 0) )
  // Example of ice (needs to be generated)
  substancePossibilities.append( new TaskObject("ice", Some(new Ice), "kitchen", Array("freezer"), 0) )
  // Example of something needing to be generated
  substancePossibilities.append( new TaskObject("orange juice", Some(new OrangeJuice), "kitchen", Array("fridge"), 0) )
  substancePossibilities.append( new TaskObject("apple juice", Some(new AppleJuice), "kitchen", Array("fridge"), 0) )
  substancePossibilities.append( new TaskObject("chocolate", Some(new Chocolate), "kitchen", Array("fridge"), 0) )
  substancePossibilities.append( new TaskObject("marshmallow", Some(new Marshmallow), roomToGenerateIn = "kitchen", Array("cupboard", "table", "desk"), generateNear = 0) )
  substancePossibilities.append( new TaskObject("ice cream", Some(new IceCream), roomToGenerateIn = "kitchen", Array("freezer"), generateNear = 0) )

  substancePossibilities.append( new TaskObject("soap", Some(new Soap), roomToGenerateIn = "kitchen", Array("cupboard", "table", "desk"), generateNear = 0) )
  substancePossibilities.append( new TaskObject("rubber", Some(new Soap), roomToGenerateIn = "workshop", Array("table", "desk"), generateNear = 0) )

  substancePossibilities.append( new TaskObject("lead", Some(new Lead()), "workshop", Array("table", "desk"), 0) )                // Metals
  substancePossibilities.append( new TaskObject("tin", Some(new Tin()), "workshop", Array("table", "desk"), 0) )
  substancePossibilities.append( new TaskObject("mercury", Some(new Mercury()), "workshop", Array("table", "desk"), 0) )
  substancePossibilities.append( new TaskObject("gallium", Some(new Gallium()), "workshop", Array("table", "desk"), 0) )
  substancePossibilities.append( new TaskObject("caesium", Some(new Caesium()), "workshop", Array("table", "desk"), 0) )



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



  // Setup a particular modifier combination on the universe
  def setupCombination(modifierCombination:List[List[TaskModifier]], universe:EnvObject, agent:Agent) = {
    val modifiers = modifierCombination.flatMap(_.toList)

    // Run each modifier's change on the universe
    for (mod <- modifiers) {
      mod.runModifier(universe, agent)
    }
  }

  // Setup a set of subgoals for this task modifier combination.
  def setupGoals(modifierCombination:List[List[TaskModifier]]): Unit = {
    // Step 1: Find substance name
    // NOTE: The first modifier here will be the substance to change the state of.
    val substanceModifier = modifierCombination(0)(0)
    var substanceName = "<unknown>"
    substanceModifier match {
      case m:TaskObject => {
        substanceName = m.name
      }
      case _ => {
        throw new RuntimeException("ERROR: Unknown task modifier found, where substance modifier was expected." + substanceModifier.toString)
      }
    }

    val subTask = "melt"
    //val subTask = "boil"
    //val subTask = "freeze"
    //val subTask = "change the state of matter of"
    //val description = "Your task is to change the state of matter of a substance.  First, focus on a substance.  Then, make changes that will cause it to change its state of matter.  To reset, type 'reset task'. "
    val description = "Your task is to " + subTask + " " + substanceName + ".  First, focus the substance.  Then, make changes that will cause it to change its state of matter. "



  }


}


//## DEBUG
object TaskChangeOfState {
  def main(args:Array[String]) = {
    val task = new TaskChangeOfState(seed = 0)
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
  val needsToBeGenerated:Boolean = exampleInstance.isEmpty

  // Add a given object to a given room.
  // TODO: Doesn't currently use 'generateNear' parameter.
  override def runModifier(universe: EnvObject, agent: Agent): Boolean = {
    // Step 1: Check to see if the object is marked as required to be generated
    if (!needsToBeGenerated) return false

    // Step 2: Check to see if the object already exists in the environment
    var allObjects:Set[EnvObject] = null
    if (disableRecursiveCheck) {
      // Only check objects that are directly contained within the named container
      allObjects = universe.getContainedObjects()
    } else {
      // Check objects that are contained in the named container, or its containers (recursively)
      allObjects = universe.getContainedObjectsAndPortalsRecursive()
    }
    for (obj <- allObjects) {
      if (obj.name == roomToGenerateIn) {
        // First, check to see if the object already exists
        val containedObjects = obj.getContainedObjects()
        for (cObj <- containedObjects) {
          if (cObj.name == name) {
            // An existing object with this name has been found -- exit
            return true
          }
        }

        // If we reach here, the object needs to be generated
        if (exampleInstance.isEmpty) throw new RuntimeException("ERROR: Trying to generate TaskObject, but exampleInstance is not defined.")
        obj.addObject( exampleInstance.get )
        println("### TaskObject: Adding object (" + exampleInstance.get.name + ") to (" + obj.name + ")")
        return true
      }
    }

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