package processworld.struct

import processworld.CategoricalVariableStore.CategoricalVariableStore
import processworld.Counters.{ObjectCounter, TickCounter}
import processworld.Logger
import processworld.Visualization.Visualize

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class Object(val objectName:String) {
  val logger = Logger.Logger

  val uniqueIdx = ObjectCounter.getNextIdx()

  var destroyed:Boolean = false

  // Variables
  val varsStr = scala.collection.mutable.Map[String, String]()
  val varsCategorical = scala.collection.mutable.Map[String, CategoricalVariableInstance]()
  val varsDouble = scala.collection.mutable.Map[String, Double]()

  // Container
  val contents = new ArrayBuffer[Object]()
  var inContainer:Option[Object] = None

  // Actions
  val actions = scala.collection.mutable.Map[String, Action]()


  // Register default actions

  // By default, all Objects are movable.
  val actionMove = new Action(name = stdAction.MOVE, callback = actionCallbackMove)
  this.addAction(actionMove)



  /*
   * Variables
   */

  // String variables
  def getVarStr(propName:String, defaultReturn:String): String = {
    if (!hasVarStr(propName)) return defaultReturn
    return varsStr(propName)
  }

  def setVarStr(propName:String, value:String) {
    if (this.getVarStr(propName, "") != value) {                          // Only write to log if the value is different
      this.addLog("Setting (" + propName + ") to (" + value + ")")
    }

    varsStr(propName) = value
  }

  def hasVarStr(propName:String):Boolean = {
    if (varsStr.contains(propName)) return true
    // Default return
    false
  }


  // Categorical variables
  def getVarCategorical(propName:String, defaultReturn:String):String = {
    if (!hasVarCategorical(propName)) return defaultReturn
    return varsCategorical(propName).getValue()
  }

  def setVarCategorical(propName:String, valueIdx:Int):Boolean = {
    return varsCategorical(propName).setValue(valueIdx)
  }

  def setVarCategorical(propName:String, valueStr:String):Boolean = {
    if (this.getVarCategorical(propName, "") != valueStr) {               // Only write to log if the value is different
      this.addLog("Setting (" + propName + ") to (" + valueStr + ")")
    }

    val idx = getCategoryIdx(propName, valueStr)
    return setVarCategorical(propName, idx)
  }

  // e.g. getCategoryIdx("open") will return an integer, to use as the index for setVarCategorical
  def getCategoryIdx(propName:String, valueStr:String):Int = {
    val idx = varsCategorical(propName).categoricalVariable.values.indexOf(valueStr)
    return idx
  }

  def hasVarCategorical(propName:String):Boolean = {
    if (varsCategorical.contains(propName)) return true
    // Default return
    false
  }

  def defineVarCategorical(propName:String, categoricalVariable:CategoricalVariable, defaultValueIdx:Int = -1): Unit = {
    var defaultValueIdx_ = defaultValueIdx
    if (defaultValueIdx_ == -1) {
      defaultValueIdx_ = categoricalVariable.defaultValueIdx
    }

    val cvInstance = new CategoricalVariableInstance(categoricalVariable, defaultValueIdx_)
    varsCategorical(propName) = cvInstance
  }


  // Numerical variables
  def getVarDouble(propName:String, defaultReturn:Double):Double = {
    if (!hasVarDouble(propName)) return defaultReturn
    return varsDouble(propName)
  }

  def setVarDouble(propName:String, value:Double) {
    if (this.getVarDouble(propName, 0.0) != value) {                      // Only write to log if the value is different
      this.addLog("Setting (" + propName + ") to (" + value + ")")
    }

    varsDouble(propName) = value
  }

  def incVarDouble(propName:String, incValue:Double): Unit = {
    setVarDouble(propName, getVarDouble(propName, 0.0) + incValue)
  }

  def decVarDouble(propName:String, decValue:Double): Unit = {
    setVarDouble(propName, getVarDouble(propName, 0.0) - decValue)
  }

  def hasVarDouble(propName:String):Boolean = {
    if (varsDouble.contains(propName)) return true
    // Default return
    false
  }



  /*
   * Container
   */
  def addObjectToContainer(obj:Object): Unit = {
    contents.append(obj)
    obj.setContainer(this)    // Back-reference for object to know which container it's in

    this.addLog("Adding object (" + obj.objectName + ") to this container. ")
  }

  def setContainer(container:Object): Unit = {
    this.inContainer = Some(container)
  }

  // Typically only used if the object is being destroyed
  def setNoContainer(): Unit = {
    this.inContainer = None
  }

  // Removes an object from this object
  def removeObjectFromContainer(obj:Object): Boolean = {
    val idx = contents.indexOf(obj)     // Find index of object to remove
    if (idx >= 0) {
      this.addLog("Removing object (" + obj.objectName + ") from this container. ")
      this.addLog("Contents before: " + this.contents.map(_.getNameString()).mkString(", "))
      contents.remove(idx, 1) // Remove it
      this.addLog("Contents after: " + this.contents.map(_.getNameString()).mkString(", "))

      obj.setNoContainer()
      return true
    }

    // Default return
    false
  }

  def getVolumeOfContainedObjects():Double = {
    var totalVolume:Double = 0.0
    for (obj <- contents) {
      totalVolume += obj.getVarDouble(stdProp.VOLUME_LITER, 0.0)
    }
    // Return
    totalVolume
  }

  def getRemainingContainerVolume():Double = {
    val totalVolume = this.getVarDouble(stdProp.CONTAINER_CAPACITY_LITERS, 0.0)
    val usedVolume = this.getVarDouble(stdProp.CONTAINER_VOLUME_FILLED_LITERS, 0.0)
    val remainingVolume = totalVolume - usedVolume

    // Return
    remainingVolume
  }



  // Checking for containment

  // Check if this object contains a given query object
  def contains(obj:Object): Boolean = {
    if (contents.contains(obj)) return true
    // Default return
    false
  }

  // Contains at least one object with a given name
  def containsObjName(objName:String): Boolean = {
    for (obj <- contents) {
      if (obj.objectName == objName) return true
    }
    // Default return
    false
  }

  // Check if this object, or any objects it contains, contains a given query object.  (e.g. query: plate, outter object: kitchen, containment: kitchen -> dishwasher -> plate)
  def containsRecursive(obj:Object): Boolean = {
    // Check if this object contains the query object
    if (this.contains(obj)) return true

    // Check if any of the contents of this object contain the query object
    for (containedObj <- contents) {
      if (containedObj.containsRecursive(obj)) return true
    }

    return false
  }


  // Return object references with a given name
  def getContainedObjectsWithName(queryName:String, allowPorousContainers:Boolean = false, alreadyTraversed:Set[Object] = Set.empty[Object]):Array[Object] = {
    addLog("* getContainedObjectsWithName: Searching for " + queryName + " in object (" + this.getNameString() + ")")
    val (objs, traversed) = getContainedObjectsWithNameHelper(queryName, allowPorousContainers)
    addLog("* getContainedObjectsWithName: Found (" + objs.map(_.getNameString()).mkString(", ") + ")")
    return objs
  }

  def getContainedObjectsWithNameHelper(queryName:String, allowPorousContainers:Boolean = false, alreadyTraversed:Set[Object] = Set.empty[Object]):(Array[Object], Set[Object]) = {
    val valuePorous = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CONTAINER_POROUS).values(1)     // 1: Porous
    val valueOpen = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CONTAINER_OPEN).values(1) // 1: "open"
    val valueOut = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.DRAWER_OUT).values(1) // 1: "out"

    val out = mutable.Set[Object]()
    var traversed = mutable.Set[Object]()
    traversed ++= alreadyTraversed

    //addLog("* getContainedObjectsWithNameHelper: Searching for " + queryName + " in object (" + this.getNameString() + ")")

    for (obj <- this.contents) {
      if (obj.objectName == queryName) {
        out.add(obj)
      }
    }

    traversed.add(this)

    //addLog("* getContainedObjectsWithNameHelper: Found in local contents: " + out.map(_.getNameString()).mkString(", "))

    // If the porous container option is enabled, recurse up and down through any porous containers to find objects in them
    if (allowPorousContainers) {
      // Recurse up (to containers contained within this container)
      for (obj <- this.contents) {
        if (!traversed.contains(obj)) {
          addLog("* getContainedObjectsWithNameHelper (up): Examining whether " + obj.getNameString() + " is porous or not")
          // Porous
          if (obj.getVarCategorical(stdProp.CONTAINER_POROUS, "") == valuePorous) {
            val (objsInContainer, traversedUp) = obj.getContainedObjectsWithNameHelper(queryName, allowPorousContainers, traversed.toSet)
            traversed ++= traversedUp
            for (cObj <- objsInContainer) {
              out.add(cObj)
            }
          }
          // Or, just an open container
          if ((obj.getVarCategorical(stdProp.CONTAINER_OPEN, "") == valueOpen) ||
              (obj.getVarCategorical(stdProp.DRAWER_OUT, "") == valueOut)) {
            val (objsInContainer, traversedUp) = obj.getContainedObjectsWithNameHelper(queryName, allowPorousContainers, traversed.toSet)
            traversed ++= traversedUp
            for (cObj <- objsInContainer) {
              out.add(cObj)
            }
          }
        }
      }

      //addLog("* getContainedObjectsWithNameHelper: Found in local+up contents: " + out.map(_.getNameString()).mkString(", "))

      // Recurse down (to containers this container is contained in, if this container is porous)

      //addLog("* getContainedObjectsWithNameHelper (down): Examining whether " + this.inContainer.get.getNameString() + " is porous or not")
      if (this.inContainer.isDefined) {
        // Porous
        if (this.getVarCategorical(stdProp.CONTAINER_POROUS, "") == valuePorous) {
          if (!traversed(this.inContainer.get)) {
            val (objsOutsideOfContainer, traversedDown) = this.inContainer.get.getContainedObjectsWithNameHelper(queryName, allowPorousContainers, traversed.toSet)
            traversed ++= traversedDown
            for (cObj <- objsOutsideOfContainer) {
              out.add(cObj)
            }
          }
        }
        // Or, just an open container
        if ((this.inContainer.get.getVarCategorical(stdProp.CONTAINER_OPEN, "") == valueOpen) ||
          (this.inContainer.get.getVarCategorical(stdProp.DRAWER_OUT, "") == valueOut)) {
          if (!traversed(this.inContainer.get)) {
            val (objsOutsideOfContainer, traversedDown) = this.inContainer.get.getContainedObjectsWithNameHelper(queryName, allowPorousContainers, traversed.toSet)
            traversed ++= traversedDown
            for (cObj <- objsOutsideOfContainer) {
              out.add(cObj)
            }
          }
        }
      }


      //addLog("* getContainedObjectsWithNameHelper: Found in local+up+down contents: " + out.map(_.getNameString()).mkString(", "))
    }

    // Return
    (out.toArray, traversed.toSet)
  }




  /*
   * Tick
   */
  def tick(subordinateCall:Boolean = false): Unit = {
    // Logging
    if (!subordinateCall) {
      //println ("tickCounter: " + TickCounter.getCurrentIdx())
      //println ("numSegments: " + logger.numSegments())

      val varValuesSegment = logger.getVariables(TickCounter.getCurrentIdx() - 1)
      logger.addVariables(this)                                         // Add the variables of all objects from this root node
      val createdObjects = logger.getCreatedObjects(TickCounter.getCurrentIdx())
      logger.addVisualization( Visualize.visualizeObjectDOT(this, varValuesSegment, createdObjects) )     // Add DOT visualization at it's last state before starting the new segment

      logger.newSegment()                                                     // Start new segment
      addLog("Tick(" + TickCounter.getNextIdx() + "): Started...")
    }

    // Volume: Calculate volume of contained objects
    this.setVarDouble(stdProp.CONTAINER_VOLUME_FILLED_LITERS, this.getVolumeOfContainedObjects())

    // Run any automatic actions
    for (key <- this.actions.keySet.toArray.sorted) {
      val action = actions(key)
      if (action.isAutomatic) {
        this.doAction(key, new ActionInfo())
      }
    }

    // Recurse: call tick() on all contained objects
    // TODO: Do we need to wrap this all in a loop, and keep checking for objects that haven't completed their ticks, until the number of updates is zero? In case any objects move during automatic actions, etc?
    for (obj <- this.contents) {
      if (obj != null) {      // Make sure that the object hasn't been removed while we've been iterating
        obj.tick(subordinateCall = true)
      }
    }

    // Logging
    if (!subordinateCall) {
      addLog("Tick(" + TickCounter.getCurrentIdx() + "): Completed...")
    }
  }


  /*
   * Action Interpreter
   */

  def addAction(action:Action): Unit = {
    actions(action.name) = action
    this.addLog("Registering action (" + action.name + "). ")
  }

  // Returns true if the requirements for a given action are met (i.e. if it's possible to successfully run that action)
  def actionRequirementsMet(actionToPerform:String): Boolean = {
    // Blank, implemented in subclasses
    false

  }

  // Perform a given action
  def doAction(actionToPerform:String, actionInfo:ActionInfo): Boolean = {
    // Step 1: Check that action exists
    if (!this.actions.contains(actionToPerform)) {
      this.addLog("Performing action (" + actionToPerform + "). ")
      this.addLog("Action (" + actionToPerform + ") is unknown. ")
      return false
    }

    // Step 2: Perform action
    // Step 2A: Message
    val action = this.actions(actionToPerform)
    var autoStr = ""
    if (action.isAutomatic) autoStr = "This is an automatic action."
    this.addLog("Performing action (" + actionToPerform + "). " + autoStr)

    // Step 2B: Run action
    val success = action.doAction(actionInfo)

    // Step 3: Success?
    if (success) {
      this.addLog("Action (" + actionToPerform + ") performed successfully. ")
    } else {
      this.addLog("Action (" + actionToPerform + ") was not performed successfully. ")
    }

    // Return
    success
  }



  /*
   * Action Callbacks
   */
  def actionCallbackDefault(actionInfo:ActionInfo):Boolean = {
    // Default
    true
  }


  // Container: Open
  def actionCallbackOpenContainer(actionInfo:ActionInfo):Boolean = {
    if (this.hasVarCategorical(stdProp.CONTAINER_OPEN)) {
      val valueOpen = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CONTAINER_OPEN).values(1) // 1: "open"
      return this.setVarCategorical(stdProp.CONTAINER_OPEN, valueOpen)
    }
    // Default return
    false
  }

  // Container: Close
  def actionCallbackCloseContainer(actionInfo:ActionInfo):Boolean = {
    if (this.hasVarCategorical(stdProp.CONTAINER_OPEN)) {
      val valueClose = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CONTAINER_OPEN).values(0) // 0: "close"
      return this.setVarCategorical(stdProp.CONTAINER_OPEN, valueClose)
    }
    // Default return
    false
  }

  // Drawer: Slide Out
  def actionCallbackDrawerSlideOut(actionInfo:ActionInfo):Boolean = {
    if (this.hasVarCategorical(stdProp.DRAWER_OUT)) {
      val valueOut = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.DRAWER_OUT).values(1) // 1: "out"
      return this.setVarCategorical(stdProp.DRAWER_OUT, valueOut)
    }
    // Default return
    false
  }

  // Drawer: Slide In
  def actionCallbackDrawerSlideIn(actionInfo:ActionInfo):Boolean = {
    if (this.hasVarCategorical(stdProp.DRAWER_OUT)) {
      val valueIn = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.DRAWER_OUT).values(0) // 0: "int"
      return this.setVarCategorical(stdProp.DRAWER_OUT, valueIn)
    }
    // Default return
    false
  }


  def actionCallbackMove(actionInfo:ActionInfo): Boolean = {
    actionInfo match {
      case ai:ActionInfoMove => {
        val objToMove = this
        val currentContainer = objToMove.inContainer
        val newContainer = ai.newContainer

        val agent = ai.agent
        val agentContainer = agent.inContainer

        // Step 1: If the agent is not in a defined container, then the object's accessibility to the agent cannot be determined, and the move can not be successful
        if (agentContainer.isEmpty) return failure("Move failed. Agent (" + agent.objectName + ") is not in a container, so object accessibility can not be determined.")

        this.addLog("Agent (" + agent.getNameString() + ") that requested to move object (" + this.getNameString() +") is in container (" + agentContainer.get.getNameString() + ")")

        // Step 2: Perform check to see if agent can move this object from it's current container to the new container (e.g. all the containers are open and accessable, etc)
        val (success1, path1) = findPathBetweenContainers(objToMove, agentContainer.get)
        if (!success1) return failure("Move failed. Object to move (" + objToMove.objectName + ") is not accessible to agent (" + agent.objectName + ") " + path1.map(_.objectName).mkString(", "))
        val (success1a, pathStr1a) = areContainersOnPathOpen(path1)
        if (!success1a) return failure("Move failed. " + pathStr1a)

        this.addLog("Object to move is accessible to agent (" + pathStr1a + ")")

        val (success2, path2) = findPathBetweenContainers(newContainer, agentContainer.get)
        if (!success2) return failure("Move failed. New container (" + newContainer.objectName + ") is not accessible to agent (" + agent.objectName + ") " + path2.map(_.objectName).mkString(", "))
        val (success2a, pathStr2a) = areContainersOnPathOpen(path2)
        if (!success2a) return failure("Move failed. " + pathStr2a)

        this.addLog("New container is accessible to agent (" + pathStr2a + ")")

        // Step 3: Perform check to see if the new container has enough volume for the new object
        val newContainerRemainingVolume = newContainer.getRemainingContainerVolume()
        val objectVolume = this.getVarDouble(stdProp.VOLUME_LITER, 0.0f)
        if (objectVolume > newContainerRemainingVolume) {
          return failure("Move failed.  New container (" + newContainer.getNameString() + ") does not have enough volume remaining (" + newContainerRemainingVolume + ") to hold object volume (" + objectVolume + ").")
        } else {
          this.addLog("New container (" + newContainer.getNameString() + ") has enough volume remaining (" + newContainerRemainingVolume + ") to hold object volume (" + objectVolume + ").")
        }

        // Remove from old container
        if (currentContainer.isDefined) {
          currentContainer.get.removeObjectFromContainer(objToMove)
        }

        // Add to new container
        newContainer.addObjectToContainer(objToMove)

        return true
      }
      case _ => {
        return failure("ActionInfoMove expected, but a different ActionInfo was found.")
      }
    }
  }



  // Checking whether objects can be moved
  private def areContainersOnPathOpen(path:Array[Object]):(Boolean, String) = {
    val pathString = path.map(_.objectName).mkString(", ")

    for (container <- path) {
      // Check for containers opened/not opened
      if (container.hasVarCategorical(stdProp.CONTAINER_OPEN)) {
        val valueOpen = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CONTAINER_OPEN).values(1) // 1: "open"
        if (container.getVarCategorical(stdProp.CONTAINER_OPEN, "") != valueOpen) {
          // This container is not open (i.e. is not accessible to the agent)
          return (false, "Container not open (" + container.objectName + ") in path (" + pathString + ").")
        }
      }

      // Check for drawers slid out/not slid out
      if (container.hasVarCategorical(stdProp.DRAWER_OUT)) {
        val valueOut = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.DRAWER_OUT).values(1) // 1: "out"
        if (container.getVarCategorical(stdProp.DRAWER_OUT, "") != valueOut) {
          // This container is not open (i.e. is not accessible to the agent)
          return (false, "Drawer not out (" + container.objectName + ") in path (" + pathString + ").")
        }
      }
    }

    // If we reach here, then the path should be accessible
    return (true, "Path is open (" + pathString + ").")
  }

  private def findPathBetweenContainers(obj1:Object, obj2:Object):(Boolean, Array[Object]) = {
    // First, check one way
    val (success1, path1) = findPathBetweenContainersHelper(obj1, obj2)
    if (success1 == true) {
      return (success1, path1)
    }

    // If the first way wasn't successful, reverse the order of the objects and check again
    val (success2, path2) = findPathBetweenContainersHelper(obj2, obj1)
    return (success2, path2)
  }

  private def findPathBetweenContainersHelper(obj1:Object, obj2:Object):(Boolean, Array[Object]) = {
    val path = new ArrayBuffer[Object]
    var ticks:Int = 0
    val MAX_TICKS = 1000

    //println ("findPathBetweenContainersHelper: Started... (" + obj1.objectName + ", " + obj2.objectName + ")")

    // Follow down the container heirarchy until we either (a) find the container we're looking for, (b) find a null container, or (c) exceed a maximum traversal limit.
    var curContainer:Option[Object] = Some(obj1)
    while ((ticks < MAX_TICKS) && (curContainer.isDefined)) {
      path.append(curContainer.get)                     // Keep track of the path
      //print ("At container: " + curContainer.get.objectName)
      if (curContainer.get == obj2) {
        // Success: we found the container that obj2 is in -- exit
        return (true, path.toArray)
      }

      curContainer = curContainer.get.inContainer       // Traverse to the next path
      ticks += 1                                        // Prevents infinite loop in errorful cases when containers have been looped.
    }

    //println ("findPathBetweenContainersHelper: Failure... ")
    // Failure
    return (false, Array.empty[Object])

  }

  /*
   * Containment (cont'd)
   */

  // Takes the current object and removes it from it's container, placing it into it's containers container.
  // E.g. expelling an apple from a box (container), causing it to now be in the box's container (e.g. room).
  def expelFromContainer(): Boolean = {
    addLog("Expelling this object from container.")

    // Case: This object is not in a container
    if (this.inContainer.isEmpty) return false

    // Case: This object's container is not in a container
    // Object will still be expelled, but will not have a container
    val objectsContainer = this.inContainer.get.inContainer

    // Remove object from from container
    this.inContainer.get.removeObjectFromContainer(this)

    // If the object's container is not in a container, the object is not placed in a new container (i.e. expelled into the void)
    if (objectsContainer.isEmpty) return false

    // Case: This object's container is valid
    val newContainer = objectsContainer.get
    newContainer.addObjectToContainer(this)

    // Success
    return true
  }


  /*
   * Creation/Destruction
   */

  // Remove a little bit of the object volume (e.g. for using up liquids, etc).  If the volume decreases below 0, then the object is destroyed.
  def reduceObjectVolume(volumeToRemove:Double): Unit = {
    val currentVolume = this.getVarDouble(stdProp.VOLUME_LITER, 0.0)
    val newVolume = currentVolume - volumeToRemove

    this.addLog("Reducing object volume by " + volumeToRemove)

    if (newVolume > 0.0) {
      // Still some left
      this.setVarDouble(stdProp.VOLUME_LITER, newVolume)
    } else {
      // None left -- destroy
      this.addLog("Volume reduced to zero -- marking object for destruction.")
      this.setVarDouble(stdProp.VOLUME_LITER, 0.0)
      this.markDestroyed()
    }
  }

  // Destroys the object completely
  def markDestroyed(): Unit = {
    this.addLog("DESTROY: Object is marked for destruction.")

    if (this.inContainer.isDefined) {
      val success = this.inContainer.get.removeObjectFromContainer(this)
      if (!success) throw new RuntimeException("ERROR: Object (" + this.getNameString() + ") was not able to be removed from container (" + this.inContainer.get.getNameString() + ").")
    }

    this.destroyed = true
  }


  /*
   * Comparison
   */
  // Check to see if this object has the same name and property values as another object
  def isEqualByProperties(that:Object):Boolean = {
    // Step 1: Compare object names
    if (this.objectName != that.objectName) return false

    // Step 2: Compare object variables
    val thisVars = this.getAllVarValues()
    val thatVars = that.getAllVarValues()

    if (thisVars.size != thatVars.size) return false
    for (key <- thisVars.keySet) {
      if (thisVars(key) != thatVars(key)) return false
    }

    // If we reach here, then the names and variable values are the same
    return true
  }


  /*
   * Logging
   */
  def addLog(str:String): Unit = {
    logger.add(getNameString().formatted("%30s") + ": " + str)
  }

  def failure(str:String):Boolean = {
    logger.add(getNameString().formatted("%30s") + ": FAILURE: " + str)
    false
  }


  /*
   * String methods
   */

  // Return a map of all variables and their values.
  def getAllVarValues():Map[String, String] = {
    val out = mutable.Map[String, String]()
    for (key <- this.varsStr.keySet) {
      out(key) = this.getVarStr(key, "")
    }
    for (key <- this.varsCategorical.keySet) {
      out(key) = this.getVarCategorical(key, "")
    }
    for (key <- this.varsDouble.keySet) {
      //out(key) = this.getVarDouble(key, 0.0).formatted("%.5f")
      out(key) = this.getVarDouble(key, 0.0).toString
    }

    // Add a faux 'current location' (i.e. current container) variable, primarily for visualization purposes
    if (this.inContainer.isDefined) {
      out(stdProp.CURRENT_CONTAINER) = this.inContainer.get.getNameString()
    } else {
      out(stdProp.CURRENT_CONTAINER) = "None"
    }

    // Return
    out.toMap
  }

  def toStringRecursive(recursive:Boolean = true): String = {
    val os = new StringBuilder

    os.append("Object: " + objectName + " (" + uniqueIdx + ")\n")

    os.append("\tString Variables: " + "\n")
    for (key <- varsStr.keySet) {
      os.append("\t\t" + key.formatted("%35s") + "\t: " + getVarStr(key, "") + "\n")
    }

    os.append("\tCategorical Variables: " + "\n")
    for (key <- varsCategorical.keySet) {
      os.append("\t\t" + key.formatted("%35s") + "\t: " + getVarCategorical(key, "") + "\n")
    }

    os.append("\tNumerical Variables: " + "\n")
    for (key <- varsDouble.keySet) {
      os.append("\t\t" + key.formatted("%35s") + "\t: " + getVarDouble(key, 0.0) + "\n")
    }


    os.append("\tContained in: ")
    if (this.inContainer.isDefined) {
      os.append( this.inContainer.get.getNameString() )
    } else {
      os.append("None")
    }
    os.append("\n")

    os.append("\tContents: " + "\n")
    for (i <- 0 until contents.length) {
      val containedObj = contents(i)
      os.append("\t\t" + i + ": " + containedObj.getNameString() + "\n")
    }

    os.append("\tActions: " + "\n")
    for (key <- actions.keySet.toArray.sorted) {
      val action = actions(key)
      if (!action.isAutomatic) os.append("\t\t" + action.name + "\n")
    }
    for (key <- actions.keySet.toArray.sorted) {
      val action = actions(key)
      if (action.isAutomatic) os.append("\t\t" + action.name + " (automatic)\n")
    }

    // Also print out the contained objects, if enabled
    if (recursive == true) {
      if (this.contents.length > 0) {
        os.append("\n")

        for (obj <- this.contents) {
          os.append(obj.toStringRecursive(recursive = true))
        }
      }
    }

    // Return
    os.toString()
  }

  override def toString():String = {
    return toStringRecursive(recursive = false)
  }

  def getNameString():String = {
    return objectName + " (" + uniqueIdx + ")"
  }

}



