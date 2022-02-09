package scienceworld.goldagent

import scienceworld.objects.agent.Agent
import scienceworld.objects.location.{Location, Universe}
import scienceworld.struct.EnvObject

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


// Find a path from one location to another location
object PathFinder {



  def findPathSequence(universe:EnvObject, agent:Agent, goalLocationName:String): Unit = {

    // Get current agent location
    val agentLocation = agent.getContainer().get

    // Check for end condition
    if (sanitize(agentLocation.name) == sanitize(goalLocationName)) {
      // Done
    } else {
      // Make next move
    }



  }


  // Find a valid path from one location to another
  def getLocationSequence(universe:EnvObject, startLocation:String, endLocation:String, maxSteps:Int = 10):(Boolean, Array[String]) = {
    val validTransitions = this.buildLocationGraph(universe)

    // Edge case: Start and end location are the same
    if (sanitize(startLocation) == sanitize(endLocation)) return (true, Array.empty[String])

    // Start: Populate with single path with single step (current start location)
    var curExhaustivePaths = Array( Array(startLocation) )

    var curSteps:Int = 0
    while (curSteps < maxSteps) {
      // Add to each path
      curExhaustivePaths = mkExhaustivePathsOneStepLonger(curExhaustivePaths, validTransitions)

      // Check for a successful path from start to finish
      for (path <- curExhaustivePaths) {
        if (sanitize(path.last) == sanitize(endLocation)) {
          // Found a valid path
          return (true, path)
        }
      }

      curSteps += 1
    }

    // If we reach here, then we timed out -- no successful path was found
    return (false, Array.empty[String])
  }


  // Make exhaustive paths one step longer
  def mkExhaustivePathsOneStepLonger(curPaths:Array[Array[String]], validTransitions:Map[String, Array[String]]):Array[Array[String]] = {
    val out = new ArrayBuffer[ Array[String] ]

    for (inPath <- curPaths) {
      val curLoc = inPath.last
      val possibleTransitions = validTransitions(curLoc)
      for (newLoc <- scala.util.Random.shuffle(possibleTransitions.toList)) {     // Shuffles the order, so different routes might be chosen
        out.append ( inPath ++ Array(newLoc) )
      }
    }

    out.toArray
  }


  // Builds a graph of where one can go (through portals) from each location
  def buildLocationGraph(universe:EnvObject):Map[String, Array[String]] = {
    val validTransitions = mutable.Map[String, Array[String]]()
    val allObjects = universe.getContainedObjectsAndPortalsRecursive(true)
    val allLocations = allObjects.filter(_.isInstanceOf[Location]).toArray

    println ("Number of locations: " + allLocations.length)
    println ("Locations: " + allLocations.map(_.name).mkString(", "))

    for (location <- allLocations) {
      val transitions = new ArrayBuffer[String]
      val portals = location.getPortals(includeHidden = false)
      for (portal <- portals) {
        for (connectionPoint <- portal.getConnectionPoints()) {
          if (sanitize(connectionPoint.name) != sanitize(location.name)) {
            transitions.append(connectionPoint.name)
          }
        }
      }

      validTransitions(location.name) = transitions.toArray
    }

    for (key <- validTransitions.keySet) {
      println ("\t" + key.formatted("%20s") + "\t" + validTransitions(key).mkString(", "))
    }

    return validTransitions.toMap
  }

  // Sanitize strings for comparison
  private def sanitize(name:String):String = {
    return name.trim().toLowerCase
  }

}
