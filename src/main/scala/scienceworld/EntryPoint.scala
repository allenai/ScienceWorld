package scienceworld

import scienceworld.environments.EnvironmentMaker
import scienceworld.objects.agent.Agent
import scienceworld.objects.{Apple, Water}
import scienceworld.objects.devices.{Freezer, Fridge, Sink, Stove}
import scienceworld.objects.location.{Location, Room, Universe}
import scienceworld.objects.portal.Door
import scienceworld.input.{ActionDefinitions, ActionHandler, InputParser}
import scienceworld.runtime.AgentInterface
import scienceworld.tasks.{Task, TaskMaker}
import scienceworld.tasks.goals.ObjMonitor

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

    //val (universe, agent) = EnvironmentMaker.mkKitchenEnvironment()
    val (universe, agent) = EnvironmentMaker.mkElectricalEnvironment()

    println(universe.getDescription())


    val startTime = System.currentTimeMillis()

    val task = TaskMaker.getRandomTask()

    val agentInterface = new AgentInterface(universe, agent, actionHandler, task)
    var curIter = 0

    println ("Task: " + agentInterface.getTaskDescription() )
    // DEBUG: Set the task/goals

    breakable {
      var userInputString:String = "look around"
      while (true) {
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

        val referents = agentInterface.inputParser.getAllUniqueReferents(agentInterface.getAgentVisibleObjects()._2).map(_._1)


        println("Possible referents: " + referents.mkString(", "))
        //println("Possible actions:\n\t" + actionHandler.getActionExamplesPlainText().mkString("\n\t"))
        //println("Possible Combinations:\n\t" + agentInterface.getPossibleActionObjectCombinations().mkString("\n\t") )


        // Get (and process) next user action
        var validInput:Boolean = false
        while (!validInput) {
          userInputString = agentInterface.getUserInput()

          if (userInputString == "debug") {
            //agentInterface.printDebugDisplay()
          } else if (userInputString == "help") {
            println("Possible Actions: \n" + agentInterface.getPossibleActions().mkString("\n"))
          } else {
            validInput = true
          }
        }

        if ((userInputString.trim.toLowerCase == "quit") || (userInputString.trim.toLowerCase == "exit")) break()
        curIter += 1

      }
    }

    val deltaTime = System.currentTimeMillis() - startTime
    println("Total execution time: " + deltaTime + " msec for " + curIter + " iterations (" + (curIter / (deltaTime.toDouble/1000.0f)) + " iterations/sec)")

    println ("")
    println ("Exiting...")

  }

}
