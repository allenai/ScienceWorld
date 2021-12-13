package scienceworld.processes

import scienceworld.objects.livingthing.plant.{AppleTree, AvocadoTree, BananaTree, CherryTree, LemonTree, OrangeTree, PeaPlant, PeachTree, RandomGeneticsPlantsA, RandomGeneticsPlantsB, RandomGeneticsPlantsC, RandomGeneticsPlantsD}
import scienceworld.objects.substance.food.{Apple, Avocado, Banana, Cherry, Lemon, Orange, Peach}
import scienceworld.processes.genetics.{ChromosomePair, GeneticReproduction}
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
  val PLANT_AVOCADO     = "advocado"
  val PLANT_BANANA      = "banana"

  val PLANT_PEA         = "pea"

  val PLANT_RANDOMGENETICS_A         = "unknown A"
  val PLANT_RANDOMGENETICS_B         = "unknown B"
  val PLANT_RANDOMGENETICS_C         = "unknown C"
  val PLANT_RANDOMGENETICS_D         = "unknown D"


  // Generator: Create an appropriate fruit for a given plant
  def createFruit(plantType:String, parent1Chromosomes:Option[ChromosomePair], parent2Chromosomes:Option[ChromosomePair]):Option[EnvObject] = {
    // If specified, perform the genetic mating
    var matedChromosomes:Option[ChromosomePair] = None
    if ((parent1Chromosomes.isDefined) && (parent2Chromosomes.isDefined)) {
      matedChromosomes = Some( GeneticReproduction.mateGenesPunnetSquare(parent1Chromosomes.get, parent2Chromosomes.get) )

      println("----------------------------------------")
      println(" MATING CHROMOSOMES:")
      println("----------------------------------------")
      println("Parent 1: ")
      println(parent1Chromosomes.get.toString())
      println("")
      println("Parent 2: ")
      println(parent2Chromosomes.get.toString())
      println("")
      println("Offspring: ")
      println(matedChromosomes.get.toString())
      println("----------------------------------------")
    }

    plantType match {
      case PLANT_APPLE    => { return Some(new Apple())   }
      case PLANT_AVOCADO  => { return Some(new Avocado()) }
      case PLANT_BANANA   => { return Some(new Banana())  }
      case PLANT_CHERRY   => { return Some(new Cherry())  }
      case PLANT_LEMON    => { return Some(new Lemon())   }
      case PLANT_ORANGE   => { return Some(new Orange())  }
      case PLANT_PEACH    => { return Some(new Peach())   }

      case PLANT_PEA      => { return Some(new PeaPlant(matedChromosomes))   }

      case PLANT_RANDOMGENETICS_A      => { return Some(new RandomGeneticsPlantsA(matedChromosomes))   }
      case PLANT_RANDOMGENETICS_B      => { return Some(new RandomGeneticsPlantsB(matedChromosomes))   }
      case PLANT_RANDOMGENETICS_C      => { return Some(new RandomGeneticsPlantsC(matedChromosomes))   }
      case PLANT_RANDOMGENETICS_D      => { return Some(new RandomGeneticsPlantsD(matedChromosomes))   }

      case _ => {
        println ("ERROR: Unknown fruit for plant type (" + plantType + "). ")
        return None
      }
    }

  }

  // Generator: Create an appropriate fruit for a given plant
  def createSeed(plantType:String):Option[EnvObject] = {
    plantType match {
      case PLANT_APPLE    => { return Some(new AppleTree())   }
      case PLANT_AVOCADO  => { return Some(new AvocadoTree()) }
      case PLANT_BANANA   => { return Some(new BananaTree())  }
      case PLANT_CHERRY   => { return Some(new CherryTree())  }
      case PLANT_LEMON    => { return Some(new LemonTree())   }
      case PLANT_ORANGE   => { return Some(new OrangeTree())  }
      case PLANT_PEACH    => { return Some(new PeachTree())   }

      case PLANT_PEA      => { return Some(new PeaPlant())    }

      case PLANT_RANDOMGENETICS_A      => { return Some(new RandomGeneticsPlantsA())    }
      case PLANT_RANDOMGENETICS_B      => { return Some(new RandomGeneticsPlantsB())    }
      case PLANT_RANDOMGENETICS_C      => { return Some(new RandomGeneticsPlantsC())    }
      case PLANT_RANDOMGENETICS_D      => { return Some(new RandomGeneticsPlantsD())    }

      case _ => {
        println ("ERROR: Unknown seed for plant type (" + plantType + "). ")
        return None
      }
    }

  }

}
