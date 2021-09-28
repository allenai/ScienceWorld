package scienceworld

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger}
import language.runtime.runners.ActionRunner
import scienceworld.Objects.agent.Agent
import scienceworld.Objects.{Apple, MetalPot, Water}
import scienceworld.Objects.devices.{Sink, Stove}
import scienceworld.Objects.location.{Location, Room, Universe}
import scienceworld.Objects.portal.Door
import scienceworld.input.{ActionHandler, InputParser}

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
    var numIterations:Int = 0

    for (i <- 0 until 30) {
      println ("-----------------------------")
      println ("  Iteration " + i)
      println ("-----------------------------")

      universe.tick()

      println("metal pot: " + metalPot.propMaterial.get.temperatureC)
      println("water: " + water.propMaterial.get.temperatureC)

      numIterations += 1
    }
    println("")

    val deltaTime = System.currentTimeMillis() - startTime
    println("Total execution time: " + deltaTime + " msec for " + numIterations + " iterations (" + (numIterations / (deltaTime.toDouble/1000.0f)) + " iterations/sec)")



    /*
    val possibleActions = program.actions.getOrElse(List.empty[ActionRequestDef]).toArray
    val actionRunner = new ActionRunner(possibleActions, program.taxonomy)
     */


    println ("Completed")

    val actionHandler = new ActionHandler()

    val triggerPhrase = new ActionTrigger(List(
      new ActionExprOR(List("eat", "consume")),
      new ActionExprIdentifier("food")
    ))
    actionHandler.addAction("eat", List(triggerPhrase))

    println (actionHandler.actions.toList)

    val inputParser = new InputParser(actionHandler.getActions())
    val result = inputParser.parse("eat apple", universe, agent)
    println(result)


    println ("")
    println ("Exiting...")

  }

}
