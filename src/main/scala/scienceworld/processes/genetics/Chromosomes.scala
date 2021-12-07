package scienceworld.processes.genetics

import GeneticTrait._

class Chromosomes {



}


// One genetic trait
class GeneticTrait(val traitName:String, val valueDominant:String, val valueRecessive:String, dominantOrRecessive:String) {

  def getValue():String = {
    if (this.dominantOrRecessive == DOMINANT) return this.valueDominant
    if (this.dominantOrRecessive == RECESSIVE) return this.valueRecessive
    // Otherwise
    throw new RuntimeException("ERROR: GeneticTrait: Unknown mode (" + dominantOrRecessive + ") -- expect either dominant or recessive.")
  }

  def isDominantValue():Boolean = {
    if (this.dominantOrRecessive == DOMINANT) return true
    // Otherwise
    return false
  }

  def isRecessiveValue():Boolean = {
    if (this.dominantOrRecessive == RECESSIVE) return true
    // Otherwise
    return false
  }
}

object GeneticTrait {
  val DOMINANT                  = "dominant"
  val RECESSIVE                 = "recessive"

  val TRAIT_PLANT_HEIGHT        = "plant height"
  val TRAIT_PEA_SHAPE           = "pea shape"
  val TRAIT_PEA_COLOR           = "pea color"
  val TRAIT_FLOWER_COLOR        = "flower color"

}


// Subclases
class GeneticTraitPeaPlantHeight(dominantOrRecessive:String) extends GeneticTrait(TRAIT_PLANT_HEIGHT, valueDominant = "tall", valueRecessive = "short", dominantOrRecessive) {

}

class GeneticTraitPeaPlantPeaShape(dominantOrRecessive:String) extends GeneticTrait(TRAIT_PEA_SHAPE, valueDominant = "round", valueRecessive = "wrinkly", dominantOrRecessive) {

}

class GeneticTraitPeaPlantPeaColor(dominantOrRecessive:String) extends GeneticTrait(TRAIT_PEA_SHAPE, valueDominant = "green", valueRecessive = "orange", dominantOrRecessive) {

}

class GeneticTraitPeaPlantFlowerColor(dominantOrRecessive:String) extends GeneticTrait(TRAIT_FLOWER_COLOR, valueDominant = "purple", valueRecessive = "white", dominantOrRecessive) {

}

