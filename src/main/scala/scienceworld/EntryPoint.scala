package scienceworld

import scienceworld.Objects.agent.Agent
import scienceworld.Objects.{Apple, MetalPot, Water}
import scienceworld.Objects.devices.{Sink, Stove}
import scienceworld.Objects.location.{Location, Room, Universe}
import scienceworld.Objects.portal.Door
import scienceworld.input.{ActionDefinitions, ActionHandler, InputParser}
import scienceworld.runtime.AgentInterface

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
   * Processing user input
   */
  /*
  def processUserInput(inputStr:String):(Boolean, String) = {   // (Success, statusString)

    val (successVisible, visibleObjects) = this.getAgentVisibleObjects()      // TODO: Currently just a reference to the container (current room), rather than a list
    if (!successVisible) throw new RuntimeException("ERROR: Agent is not in container.")

    //val (successUserInput, errStr, userStr) = userInputParser.parse(inputStr, interpreter.objectTreeRoot, agent)
    val (successUserInput, errStr, userStr) = userInputParser.parse(inputStr, visibleObjects, agent)
    if (!successUserInput) {
      println("ERROR: " + errStr)
    } else {
      println(userStr)
    }

    return (successUserInput, userStr)
  }

   */



  /*
   * Main
   */

  def main(args:Array[String]) = {
    println("Initializing... ")

    val actionHandler = ActionDefinitions.mkActionDefinitions()
    val inputParser = new InputParser(actionHandler.getActions())


    // Universe (object tree root)
    val universe = new Universe()

    // Rooms
    val roomKitchen = new Room("Kitchen")
    universe.addObject(roomKitchen)

    val roomLivingRoom = new Room("Living Room")
    universe.addObject(roomLivingRoom)

    val roomHallway = new Room("Hallway")
    universe.addObject(roomHallway)

    // Doors
    mkDoor(roomKitchen, roomHallway)
    mkDoor(roomLivingRoom, roomHallway)


    // Objects
    val apple = new Apple()
    roomHallway.addObject(apple)


    val metalPot = new MetalPot
    roomKitchen.addObject(metalPot)

    val sink = new Sink()
    roomKitchen.addObject(sink)

    val stove = new Stove()
    roomKitchen.addObject(stove)


    // Add water to pot, place it on the stove, and turn on the stove.
    val water = new Water()
    metalPot.addObject(water)

    stove.addObject(metalPot)

    stove.propDevice.get.isActivated = true


    // Agent
    val agent = new Agent()
    roomKitchen.addObject(agent)


    println(universe.getDescription())

    // Turn on the heat of the stove.
    stove.propDevice.get.isActivated = true
    stove.propHeatSource.get.setOnMax()




    val startTime = System.currentTimeMillis()

    val agentInterface = new AgentInterface(universe, agent, actionHandler)
    var curIter = 0

    breakable {
      var userInputString:String = "look around"
      while (true) {
        println("")
        println("---------------------------------------")
        println(" Iteration " + curIter)
        println("---------------------------------------")
        println("")

        // Process step in environment
        val description = agentInterface.step(userInputString)
        println("")
        println("Description: ")
        println(description)

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

        println("metal pot: " + metalPot.propMaterial.get.temperatureC)
        println("water: " + water.propMaterial.get.temperatureC)

      }
    }

    val deltaTime = System.currentTimeMillis() - startTime
    println("Total execution time: " + deltaTime + " msec for " + curIter + " iterations (" + (curIter / (deltaTime.toDouble/1000.0f)) + " iterations/sec)")



    /*
    val possibleActions = program.actions.getOrElse(List.empty[ActionRequestDef]).toArray
    val actionRunner = new ActionRunner(possibleActions, program.taxonomy)
     */


    println ("Completed")

    println ("Referents: " + inputParser.getAllReferents(universe).mkString(", "))

    val result1 = inputParser.parse("eat apple", universe, agent)
    println(result1)
    val result2 = inputParser.parse("move stove to kitchen", universe, agent)
    println(result2)
    val result3 = inputParser.parse("open door to kitchen", roomKitchen, agent)
    println(result3)
    val result4 = inputParser.parse("open door", universe, agent)
    println(result4)



    println ("")
    println ("Exiting...")

  }

}
