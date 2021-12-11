package scienceworld.processes.genetics

import scienceworld.processes.genetics.GeneticTrait._

import scala.collection.mutable.ArrayBuffer


/*
 * Random (unknown) plants
 */

// Unknown Plant A
object GeneticTraitUnknownPlantA {

  def mkTraitPlantHeight(dominantOrRecessive:String):GeneticTrait = {
    new GeneticTrait(TRAIT_PLANT_HEIGHT, valueDominant = "short", valueRecessive = "tall", strSuffix = "height", dominantOrRecessive)
  }

  def mkTraitLeafShape(dominantOrRecessive:String):GeneticTrait = {
    new GeneticTrait(TRAIT_LEAF_SHAPE, valueDominant = "pointed", valueRecessive = "round", strSuffix = "leaves", dominantOrRecessive)
  }

  def mkTraitFlowerSize(dominantOrRecessive:String):GeneticTrait = {
    new GeneticTrait(TRAIT_FLOWER_SIZE, valueDominant = "large", valueRecessive = "small", strSuffix = "flowers", dominantOrRecessive)
  }

  def mkTraitFlowerColor(dominantOrRecessive:String):GeneticTrait = {
    new GeneticTrait(TRAIT_FLOWER_COLOR, valueDominant = "orange", valueRecessive = "yellow", strSuffix = "flowers", dominantOrRecessive)
  }


  /*
   * Make random traits
   */
  def mkRandomChromosomePair():ChromosomePair = {
    val parent1 = new Chromosomes( this.mkRandomTraits() )
    val parent2 = new Chromosomes( this.mkRandomTraits() )
    return new ChromosomePair(parent1, parent2)
  }

  def mkRandomTraits():Array[GeneticTrait] = {
    val out = new ArrayBuffer[GeneticTrait]()

    out.append( mkTraitPlantHeight(GeneticTrait.mkRandomDomRec()) )
    out.append( mkTraitLeafShape(GeneticTrait.mkRandomDomRec()) )
    out.append( mkTraitFlowerSize(GeneticTrait.mkRandomDomRec()) )
    out.append( mkTraitFlowerColor(GeneticTrait.mkRandomDomRec()) )

    // Return
    out.toArray
  }

  /*
   * Make random traits (but, control one trait)
   */
  def mkRandomChromosomePairExcept(traitName:String, parent1DomOrRec:String, parent2DomOrRec:String):ChromosomePair = {
    val parent1 = new Chromosomes( this.mkRandomTraitsExcept(traitName, parent1DomOrRec) )
    val parent2 = new Chromosomes( this.mkRandomTraitsExcept(traitName, parent2DomOrRec) )
    return new ChromosomePair(parent1, parent2)
  }

  def mkRandomTraitsExcept(traitName:String, domOrRec:String):Array[GeneticTrait] = {
    val out = new ArrayBuffer[GeneticTrait]()

    if (traitName == TRAIT_PLANT_HEIGHT) {
      out.append(mkTraitPlantHeight(domOrRec))
    } else {
      out.append(mkTraitPlantHeight(GeneticTrait.mkRandomDomRec()))
    }

    if (traitName == TRAIT_LEAF_SHAPE) {
      out.append(mkTraitLeafShape(domOrRec))
    } else {
      out.append(mkTraitLeafShape(GeneticTrait.mkRandomDomRec()))
    }

    if (traitName == TRAIT_FLOWER_SIZE) {
      out.append(mkTraitFlowerSize(domOrRec))
    } else {
      out.append(mkTraitFlowerSize(GeneticTrait.mkRandomDomRec()))
    }

    if (traitName == TRAIT_FLOWER_COLOR) {
      out.append(mkTraitFlowerColor(domOrRec))
    } else {
      out.append(mkTraitFlowerColor(GeneticTrait.mkRandomDomRec()))
    }

    // Return
    out.toArray
  }

}
