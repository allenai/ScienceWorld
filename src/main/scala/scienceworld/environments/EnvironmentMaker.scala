package scienceworld.environments

import scienceworld.objects.{Apple, OrangeJuice, Water}
import scienceworld.objects.containers.{BookShelf, GlassCup, MetalPot, Sewer, WoodCup}
import scienceworld.objects.agent.Agent
import scienceworld.objects.containers.furniture.{Desk, Table}
import scienceworld.objects.devices.{Freezer, Fridge, Sink, Stove, Thermometer, Toilet}
import scienceworld.objects.document.{BookFrankenstein, BookMobyDick}
import scienceworld.objects.electricalcomponent.{Battery, LightBulb, PolarizedElectricalComponent, Switch, Wire}
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

    //## Specific start point in environment
    //for (location <- locations) if (location.name == "workshop") location.addObject(agent)
    for (location <- locations) if (location.name == "kitchen") location.addObject(agent)


    // Return
    return (universe, agent)
  }


  /*
  def mkConnection(obj1:PolarizedElectricalComponent, obj2:PolarizedElectricalComponent): Unit = {
    obj1.anode.propElectricalConnection.get.addConnection( obj2.cathode )
    obj2.cathode.propElectricalConnection.get.addConnection( obj1.anode )
  }
   */

/*
  def mkElectricalEnvironment(): (EnvObject, EnvObject) = {
    // Universe (object tree root)
    val universe = new Universe()

    // Sewer (for plumbing)
    val sewer = new Sewer()
    universe.addObject(sewer)


    // Agent
    val agent = new Agent()
    // Place in a random location
    val locations = universe.getContainedObjectsOfType[Location]().toArray
    val randomLocation = locations( Random.nextInt(locations.length) )

    randomLocation.addObject(agent)

    // Return
    return (universe, agent)
  }
*/

}
