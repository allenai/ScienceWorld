package scienceworld.runtime.pythonapi

import py4j.GatewayServer
import scienceworld.environments.EnvironmentMaker
import scienceworld.input.{ActionDefinitions, InputParser}
import scienceworld.objects.agent.Agent
import scienceworld.runtime.AgentInterface
import scienceworld.struct.EnvObject
import scienceworld.tasks.Task
import collection.JavaConverters._
import scala.util.control.Breaks.{break, breakable}

// Storage class
class PythonInterfaceReturn(val observation:String, val score:Double, val isCompleted:Boolean) {

}

class PythonInterface() {
  val ERROR_MESSAGE_UNINITIALIZED = "ERROR: Interface is not initialized -- call reset() before beginning."

  var agentInterface:Option[AgentInterface] = None
  var agent:Option[EnvObject] = None
  val actionHandler = ActionDefinitions.mkActionDefinitions()

  var environmentStr:String = ""
  var curIter:Int = 0

  var score:Double = 0.0
  var isComplete:Boolean = false

  /*
   * Load/reset/shutdown server
   */
  def load(environmentStr:String): Unit = {
    this.environmentStr = environmentStr

    val goalSequence = Task.mkTaskChangeOfState()
    val (universe, agent_) = EnvironmentMaker.mkKitchenEnvironment()

    agent = Some(agent_)
    agentInterface = Some( new AgentInterface(universe, agent.get, actionHandler, goalSequence) )

    curIter = 0
  }

  def reset() = {
    this.load(environmentStr)
  }

  def shutdown(): Unit = {
    sys.exit(0)
  }

  /*
   * Get object/action space
   */
  def getPossibleActions(): java.util.List[String] = {
    if (!agentInterface.isDefined) return List(ERROR_MESSAGE_UNINITIALIZED).asJava
    agentInterface.get.getPossibleActions().toList.asJava
  }

  def getPossibleObjects(): java.util.List[String] = {
    if (!agentInterface.isDefined) return List(ERROR_MESSAGE_UNINITIALIZED).asJava
    agentInterface.get.getPossibleObjects().toList.asJava
  }

  def getPossibleActionObjectCombinations(): java.util.List[String] = {
    if (!agentInterface.isDefined) return List(ERROR_MESSAGE_UNINITIALIZED).asJava
    agentInterface.get.getPossibleActionObjectCombinations().toList.asJava
  }

  def getNumMoves():Int = this.curIter

  /*
   * Take action steps and get observations/scores
   */

  def getScore():Double = this.score

  def getCompleted():Boolean = this.isComplete

  def step(userInputString:String): String = {
    val outStr = new StringBuilder
    // Error checking
    if (agentInterface.isEmpty) return ERROR_MESSAGE_UNINITIALIZED
    if (agent.isEmpty) return "ERROR: No agent is marked as main."
    if (agent.get.getContainer().isEmpty) return "ERROR: Agent is not in a container."

    // Get agent's container (to render agent's perspective)
    val agentContainer = agent.get.getContainer().get

    // Process step in environment
    val (description, score_, isCompleted_) = agentInterface.get.step(userInputString)
    this.score = score_
    this.isComplete = isCompleted_

    println("Description: ")
    println(description)

    // DEBUG
    val referents = InputParser.getPossibleReferents(agentInterface.get.getAgentVisibleObjects()._2, agentContainer)
    println("Possible referents: " + referents.mkString(", "))

    outStr.append(description)
    outStr.append("\nPossible referents: " + referents.mkString(", "))

    curIter += 1
    // Return
    return outStr.toString()
  }


}

object PythonInterface {

  def main(args:Array[String]): Unit = {
    println ("Initializing Virtual Environment Python Server...")
    val obj = new PythonInterface()

    val server = new GatewayServer(obj)
    server.start()

  }

}
