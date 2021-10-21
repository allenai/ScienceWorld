package scienceworld.runtime

import scienceworld.input.{ActionHandler, InputParser}
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

  def getPossibleObjects(): Array[String] = {
    val referents = inputParser.getAllUniqueReferents(this.getAgentVisibleObjects()._2, includeHidden = false).map(_._1)
    return referents
  }

  def getPossibleActionObjectCombinations(): (Array[TemplateAction], Map[Int, String]) = {
    val OBJ_PLACEHOLDER_TOKEN = "OBJ"
    val START_TOKEN = "START "
    val END_TOKEN = " END"

    val outTemplates = new ArrayBuffer[TemplateAction]
    val outObjectIdxLUT = mutable.Map[Int, String]()

    val objects = inputParser.getAllUniqueReferents(this.getAgentVisibleObjects()._2, includeHidden = false)

    val allActions = this.getPossibleActions()
    for (actionIdx <- 0 until allActions.length) {
      val actionStr = allActions(actionIdx)

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
          val templateID = actionIdx      // TODO: This is just the index of the action in a name-stored array, rather than a unique ID for each action.  If different environments are run with different numbers of valid actions, this ID number would likely be different. (i.e. cross-action-space transfer would not work)
          val objectUUIDs = outObjs.map(_.uuid).map(_.toInt).toList

          // Pack
          val template = new TemplateAction(sanitizedOutStr, templateID, objectUUIDs)

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

  def getCurIterations():Int = this.curIter


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

    //val (successUserInput, errStr, userStr) = userInputParser.parse(inputStr, interpreter.objectTreeRoot, agent)
    val (successUserInput, errStr, userStr, action) = inputParser.parse(inputStr, visibleObjects, agent, objMonitor, task.goalSequence, agentContainer)
    if (!successUserInput) {
      println("ERROR: " + errStr)
    } else {
      println(userStr)
      actionHandler.queueAction(action.get)
    }

    return (successUserInput, userStr)
  }


  /*
   * Step
   */
  def step(userInputStr:String): (String, Double, Boolean) = {
    val userOutStr = new StringBuilder()

    // Parse user input
    val (success, statusStr) = this.processUserInput(userInputStr)
    if (statusStr.length > 0) {
      userOutStr.append("Input: " + statusStr + "\n\n")
    }


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

        // If the agent is not waiting, then break.  But if the agent is waiting, continue cycling through until the agent is finished waiting X number of ticks. (wait time is automatically decreased in the agent's wait function)
        if (!agent.isWaiting()) break

        this.curIter += 1
      }
    }

    val score = task.goalSequence.score()
    val isCompleted = task.goalSequence.isCompleted()

    // Return action string
    return (userOutStr.toString(), score, isCompleted)
  }

}
