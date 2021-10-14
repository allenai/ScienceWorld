package scienceworld.environments

import scienceworld.objects.{Apple, OrangeJuice, Water}
import scienceworld.objects.containers.{BookShelf, GlassCup, MetalPot, Sewer, WoodCup}
import scienceworld.objects.agent.Agent
import scienceworld.objects.containers.furniture.{Desk, Table}
import scienceworld.objects.devices.{Freezer, Fridge, Sink, Stove, Thermometer, Toilet}
import scienceworld.objects.document.{BookFrankenstein, BookMobyDick}
import scienceworld.objects.electricalcomponent.{Battery, LightBulb, PolarizedElectricalComponent, Switch}
import scienceworld.objects.location.{Location, Room, Universe}
import scienceworld.objects.misc.Picture
import scienceworld.objects.portal.Door
import scienceworld.struct.EnvObject

import scala.util.Random

class EnvironmentMaker {

}

object EnvironmentMaker {


  def mkKitchenEnvironment(): (EnvObject, EnvObject) = {
    // Universe (object tree root)
    val universe = new Universe()

    // Sewer (for plumbing)
    val sewer = new Sewer()
    universe.addObject(sewer)

    // House
    BuildingMaker.mkRandomHouse(universe, sewer)


    // Agent
    val agent = new Agent()
    // Place in a random location
    val locations = universe.getContainedObjectsOfType[Location]().toArray
    val randomLocation = locations( Random.nextInt(locations.length) )

    randomLocation.addObject(agent)

    // Return
    return (universe, agent)
  }


  def mkConnection(obj1:PolarizedElectricalComponent, obj2:PolarizedElectricalComponent): Unit = {
    obj1.anode.propElectricalConnection.get.addConnection( obj2.cathode )
    obj2.cathode.propElectricalConnection.get.addConnection( obj1.anode )
  }


  def mkElectricalEnvironment(): (EnvObject, EnvObject) = {
    // Universe (object tree root)
    val universe = new Universe()

    // Sewer (for plumbing)
    val sewer = new Sewer()
    universe.addObject(sewer)

    // House
    val room = new Room("workshop")
    universe.addObject(room)

    // Electical
    val lightbulb1 = new LightBulb()
    room.addObject(lightbulb1)
    lightbulb1.name = "light bulb 1"

    val lightbulb2 = new LightBulb()
    room.addObject(lightbulb2)
    lightbulb2.name = "light bulb 2"

    val lightbulb3 = new LightBulb()
    room.addObject(lightbulb3)
    lightbulb3.name = "light bulb 3"

    val switch = new Switch()
    room.addObject(switch)

    val battery = new Battery()
    room.addObject(battery)

    /*
    this.mkConnection(battery, lightbulb1)
    this.mkConnection(lightbulb1, lightbulb2)
    this.mkConnection(lightbulb2, battery)
     */

    /*
    this.mkConnection(battery, lightbulb1)
    this.mkConnection(lightbulb1, lightbulb2)
    this.mkConnection(lightbulb2, lightbulb3)
    this.mkConnection(lightbulb3, battery)
     */

    this.mkConnection(battery, lightbulb1)
    this.mkConnection(lightbulb1, lightbulb2)
    this.mkConnection(lightbulb2, switch)
    //this.mkConnection(lightbulb3, switch)
    this.mkConnection(switch, battery)


    /*
    battery.anode.propElectricalConnection.get.addConnection( lightbulb1.cathode )
    battery.cathode.propElectricalConnection.get.addConnection( lightbulb2.anode )

    lightbulb1.anode.propElectricalConnection.get.addConnection( lightbulb2.cathode )
    lightbulb1.cathode.propElectricalConnection.get.addConnection( battery.anode )

    lightbulb2.anode.propElectricalConnection.get.addConnection( battery.cathode )
    lightbulb2.cathode.propElectricalConnection.get.addConnection( lightbulb1.anode )
     */

/*
    battery.anode.propElectricalConnection.get.addConnection( lightbulb.anode )
    battery.cathode.propElectricalConnection.get.addConnection( lightbulb.cathode )

    lightbulb.anode.propElectricalConnection.get.addConnection( battery.anode )
    lightbulb.cathode.propElectricalConnection.get.addConnection( battery.cathode )
*/



    // Agent
    val agent = new Agent()
    // Place in a random location
    val locations = universe.getContainedObjectsOfType[Location]().toArray
    val randomLocation = locations( Random.nextInt(locations.length) )

    randomLocation.addObject(agent)

    // Return
    return (universe, agent)
  }

}
