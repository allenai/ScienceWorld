package scienceworld

import scienceworld.Objects.{Apple, MetalPot, Water}
import scienceworld.Objects.devices.{Sink, Stove}
import scienceworld.Objects.location.{Location, Room, Universe}
import scienceworld.Objects.portal.Door

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



    val water = new Water()
    metalPot.addObject(water)

    stove.addObject(metalPot)

    stove.propDevice.get.isActivated = true




    println(universe.getDescription())

    stove.propDevice.get.isActivated = true
    stove.propHeatSource.get.setOnMax()

    for (i <- 0 until 10) {
      println ("-----------------------------")
      println ("  Iteration " + i)
      println ("-----------------------------")

      universe.tick()

      println("metal pot: " + metalPot.propMaterial.get.temperatureC)
      println("water: " + water.propMaterial.get.temperatureC)
    }

    println ("Completed")

  }

}
