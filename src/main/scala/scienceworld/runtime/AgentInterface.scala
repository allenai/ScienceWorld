package scienceworld.runtime

import scienceworld.input.{ActionDefinitions, ActionHandler, ExampleAction, InputParser}
import scienceworld.objects.agent.Agent
import scienceworld.runtime.pythonapi.TemplateAction
import scienceworld.struct.EnvObject
import scienceworld.tasks.Task
import scienceworld.tasks.goals.{GoalSequence, ObjMonitor}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn.readLine
import scala.util.control.Breaks._

class AgentInterface(universe:EnvObject, agent:Agent, actionHandler:ActionHandler, task:Task) {
  val inputParser = new InputParser(actionHandler.getActions())
  val objMonitor = new ObjMonitor()
  // Store whether the environment is in an unexpected error state
  var errorState:Boolean = false
  var errorMessage:String = ""

  // Add any task-specific objects to the agent's inventory
  // TODO: Currently places task objects in agents inventory
  for (taskObject <- task.taskObjects) {
    agent.getInventoryContainer().addObject(taskObject)
  }

  private var curIter:Int = 0

  /*
   * Objects visible to the agent
   */

  // TODO: Currently just returns the current room, rather than a list of all visible objects
  def getAgentVisibleObjects():(Boolean, EnvObject)  = {
    val agentContainer = agent.getContainer()
    if (agentContainer.isEmpty) {
      val errStr = "ERROR: Agent is not in container."
      return (false, new EnvObject)
    }

    return (true, agentContainer.get)
  }


  /*
   * Get possible actions/objects
   */
  def getPossibleActions(): Array[String] = {
    actionHandler.getActionExamplesPlainText().sorted
  }

  def getPossibleActionsWithIDs(): Array[ExampleAction] = {
    actionHandler.getActionExamplesPlainTextWithID()
  }

  def getPossibleActionsWithIDsJSON(): String = {
    val os = new StringBuilder

    val templates = actionHandler.getActionExamplesPlainTextWithID()
    val templateJSON = templates.map(_.toJSON())
    os.append("[" + templateJSON.mkString(", ") + "]")

    os.toString()
  }


  def getPossibleObjects(): Array[String] = {
    val referents = inputParser.getAllUniqueReferents(this.getAgentVisibleObjects()._2, includeHidden = false).map(_._1)
    return referents
  }

  def getPossibleObjectReferentLUT():Map[Long, String] = {
    val uuid2referentLUT = inputParser.getAllUniqueReferentsLUT(this.getAgentVisibleObjects()._2, includeHidden = false)
    return uuid2referentLUT
  }

  def getPossibleObjectReferentLUTJSON():String = {
    val uuid2referentLUT = this.getPossibleObjectReferentLUT()

    val elems = new ArrayBuffer[String]
    for (key <- uuid2referentLUT.keySet) {
      elems.append( "\"" + key + "\": \"" + uuid2referentLUT(key) + "\"")
    }

    return "{" + elems.mkString(", ") + "}"
  }

  def getAllObjectTypesLUT():Map[Long, Long] = {
    val allObjs = InputParser.collectObjects(universe, includeHidden=true).toArray

    val lut = mutable.Map[Long, Long]()
    for (obj <- allObjs) {
      lut(obj.uuid) = obj.typeID
    }

    return lut.toMap
  }

  def getAllObjectTypesLUTJSON():String = {
    val uuid2typeLUT = this.getAllObjectTypesLUT()

    val elems = new ArrayBuffer[String]
    for (key <- uuid2typeLUT.keySet) {
      elems.append( "\"" + key + "\": \"" + uuid2typeLUT(key) + "\"")
    }

    return "{" + elems.mkString(", ") + "}"

  }

  // Get a LUT of {object_id: {type_id, referent:[]} values for the entire universe.
  def getAllObjectIdsTypesReferentsLUTJSON(): String = {
    val allObjs = InputParser.collectObjects(universe, includeHidden=true).toArray

    val elems = new ArrayBuffer[String]
    for (obj <- allObjs) {
      println(obj.name)
      val uuid = obj.uuid
      val typeId = obj.typeID
      val referents = obj.getReferents()
      val referentsProcessed = new ArrayBuffer[String]
      for (referent <- referents) {
        referentsProcessed.append("\"" + referent + "\"")
      }

      val json = "\"" + uuid + "\":{ \"type_id\":" + typeId + ", \"referents\":[" + referentsProcessed.mkString(", ") + "] }"
      elems.append(json)
    }

    val jsonOut = "{ " + elems.mkString(", ") + " }"
    return jsonOut
  }

  // Returns a list of only the valid action-agent combinations
  def getValidActionObjectCombinations(): Array[String] = {
    // Collect all objects visible to the agent
    val visibleObjTreeRoot = this.getAgentVisibleObjects()._2
    val agentInventory = agent.getInventoryContainer()
    val allVisibleObjects = InputParser.collectObjects(visibleObjTreeRoot, includeHidden = false).toList
    // Collect UUID -> Unique Referent LUT
    val uuid2referentLUT = inputParser.getAllUniqueReferentsLUT(visibleObjTreeRoot, includeHidden=false)

    // Generate all possible valid actions
    val validActions = ActionDefinitions.mkPossibleActions(agent, allVisibleObjects.toArray, uuid2referentLUT)

    return validActions.map(_.mkHumanReadableStr())
  }

  def getValidActionObjectCombinationsJSON(): String = {
    // Collect all objects visible to the agent
    val visibleObjTreeRoot = this.getAgentVisibleObjects()._2
    val agentInventory = agent.getInventoryContainer()
    val allVisibleObjects = InputParser.collectObjects(visibleObjTreeRoot, includeHidden = false).toList
    // Collect UUID -> Unique Referent LUT
    val uuid2referentLUT = inputParser.getAllUniqueReferentsLUT(visibleObjTreeRoot, includeHidden=false)

    // Generate all possible valid actions
    val validActions = ActionDefinitions.mkPossibleActions(agent, allVisibleObjects.toArray, uuid2referentLUT)

    // To templates
    val validActionsTemplates = validActions.map(_.toTemplate())
    val validActionTemplatesJSON = validActionsTemplates.map(_.toJSON())

    // To JSON
    val outJSON = "{\"validActions\": [" + validActionTemplatesJSON.mkString(",") + "]" + "}"

    return outJSON
  }



  def getPossibleActionObjectCombinations(): (Array[TemplateAction], Map[Int, String]) = {
    val OBJ_PLACEHOLDER_TOKEN = "OBJ"
    val START_TOKEN = "START "
    val END_TOKEN = " END"

    val outTemplates = new ArrayBuffer[TemplateAction]
    val outObjectIdxLUT = mutable.Map[Int, String]()

    val objects = inputParser.getAllUniqueReferents(this.getAgentVisibleObjects()._2, includeHidden = false)

    val allActions = this.getPossibleActionsWithIDs()
    for (actionIdx <- 0 until allActions.length) {
      val actionStr = allActions(actionIdx).exampleStr

      val actionStr1 = START_TOKEN + actionStr + END_TOKEN
      val split = actionStr1.split(OBJ_PLACEHOLDER_TOKEN)

      // Number of objects to substitute
      val numPlaceholders = split.length - 1

      // Create all possible permutations of combinations of N objects
      val combos = objects combinations(numPlaceholders)
      for (combo <- combos) {
        for (perm <- combo permutations) {
          val outStr = new StringBuilder
          val outObjs = new ArrayBuffer[EnvObject]

          for (i <- 0 until split.length) {
            outStr.append(split(i))
            if (i < (split.length - 1)) {
              val objReferent = perm(i)._1
              val obj = perm(i)._2
              outStr.append(objReferent)
              outObjs.append(obj)

              // Also add Object UUID and unique referent to LUT
              val objUUID = obj.uuid.toInt
              outObjectIdxLUT(objUUID) = objReferent
            }
          }
          // Remove start/end tokens
          val sanitizedOutStr = outStr.substring(START_TOKEN.length, outStr.length - END_TOKEN.length).trim
          val templateID = allActions(actionIdx).actionID   // Fetch unique template ID for this action template
          val objectUUIDs = outObjs.map(_.uuid).map(_.toInt).toList
          val objectTypeIDs = outObjs.map(_.typeID).map(_.toInt).toList

          // Pack
          val template = new TemplateAction(sanitizedOutStr, templateID, objectUUIDs, objectTypeIDs)

          // Store
          outTemplates.append(template)
        }
      }
    }

    // Return
    (outTemplates.toArray, outObjectIdxLUT.toMap)
  }

  def getPossibleActionObjectCombinationsJSON():String = {
    // Step 1: Get templates
    val (templates, uuidToRefLUT) = this.getPossibleActionObjectCombinations()

    // Step 2: Serialize templates to JSON
    val templatesJSON = templates.map(_.toJSON())

    // Step 3: Serialize LUT to JSON
    val jsonRecords = new ArrayBuffer[String]
    for (uuid <- uuidToRefLUT.keys.toArray.sorted) {
      jsonRecords.append("\"" + uuid + "\": \"" + uuidToRefLUT(uuid) + "\"")
    }
    val lutJSON = "{" + jsonRecords.mkString(",") + "}"

    // Step 4: Join JSONs together
    val outJSON = "{\"templates\": [" + templatesJSON.mkString(",") + "], \"lookUpTable\": " + lutJSON + "}"

    // Step 3: Return
    return outJSON
  }

  def getTaskDescription():String = {
    return this.task.description
  }

  def getCurIterations():Int = {
    return this.curIter
  }


  /*
   * Error Handling
   */
  def setErrorState(errorStr:String) {
    this.errorState = true
    this.errorMessage = errorStr
  }

  def isInErrorState():Boolean = {
    return this.errorState
  }

  def getErrorStateMessage():String = {
    return this.errorMessage
  }

  /*
   * User Input
   */
  def getUserInput():String = {
    print("> ")
    val inputStr = readLine()
    return inputStr
  }

  def processUserInput(inputStr:String):(Boolean, String) = {   // (Success, statusString)
    val (successVisible, visibleObjects) = this.getAgentVisibleObjects()      // TODO: Currently just a reference to the container (current room), rather than a list
    if (!successVisible) throw new RuntimeException("ERROR: Agent is not in container.")

    // The agent's container (to render the agent's perspective)
    val agentContainer = agent.getContainer().get


    if (!this.inputParser.isInAmbiguousState()) {
      // Case 1: Normal case

      //val (successUserInput, errStr, userStr) = userInputParser.parse(inputStr, interpreter.objectTreeRoot, agent)
      val (successUserInput, errStr, userStr, action) = inputParser.parse(inputStr, visibleObjects, agent, objMonitor, task.goalSequence, agentContainer)
      if (!successUserInput) {
        println("ERROR: " + errStr)
      } else {
        println(userStr)
        actionHandler.queueAction(action.get)
      }

      return (successUserInput, userStr)
    } else {
      // Case 2: Waiting to resolve an ambiguity

      // NOTE: Whether or not the ambiguity is resolved successfully, this will handle it and generate an appropriate user message.
      val (userStr, action) = inputParser.resolveAmbiguity(inputStr, agent, objMonitor, task.goalSequence)
      if (action.isDefined) {
        actionHandler.queueAction(action.get)
      }
      return (true, userStr)

    }

  }


  /*
   * Step
   */

  // Returns (observation, score, isCompleted)
  def step(userInputStr: String): (String, Double, Boolean) = {
    val userOutStr = new StringBuilder()

    // Check whether the simulator is in an error state (if so, return the error message)
    if (this.isInErrorState()) {
      return (this.getErrorStateMessage(), -1, true)
    }

    // Parse user input
    val (success, statusStr) = this.processUserInput(userInputStr)
    if (statusStr.length > 0) {
      userOutStr.append("Input: " + statusStr + "\n\n")
    }

    // Check for ambiguity resolution case after parsing new input
    if (this.inputParser.isInAmbiguousState()) {
      // Request clarification from user to resolve ambiguity -- do not run tick(), etc.
      val score = task.goalSequence.score()
      val isCompleted = task.goalSequence.isCompleted()
      return (statusStr, score, isCompleted)
    }

    try {
      breakable {
        while (true) {
          // Run queued actions
          val actionOutStr = actionHandler.runQueuedActions()
          userOutStr.append(actionOutStr)

          // Run universe tick
          universe.clearTickProcessedRecursive()
          universe.tick()

          // Check whether the goal conditions are met
          task.goalSequence.tick(objMonitor)

          // Increment the number of iterations
          this.curIter += 1

          // If the agent is not waiting, then break.  But if the agent is waiting, continue cycling through until the agent is finished waiting X number of ticks. (wait time is automatically decreased in the agent's wait function)
          if (!agent.isWaiting()) break
        }
      }
      //## Uncomment when debugging in IntelliJ
    }
    /*
        } catch {
          case e:Throwable => {
            this.setErrorState(e.toString)
          }

        }
    */

    val score = task.goalSequence.score()
    val isCompleted = task.goalSequence.isCompleted()

    // Return action string
    return (userOutStr.toString(), score, isCompleted)
  }

}
