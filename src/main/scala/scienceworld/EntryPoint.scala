package scienceworld

import scienceworld.environments.EnvironmentMaker
import scienceworld.objects.agent.Agent
import scienceworld.objects.devices.{Freezer, Fridge, Sink, Stove}
import scienceworld.objects.location.{Location, Room, Universe}
import scienceworld.objects.portal.Door
import scienceworld.input.{ActionDefinitions, ActionHandler, InputParser}
import scienceworld.objects.substance.food.Apple
import scienceworld.properties.BlackPaintProp
import scienceworld.runtime.AgentInterface
import scienceworld.tasks.{Task, TaskMaker, TaskMaker1}
import scienceworld.tasks.goals.ObjMonitor
import scienceworld.tasks.specifictasks.TaskChangeOfState
import scienceworld.tasks.specifictasks.TaskChangeOfState.MODE_MELT

import scala.io.StdIn.readLine
import scala.util.control.Breaks.{break, breakable}

class EntryPoint {

}

object EntryPoint {


  /*
   * Main
   */

  def main(args:Array[String]) = {
    println("Initializing... ")

    val actionHandler = ActionDefinitions.mkActionDefinitions()

    val (universe, agent) = EnvironmentMaker.mkKitchenEnvironment()
    //val (universe, agent) = EnvironmentMaker.mkElectricalEnvironment()

    println(universe.getDescription())


    val startTime = System.currentTimeMillis()
    val taskMaker = new TaskMaker1()
    println ("TASK LIST: ")
    val taskList = taskMaker.getTaskList()
    for (i <- 0 until taskList.length) {
      val taskName = taskList(i)
      val numVariations = taskMaker.getMaxVariations(taskName)
      println( i.formatted("%5s") + ": \t" + taskName.formatted("%60s") + "  (" + numVariations + " variations)")
    }


    // Pick a task
    //val taskName = taskMaker.getTaskList()(6)
    //val taskName = taskMaker.getTaskList()(8)
    //val taskName = taskMaker.getTaskList()(7)
    //val taskName = taskMaker.getTaskList()(0)
    val taskName = taskMaker.getTaskList()(16)

    // Setup task
    val (task_, taskErrStr) = taskMaker.doTaskSetup(taskName, 3, universe, agent)
    var task:Option[Task] = None
    if (task_.isDefined) {
      task = task_
    } else {
      task = Some( Task.mkUnaccomplishableTask() )
    }

    // Setup agent interface
    val agentInterface = new AgentInterface(universe, agent, actionHandler, task.get)
    // If there were any errors setting up the task, then note this.
    if (taskErrStr.length > 0) {
      agentInterface.setErrorState(taskErrStr)
    }

    println ("Task: " + agentInterface.getTaskDescription() )


    // DEBUG: Set the task/goals
    var curIter:Int = 0

    breakable {
      var userInputString:String = "look around"
      while (true) {
        curIter = agentInterface.getCurIterations()
        println("")
        println("---------------------------------------")
        println(" Iteration " + curIter)
        println("---------------------------------------")
        println("")

        // Process step in environment
        val (description, score, isCompleted) = agentInterface.step(userInputString)
        println("")
        println("Description: ")
        println(description)

        println("")
        //println("metal pot: " + metalPot.propMaterial.get.temperatureC)
        //println("water: " + water.propMaterial.get.temperatureC)

        println("Score: " + score.formatted("%3.3f"))
        println("IsCompleted: " + isCompleted)


        // DEBUG
        //val referents = agentInterface.inputParser.getAllReferents(agentInterface.getAgentVisibleObjects()._2)

        val referents = agentInterface.inputParser.getAllUniqueReferents(agentInterface.getAgentVisibleObjects()._2, includeHidden = false).map(_._1)


        println("Possible referents: " + referents.mkString(", "))
        //println("Possible actions:\n\t" + actionHandler.getActionExamplesPlainText().mkString("\n\t"))
        //println("Possible Combinations:\n\t" + agentInterface.getPossibleActionObjectCombinations().mkString("\n\t") )

        /*
        println("TEST: ")
        println(agentInterface.getAllObjectIdsTypesReferentsLUTJSON())
        println("")
         */

        // Get (and process) next user action
        var validInput:Boolean = false
        while (!validInput) {
          userInputString = agentInterface.getUserInput()

          if (userInputString == "debug") {
            //agentInterface.printDebugDisplay()
          } else if (userInputString == "help") {
            println("Possible Actions: \n" + agentInterface.getPossibleActions().mkString("\n"))
          } else if (userInputString == "validactions") {
            // Collect all objects visible to the agent
            val visibleObjTreeRoot = agentInterface.getAgentVisibleObjects()._2
            val agentInventory = agent.getInventoryContainer()
            val allVisibleObjects = InputParser.collectObjects(visibleObjTreeRoot, includeHidden = false).toList ++ InputParser.collectObjects(agentInventory, includeHidden = false).toList
            // Collect UUID -> Unique Referent LUT
            val uuid2referentLUT = agentInterface.inputParser.getAllUniqueReferentsLUT(visibleObjTreeRoot, includeHidden=false)

            // Generate all possible valid actions
            val validActions = ActionDefinitions.mkPossibleActions(agent, allVisibleObjects.toArray, uuid2referentLUT)

            println("Valid actions (length = " + validActions.length + ")")
            for (i <- 0 until validActions.length) {
              println (i + ": \t" + validActions(i).toString())
            }

          } else {
            validInput = true
          }
        }

        if ((userInputString.trim.toLowerCase == "quit") || (userInputString.trim.toLowerCase == "exit")) break()

      }
    }

    val deltaTime = System.currentTimeMillis() - startTime
    println("Total execution time: " + deltaTime + " msec for " + curIter + " iterations (" + (curIter / (deltaTime.toDouble/1000.0f)) + " iterations/sec)")

    println ("")
    println ("Exiting...")

  }

}
