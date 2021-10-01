package scienceworld.environments

import scienceworld.EntryPoint.mkDoor
import scienceworld.objects.{Apple, GlassCup, MetalPot, Water}
import scienceworld.objects.agent.Agent
import scienceworld.objects.devices.{Freezer, Fridge, Sink, Stove, Thermometer}
import scienceworld.objects.location.{Room, Universe}
import scienceworld.struct.EnvObject

class EnvironmentMaker {

}

object EnvironmentMaker {

  def mkKitchenEnvironment(): (EnvObject, EnvObject) = {
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

    val fridge = new Fridge()
    roomKitchen.addObject(fridge)

    val freezer = new Freezer()
    roomKitchen.addObject(freezer)


    // Add water to pot, place it on the stove, and turn on the stove.
    val water = new Water()
    metalPot.addObject(water)

    stove.addObject(metalPot)

    // Another container
    val cup = new GlassCup()
    roomKitchen.addObject(cup)

    // A Thermometer
    val thermometer = new Thermometer()
    roomKitchen.addObject(thermometer)

    // Agent
    val agent = new Agent()
    roomKitchen.addObject(agent)


    // Return
    return (universe, agent)
  }

}
