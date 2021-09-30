package scienceworld.runtime.pythonapi

import py4j.GatewayServer
import scienceworld.environments.EnvironmentMaker
import scienceworld.input.{ActionDefinitions, InputParser}
import scienceworld.objects.agent.Agent
import scienceworld.runtime.AgentInterface
import scienceworld.struct.EnvObject
import scienceworld.tasks.Task

import scala.util.control.Breaks.{break, breakable}

class PythonInterface() {
  var agentInterface:Option[AgentInterface] = None
  var agent:Option[EnvObject] = None
  val actionHandler = ActionDefinitions.mkActionDefinitions()

  var environmentStr:String = ""
  var curIter:Int = 0


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

  def step(userInputString:String): (String, Double, Boolean) = {
    val outStr = new StringBuilder
    // Error checking
    if (agentInterface.isEmpty) return ("ERROR: Interface is not initialized -- call reset() before beginning.", 0.0, false)
    if (agent.isEmpty) return ("ERROR: No agent is marked as main.", 0.0, false)

    // Process step in environment
    val (description, score, isCompleted) = agentInterface.get.step(userInputString)
    println("Description: ")
    println(description)

    // DEBUG
    val referents = InputParser.getPossibleReferents(agentInterface.get.getAgentVisibleObjects()._2)
    println("Possible referents: " + referents.mkString(", "))

    outStr.append(description)
    outStr.append("\nPossible referents: " + referents.mkString(", "))

    curIter += 1
    // Return
    return (outStr.toString(), score, isCompleted)
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
