package main.scala.scienceworld.runtime

import scienceworld.objects.agent.Agent
import scienceworld.objects.location.Universe
import scienceworld.objects.portal.Portal
import scienceworld.struct.EnvObject

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._


// Generic
class Simplification(val label:String, val description:String) {
  var runAtInitialization:Boolean = false
  var runEachTick:Boolean = false

  def runSimplification(universe:EnvObject, agent:Agent):Boolean = {
    return false
  }

}

// Simplification: Open all doors in the environment
class SimplificationOpenDoors extends Simplification(label = "openDoors", description = "All doors are open by default.") {
  runAtInitialization = true

  override def runSimplification(universe: EnvObject, agent:Agent): Boolean = {
    val allObjs = universe.getContainedObjectsAndPortalsRecursive(includeHidden = true, includePortalConnections = true)
    for (obj <- allObjs) {
      obj match {
        case x:Portal => {
          if ((x.propPortal.isDefined) && (x.propPortal.get.isOpenable)) {    // If a portal is openable
            x.propPortal.get.isOpen = true                                    // Open it
          }
        }
        case _ => {
          // do nothing
        }
      }
    }

    return true
  }

}


// Simplification: Open all doors in the environment
class SimplificationOpenContainers extends Simplification(label = "openContainers", description = "All containers are open by default.") {
  runAtInitialization = true

  override def runSimplification(universe: EnvObject, agent:Agent): Boolean = {
    val allObjs = universe.getContainedObjectsAndPortalsRecursive(includeHidden = true, includePortalConnections = true)
    for (obj <- allObjs) {
      obj match {
        case x:Portal => {
          if ((x.propContainer.isDefined) && (x.propContainer.get.isContainer) && (x.propContainer.get.isClosable)) {    // If an object is a container that's closable
            x.propContainer.get.isOpen = true                                    // Open it
          }
        }
        case _ => {
          // do nothing
        }
      }
    }

    return true
  }

}




object SimplifierProcessor {
  val simplifications = new ArrayBuffer[Simplification]()


  // Accessors
  def addSimplification(simplification:Simplification): Unit = {
    // Check for duplicates
    for (s <- simplifications) {
      if (s.label == simplification.label) return
    }
    // Add if not a duplicate
    simplifications.append(simplification)
  }

  /*
   * Parsing/adding simplifications
   */

  // A CSV string with all the simplifications that should be added
  // e.g. openDoors,openContainers
  def parseSimplificationStr(simplificationStr:String) = {
    val fields = simplificationStr.toLowerCase.trim().split(",")
    val allSimplifications = this.getAllSimplifications()

    for (field <- fields) {
      breakable {
        for (s <- allSimplifications) {
          if (s.label.toLowerCase == field.trim()) {
            println("Adding simplification: (" + s.label + "): " + s.description)
            this.addSimplification(s)
            break()
          }
        }

        throw new RuntimeException("ERROR: Unknown simplification label: " + field)
      }
    }
  }

  /*
   * Get simplifications being used
   */
  def getSimplificationsUsed():String = {
    return simplifications.map(_.label).sorted.mkString(", ")
  }

  def getPossibleSimplifications():String = {
    return this.getAllSimplifications().map(_.label).sorted.mkString(", ")
  }

  /*
   * Run simplifications
   */
  def runSimplificationsInitialization(universe: EnvObject, agent: Agent): Boolean = {
    var successOverall = true
    for (s <- simplifications) {
      if (s.runAtInitialization) {
        println ("Running simplification: " + s.label)
        val success = s.runSimplification(universe, agent)
        if (!success) successOverall = false
      }
    }
    return successOverall
  }

  def runSimplificationsEachTick(universe: EnvObject, agent: Agent): Boolean = {
    var successOverall = true
    for (s <- simplifications) {
      if (s.runEachTick) {
        println ("Running simplification: " + s.label)
        val success = s.runSimplification(universe, agent)
        if (!success) successOverall = false
      }
    }
    return successOverall
  }


  /*
   * Helper functions
   */
  // Add all new simplifications here
  def getAllSimplifications():Array[Simplification] = {
    val out = new ArrayBuffer[Simplification]()

    out.append( new SimplificationOpenDoors() )
    out.append( new SimplificationOpenContainers() )

    return out.toArray
  }
}
