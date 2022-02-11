package scienceworld.goldagent

import language.model.ActionRequestDef
import scienceworld.actions.{Action, ActionFocus, ActionLookAround, ActionMoveObject, ActionMoveThroughDoor, ActionOpenDoor}
import scienceworld.objects.agent.Agent
import scienceworld.objects.location.{Location, Universe}
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.ObjMonitor

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/*
 * Tools for generating gold trajectories/action sequences.
 */


object PathFinder {



  def createActionSequence(universe:EnvObject, agent:Agent, startLocation:String, endLocation:String): (Array[Action], Array[String]) = {
    val actionSequence = new ArrayBuffer[(Action, String)]

    val (success, pathLocations) = this.getLocationSequence(universe, startLocation, endLocation)
    if (!success) {
      // Fail gracefully
      //println ("Error: could not get location sequence")
      return (Array.empty[Action], Array.empty[String])
    }

    //println ("PathLocations: " + pathLocations.mkString(", "))

    for (i <- 0 until pathLocations.length-1) {
      val locationName = pathLocations(i)
      val nextLocationName = pathLocations(i+1)

      // Get the location object
      val location = this.getEnvObject(queryName = locationName, universe)
      if (location.isEmpty) {
        // Fail gracefully
        //println ("Error: location is empty")
        return (Array.empty[Action], Array.empty[String])
      }

      // Find the appropraite portal
      val portals = location.get.getPortals()
      for (portal <- portals) {
        if (portal.doesConnectTo(nextLocationName)) {
          // This is the portal that connects to the next location.

          // Get portal referent
          val portalReferents = portal.getReferents(location.get).toList.sortBy(- _.length)
          val portalReferent = portalReferents(0)   // Take the longest referent (least likely to be ambiguous)

          // Do we need to open it?
          // TODO: Add check?
          val actionOpenDoor = new ActionOpenDoor(ActionRequestDef.mkBlank(), assignments = Map("agent" -> agent, "door" -> portal))
          actionSequence.append( (actionOpenDoor, "open " + portalReferent) )

          // Now we need to go through it
          val actionMoveLocation = new ActionMoveThroughDoor(ActionRequestDef.mkBlank(), assignments = Map("agent" -> agent, "doorOrLocation" -> portal))
          actionSequence.append( (actionMoveLocation, "go to " + nextLocationName) )
        }

      }

    }


    // Convert to parallel arrays
    val actions = new ArrayBuffer[Action]
    val strs = new ArrayBuffer[String]
    for (i <- 0 until actionSequence.length) {
      actions.append( actionSequence(i)._1 )
      strs.append( actionSequence(i)._2 )
    }

    // Return
    return (actions.toArray, strs.toArray)
  }


  // Find a valid path (sequence of locations) from a starting location to an end location.
  // Returns (success, array(location names) )
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



  /*
   * Action helpers
   */

  // Generate an action to focus on an object
  def actionFocusOnObject(obj:EnvObject, agent:Agent): (Action, String) = {
    val actionFocus = new ActionFocus(ActionRequestDef.mkBlank(), assignments = Map("agent" -> agent, "obj" -> obj), objMonitor = new ObjMonitor())   //## new objMonitor
    val actionStr = "focus on " + this.getObjReferent(obj)
    return (actionFocus, actionStr)
  }

  def actionMoveObject(obj:EnvObject, destination:EnvObject, agent:Agent): (Action, String) = {
    val actionMove = new ActionMoveObject(ActionRequestDef.mkBlank(), assignments = Map("agent" -> agent, "obj" -> obj, "moveTo" -> destination))
    val actionStr = "move " + this.getObjReferent(obj) + " to " + this.getObjReferent(destination)
    return (actionMove, actionStr)
  }

  def actionLookAround(agent:Agent):(Action, String) = {
    val actionLookAround = new ActionLookAround(ActionRequestDef.mkBlank(), assignments = Map("agent" -> agent))
    val actionStr = "look around"
    return (actionLookAround, actionStr)
  }


  // Get a likely OK referent name for an object
  def getObjReferent(obj:EnvObject): String = {
    val referents = obj.getReferents().toList.sortBy(- _.length)
    val referent = referents(0)   // Take the longest referent (least likely to be ambiguous)
    return referent
  }


  /*
   * Helpers
   */

  // Find an object in the universe by name
  def getEnvObject(queryName:String, universe:EnvObject):Option[EnvObject] = {
    val allObjects = universe.getContainedObjectsAndPortalsRecursive(true)
    for (obj <- allObjects) {
      if ((sanitize(obj.name) == sanitize(queryName)) || (sanitize(obj.getDescriptName()) == sanitize(queryName))) {
        return Some(obj)
      }
    }

    // Default return
    None
  }

  // Find all objects with a given name in the universe
  def getAllEnvObject(queryName:String, universe:EnvObject):Array[EnvObject] = {
    val allObjects = universe.getContainedObjectsAndPortalsRecursive(true)
    val out = new ArrayBuffer[EnvObject]

    for (obj <- allObjects) {
      if ((sanitize(obj.name) == sanitize(queryName)) || (sanitize(obj.getDescriptName()) == sanitize(queryName))) {
        out.append(obj)
      }
    }

    // Default return
    out.toArray
  }


  // Sanitize strings for comparison
  private def sanitize(name:String):String = {
    return name.trim().toLowerCase
  }

}
