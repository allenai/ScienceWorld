package scienceworld.runtime.pythonapi

import py4j.GatewayServer
import main.scala.scienceworld.runtime.SimplifierProcessor
import scienceworld.environments.EnvironmentMaker
import scienceworld.input.{ActionDefinitions, InputParser}
import scienceworld.objects.agent.Agent
import scienceworld.runtime.AgentInterface
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1}
import util.{UniqueIdentifier, UniqueTypeID}

import collection.JavaConverters._
import scala.util.control.Breaks.{break, breakable}


// Storage class
class PythonInterfaceReturn(val observation:String, val score:Double, val isCompleted:Boolean) {

}

class PythonInterface() {
  val ERROR_MESSAGE_UNINITIALIZED = "ERROR: Interface is not initialized -- call reset() before beginning."

  var agentInterface:Option[AgentInterface] = None
  var agent:Option[Agent] = None
  val actionHandler = ActionDefinitions.mkActionDefinitions()

  var taskStr:String = ""                // Environment/task name
  var taskVariationIdx:Int = 0           // Task variation seed
  var simplificationStr:String = ""      // CSV delimited string of simplifications to perform to the environment

  var score:Double = 0.0
  var isComplete:Boolean = false

  var errorUnknownEnvironment:Boolean = false
  var errorStr:String = ""

  val taskMaker = new TaskMaker1()

  /*
   * Load/reset/shutdown server
   */
  def load(taskStr:String, variationIdx:Int, simplificationStr:String): Unit = {
    this.taskStr = taskStr
    this.taskVariationIdx = variationIdx
    this.simplificationStr = simplificationStr

    // Clear error string
    this.errorStr = ""

    // Reset UUID counter
    UniqueIdentifier.reset()

    // Make environment and agent
    val (universe, agent_) = EnvironmentMaker.mkKitchenEnvironment()

    //## Currently, get a random task instead of using the environment string
    // Set up task
    val (task_, taskErrStr) = taskMaker.doTaskSetup(taskStr, this.taskVariationIdx, universe, agent_)
    var task:Option[Task] = None
    if (task_.isDefined) {
      task = task_
    } else {
      task = Some( Task.mkUnaccomplishableTask() )
      errorStr += "ERROR: Task (" + this.taskStr + "): " + taskErrStr
    }

    if (task.isDefined) {
      this.errorUnknownEnvironment = false
      agent = Some(agent_)
      agentInterface = Some(new AgentInterface(universe, agent.get, task.get, simplificationStr))
    } else {
      this.errorUnknownEnvironment = true
    }

  }

  def reset() = {
    this.load(this.taskStr, this.taskVariationIdx, this.simplificationStr)
  }

  def shutdown(): Unit = {
    sys.exit(0)
  }

  /*
   * Get valid tasks/environments
   */
  def getTaskNames():java.util.List[String] = {
    taskMaker.getTaskList().toList.asJava
  }

  // Get the maximum variations for a given task
  def getTaskMaxVariations(taskName:String): Int = {
    taskMaker.getMaxVariations(taskName)
  }

  /*
   * Simplifications
   */
  def getSimplificationsUsed():String = {
    return SimplifierProcessor.getSimplificationsUsed()
  }

  def getPossibleSimplifications():String = {
    return SimplifierProcessor.getPossibleSimplifications()
  }

  /*
   * Get object/action space
   */
  def getPossibleActions(): java.util.List[String] = {
    if (!agentInterface.isDefined) return List(ERROR_MESSAGE_UNINITIALIZED).asJava
    agentInterface.get.getPossibleActions().toList.asJava
  }

  def getPossibleActionsWithIDs(): String = {
    if (!agentInterface.isDefined) return ERROR_MESSAGE_UNINITIALIZED
    agentInterface.get.getPossibleActionsWithIDsJSON()
  }

  def getPossibleObjects(): java.util.List[String] = {
    if (!agentInterface.isDefined) return List(ERROR_MESSAGE_UNINITIALIZED).asJava
    agentInterface.get.getPossibleObjects().toList.asJava
  }

  def getPossibleObjectReferentLUTJSON():String = {
    if (!agentInterface.isDefined) return ERROR_MESSAGE_UNINITIALIZED
    agentInterface.get.getPossibleObjectReferentLUTJSON()
  }

  def getValidActionObjectCombinations():java.util.List[String] = {
    if (!agentInterface.isDefined) return List(ERROR_MESSAGE_UNINITIALIZED).asJava
    agentInterface.get.getValidActionObjectCombinations().toList.asJava
  }

  def getValidActionObjectCombinationsJSON():String = {
    if (!agentInterface.isDefined) return ERROR_MESSAGE_UNINITIALIZED
    agentInterface.get.getValidActionObjectCombinationsJSON()
  }

  def getPossibleActionObjectCombinationsJSON(): String = {
    if (!agentInterface.isDefined) return ERROR_MESSAGE_UNINITIALIZED
    agentInterface.get.getPossibleActionObjectCombinationsJSON()
  }

  def getObjectTypesLUTJSON(): String = {
    UniqueTypeID.toJSON()
  }

  def getAllObjectTypesLUTJSON(): String = {
    if (!agentInterface.isDefined) return ERROR_MESSAGE_UNINITIALIZED
    agentInterface.get.getAllObjectTypesLUTJSON()
  }

  def getAllObjectIdsTypesReferentsLUTJSON(): String = {
    if (!agentInterface.isDefined) return ERROR_MESSAGE_UNINITIALIZED
    agentInterface.get.getAllObjectIdsTypesReferentsLUTJSON()
  }

  def getNumMoves():Integer = {
    if (agentInterface.isEmpty) return 0
    agentInterface.get.getCurIterations()
  }

  def getTaskDescription():String = {
    if (agentInterface.isEmpty) return ERROR_MESSAGE_UNINITIALIZED
    agentInterface.get.getTaskDescription()
  }

  /*
   * Take action steps and get observations/scores
   */

  // Normal

  def getScore():Double = this.score

  def getCompleted():Boolean = this.isComplete

  def step(userInputString:String): String = {
    val outStr = new StringBuilder
    // Error checking
    if (this.errorStr != "") return this.errorStr
    if (this.errorUnknownEnvironment) return "ERROR: Unknown task (" + this.taskStr + ") or task variation index (" + this.taskVariationIdx + ")."
    if (agentInterface.isEmpty) return ERROR_MESSAGE_UNINITIALIZED
    if (agent.isEmpty) return "ERROR: No agent is marked as main."
    if (agent.get.getContainer().isEmpty) return "ERROR: Agent is not in a container."

    // Get agent's container (to render agent's perspective)
    val agentContainer = agent.get.getContainer().get

    // Process special input commands (help, objects)
    if (userInputString.trim.toLowerCase == "help") {
      return "Possible actions: \n\t" + this.getPossibleActions().toArray().mkString("\n\t")
    }
    if (userInputString.trim.toLowerCase == "objects") {
      return "Possible object references: \n\t" + this.getPossibleObjects().toArray().mkString("\n\t")
    }
    if (userInputString.trim.toLowerCase == "task") {
      return "Task description:\n" + agentInterface.get.getTaskDescription()
    }

    // Process step in environment
    val (description, score_, isCompleted_) = agentInterface.get.step(userInputString)
    this.score = score_
    this.isComplete = isCompleted_

    println("Description: ")
    println(description)

    // DEBUG
    val referents = InputParser.getPossibleReferents(agentInterface.get.getAgentVisibleObjects()._2, agentContainer)
    println("Possible referents: " + referents.mkString(", "))

    if (description.length > 0) {
      outStr.append(description)
    } else {
      outStr.append("Unknown action.  Type 'help' for a list of actions, and 'objects' for a list of possible object referents. ")
    }
    //outStr.append("\nPossible referents: " + referents.mkString(", "))

    println ("step() finished.")

    // Return
    return outStr.toString()
  }

  // Free actions
  def freeActionLook():String = {
    // Error checking
    if (this.errorStr != "") return this.errorStr
    if (this.errorUnknownEnvironment) return "ERROR: Unknown task (" + this.taskStr + ") or task variation index (" + this.taskVariationIdx + ")."
    if (agentInterface.isEmpty) return ERROR_MESSAGE_UNINITIALIZED
    if (agent.isEmpty) return "ERROR: No agent is marked as main."
    if (agent.get.getContainer().isEmpty) return "ERROR: Agent is not in a container."

    return agentInterface.get.freeActionLook()
  }

  def freeActionInventory():String = {
    // Error checking
    if (this.errorStr != "") return this.errorStr
    if (this.errorUnknownEnvironment) return "ERROR: Unknown task (" + this.taskStr + ") or task variation index (" + this.taskVariationIdx + ")."
    if (agentInterface.isEmpty) return ERROR_MESSAGE_UNINITIALIZED
    if (agent.isEmpty) return "ERROR: No agent is marked as main."
    if (agent.get.getContainer().isEmpty) return "ERROR: Agent is not in a container."

    return agentInterface.get.freeActionInventory()
  }

  def freeActionTaskDesc():String = {
    // Error checking
    if (this.errorStr != "") return this.errorStr
    if (this.errorUnknownEnvironment) return "ERROR: Unknown task (" + this.taskStr + ") or task variation index (" + this.taskVariationIdx + ")."
    if (agentInterface.isEmpty) return ERROR_MESSAGE_UNINITIALIZED
    if (agent.isEmpty) return "ERROR: No agent is marked as main."
    if (agent.get.getContainer().isEmpty) return "ERROR: Agent is not in a container."

    return agentInterface.get.freeActionTaskDesc()
  }


}

object PythonInterface {

  def printUsage(): Unit = {
    println("Usage: PythonInterface <portNumber>")
  }

  def main(args:Array[String]): Unit = {
    println ("Initializing Virtual Environment Python Server...")
    val obj = new PythonInterface()

    // Parse command line argument for port
    var port:Int = 25335      // Default port (if not specified)
    if (args.length == 1) {
      try {
        port = args(0).toInt
      } catch {
        case e:Throwable => {
          printUsage()
          throw new RuntimeException("ERROR: Unable to parse port number into integer(" + args(0) + ").")
        }
      }
    } else if (args.length > 1) {
      printUsage()
      sys.exit(1)
    }

    println ("Starting server on port " + port + ".")

    val server = new GatewayServer(obj, port)
    server.start()

    println ("Server started... ")

  }

}
