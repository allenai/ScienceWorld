package scienceworld.runtime

import scienceworld.input.{ActionHandler, InputParser}
import scienceworld.runtime.pythonapi.TemplateAction
import scienceworld.struct.EnvObject
import scienceworld.tasks.Task
import scienceworld.tasks.goals.{GoalSequence, ObjMonitor}

import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn.readLine

class AgentInterface(universe:EnvObject, agent:EnvObject, actionHandler:ActionHandler, task:Task) {
  val inputParser = new InputParser(actionHandler.getActions())
  val objMonitor = new ObjMonitor()


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
    actionHandler.getActionExamplesPlainText()
  }

  def getPossibleObjects(): Array[String] = {
    val referents = inputParser.getAllUniqueReferents(this.getAgentVisibleObjects()._2).map(_._1)
    return referents
  }

  def getPossibleActionObjectCombinations(): Array[TemplateAction] = {
    val OBJ_PLACEHOLDER_TOKEN = "OBJ"
    val START_TOKEN = "START "
    val END_TOKEN = " END"

    val outTemplates = new ArrayBuffer[TemplateAction]

    val objects = inputParser.getAllUniqueReferents(this.getAgentVisibleObjects()._2)

    for (actionStr <- this.getPossibleActions()) {
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
            }
          }
          // Remove start/end tokens
          val sanitizedOutStr = outStr.substring(START_TOKEN.length, outStr.length - END_TOKEN.length).trim
          val templateID = 0
          val objectUUIDs = outObjs.map(_.uuid).map(_.toInt).toList
          // Pack
          val template = new TemplateAction(sanitizedOutStr, templateID, objectUUIDs)

          // Store
          outTemplates.append(template)
        }
      }
    }

    // Return
    outTemplates.toArray
  }

  def getPossibleActionObjectCombinationsJSON():Array[String] = {
    // Step 1: Get templates
    val templates = this.getPossibleActionObjectCombinations()

    // Step 2: Serialize to JSON
    val templatesJSON = templates.map(_.toJSON())

    // Step 3: Return
    return templatesJSON
  }

  def getTaskDescription():String = {
    return this.task.description
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

    // Run queued actions
    val actionOutStr = actionHandler.runQueuedActions()
    userOutStr.append(actionOutStr)

    // Run universe tick
    universe.tick()

    // Check whether the goal conditions are met
    task.goalSequence.tick(objMonitor)
    val score = task.goalSequence.score()
    val isCompleted = task.goalSequence.isCompleted()

    // Return action string
    return (userOutStr.toString(), score, isCompleted)
  }

}
