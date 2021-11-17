package scienceworld.environments

import scienceworld.objects.containers.{CeramicCup, GlassCup, Jug, MetalPot, PaperCup, PlasticCup, TinCup, WoodCup}
import scienceworld.struct.EnvObject

import scala.util.Random


object ContainerMaker {

  def mkRandomLiquidCup(liquidToInsert:EnvObject):EnvObject = {
    val randIdx:Int = Random.nextInt(7)         // Set to max

    // Step 1: Create random container
    var container:EnvObject = new WoodCup()
    randIdx match {
      case 0 => container = new MetalPot()
      case 1 => container = new GlassCup()
      case 2 => container = new PlasticCup()
      case 3 => container = new TinCup()
      case 4 => container = new PaperCup()
      case 5 => container = new CeramicCup()
      case 6 => container = new Jug()
    }

    // Step 2: Insert liquid
    container.addObject( liquidToInsert )

    // Return
    return container
  }

}
