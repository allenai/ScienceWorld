package scienceworld

import scienceworld.environments.EnvironmentMaker
import scienceworld.objects.agent.Agent
import scienceworld.objects.{Apple, MetalPot, Water}
import scienceworld.objects.devices.{Freezer, Fridge, Sink, Stove}
import scienceworld.objects.location.{Location, Room, Universe}
import scienceworld.objects.portal.Door
import scienceworld.input.{ActionDefinitions, ActionHandler, InputParser}
import scienceworld.runtime.AgentInterface
import scienceworld.tasks.Task
import scienceworld.tasks.goals.ObjMonitor

import scala.io.StdIn.readLine
import scala.util.control.Breaks.{break, breakable}

class EntryPoint {

}

object EntryPoint {

  /*
   * Helper functions
   */
  def mkDoor(location1:Location, location2:Location, isOpen:Boolean = false) {
    val door1 = new Door(isOpen, location1, location2)
    location1.addObject(door1)

    val door2 = new Door(isOpen, location2, location1)
    location2.addObject(door2)
  }



  /*
   * Main
   */

  def main(args:Array[String]) = {
    println("Initializing... ")

    val actionHandler = ActionDefinitions.mkActionDefinitions()

    val (universe, agent) = EnvironmentMaker.mkKitchenEnvironment()
    println(universe.getDescription())


    val startTime = System.currentTimeMillis()

    val goalSequence = Task.mkTaskChangeOfState()

    val agentInterface = new AgentInterface(universe, agent, actionHandler, goalSequence)
    var curIter = 0

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
        val referents = agentInterface.inputParser.getAllReferents(agentInterface.getAgentVisibleObjects()._2)
        println("Possible referents: " + referents.mkString(", "))

        // Get (and process) next user action
        var validInput:Boolean = false
        while (!validInput) {
          userInputString = agentInterface.getUserInput()

          if (userInputString == "debug") {
            //agentInterface.printDebugDisplay()
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
