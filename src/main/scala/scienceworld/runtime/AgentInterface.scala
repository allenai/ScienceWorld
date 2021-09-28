package scienceworld.runtime

import scienceworld.input.{ActionHandler, InputParser}
import scienceworld.struct.EnvObject

import scala.io.StdIn.readLine

class AgentInterface(universe:EnvObject, agent:EnvObject, actionHandler:ActionHandler) {
  val inputParser = new InputParser(actionHandler.getActions())

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

    //val (successUserInput, errStr, userStr) = userInputParser.parse(inputStr, interpreter.objectTreeRoot, agent)
    val (successUserInput, errStr, userStr, action) = inputParser.parse(inputStr, visibleObjects, agent)
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
  def step(userInputStr:String): String = {

    // Parse user input
    val (success, statusStr) = this.processUserInput(userInputStr)

    // Run queued actions
    val userOutstr = actionHandler.runQueuedActions()

    // Run universe tick
    universe.tick()

    // Return action string
    return userOutstr
  }

}
