package scienceworld.processes

import scienceworld.objects.{Apple, Avocado, Banana, Cherry, Lemon, Orange, Peach}
import scienceworld.struct.EnvObject

class PlantReproduction {

}

object PlantReproduction {
  // Plant names
  val PLANT_APPLE       = "apple"
  val PLANT_ORANGE      = "orange"
  val PLANT_PEACH       = "peach"
  val PLANT_LEMON       = "lemon"
  val PLANT_CHERRY      = "cherry"
  val PLANT_AVOCADO    = "advocado"
  val PLANT_BANANA      = "banana"


  // Generator: Create an appropriate fruit for a given plant
  def createFruit(plantType:String):EnvObject = {
    plantType match {
      case PLANT_APPLE    => { return new Apple() }
      case PLANT_AVOCADO  => { return new Avocado() }
      case PLANT_BANANA   => { return new Banana() }
      case PLANT_CHERRY   => { return new Cherry() }
      case PLANT_LEMON    => { return new Lemon() }
      case PLANT_ORANGE   => { return new Orange() }
      case PLANT_PEACH    => { return new Peach() }

      case _ => { throw new Exception("ERROR: Unknown fruit to generate (" + plantType + ")") }
    }
  }

}
