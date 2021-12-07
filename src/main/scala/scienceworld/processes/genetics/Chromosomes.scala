package scienceworld.processes.genetics

import GeneticTrait._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

// One set of alleles for all the traits for a given life form
class Chromosomes(val chromosomes:Array[GeneticTrait]) {

  def getTrait(name:String):Option[GeneticTrait] = {
    for (chromosome <- chromosomes) {
      if (chromosome.traitName == name) return Some(chromosome)
    }
    // Otherwise
    return None
  }

}

// Two complete sets of alleles, one from each parent, for the traits for a given lifeform
class ChromosomePair(val parent1:Chromosomes, val parent2:Chromosomes) {

  def getTraitNames():Set[String] = {
    return parent1.chromosomes.map(_.traitName).toSet ++ parent2.chromosomes.map(_.traitName).toSet
  }

  def getPhenotypes(): Array[GeneticTrait] = {
    val out = new ArrayBuffer[GeneticTrait]

    for (alleleP1 <- parent1.chromosomes) {
      val alleleP2 = parent2.getTrait(alleleP1.traitName)
      if (alleleP2.isEmpty) throw new RuntimeException("ERROR: ChromosomePair.gePhenotypes(): One parent is missing critical genes (" + alleleP1.traitName + ").")

      // Figure out ultimate trait phenotype
      // If both recessive, then the value will be recessive
      if (alleleP1.isRecessiveValue() && alleleP2.get.isRecessiveValue()) {
        val phenotype = new GeneticTrait(alleleP1.traitName, alleleP1.valueDominant, alleleP1.valueRecessive, GeneticTrait.RECESSIVE)
        out.append(phenotype)
      } else {
        // Otherwise, if one or more dominant genes are present, then the phenotype will be dominant
        val phenotype = new GeneticTrait(alleleP1.traitName, alleleP1.valueDominant, alleleP1.valueRecessive, GeneticTrait.DOMINANT)
        out.append(phenotype)
      }
    }

    out.toArray
  }

  def getGenotype(traitName:String):Option[(GeneticTrait, GeneticTrait)] = {
    // Step 1: Find trait from both parents
    var traitP1:Option[GeneticTrait] = None
    var traitP2:Option[GeneticTrait] = None
    // Parent 1
    for (t <- parent1.chromosomes) {
      if (t.traitName == traitName) traitP1 = Some(t)
    }
    // Parent 2
    for (t <- parent2.chromosomes) {
      if (t.traitName == traitName) traitP2 = Some(t)
    }

    // Return
    if (traitP1.isEmpty || traitP2.isEmpty) return None
    return Some(traitP1.get, traitP2.get)
  }

}

object GeneticReproduction {

  // Mate the genes (chromosome pairs) from two parents using a Punnet Square, to generate a new set of chromosome pairs for an offspring
  def mateGenesPunnetSquare(cpairParent1:ChromosomePair, cpairParent2:ChromosomePair): Unit = {
    val traitNames = cpairParent1.getTraitNames() ++ cpairParent2.getTraitNames()

    val genesParent1 = new ArrayBuffer[GeneticTrait]()
    val genesParent2 = new ArrayBuffer[GeneticTrait]()

    for (traitName <- traitNames) {
      // Get genotype from Parent 1
      val traitsP1 = cpairParent1.getGenotype(traitName)
      if (traitsP1.isEmpty) throw new RuntimeException("ERROR: GeneticReproduction.mateGenesPunnetSquare(): Genes for trait not found (" + traitName + ")")
      val traitGP1a = traitsP1.get._1
      val traitGP1b = traitsP1.get._2

      // Get genotype from Parent 2
      val traitsP2 = cpairParent1.getGenotype(traitName)
      if (traitsP2.isEmpty) throw new RuntimeException("ERROR: GeneticReproduction.mateGenesPunnetSquare(): Genes for trait not found (" + traitName + ")")
      val traitGP2a = traitsP2.get._1
      val traitGP2b = traitsP2.get._2

      // Mate genes
      val (traitChildP1, traitChildP2) = this.doPunnetSquare(traitGP1a, traitGP1b, traitGP2a, traitGP2b)
      genesParent1.append(traitChildP1)
      genesParent2.append(traitChildP2)
    }

    // Create new chromosome pair storage class for child
    val chromosomesParent1 = new Chromosomes(genesParent1.toArray)
    val chromosomesParent2 = new Chromosomes(genesParent2.toArray)
    new ChromosomePair(chromosomesParent1, chromosomesParent2)
  }

  private def doPunnetSquare(traitGP1a:GeneticTrait, traitGP1b:GeneticTrait, traitGP2a:GeneticTrait, traitGP2b:GeneticTrait): (GeneticTrait, GeneticTrait) = {
    // Make four possibilities of Punnet Square
    val possibilities = new ArrayBuffer[(GeneticTrait, GeneticTrait)]
    possibilities.append( (traitGP1a, traitGP2a) )
    possibilities.append( (traitGP1a, traitGP2b) )
    possibilities.append( (traitGP1b, traitGP2a) )
    possibilities.append( (traitGP1b, traitGP2b) )

    // Randomly select one possibility
    val shuffled = Random.shuffle(possibilities)
    val selected = shuffled(0)

    val parent1TraitContribution = selected._1
    val parent2traitContribution = selected._2

    // Return
    return (parent1TraitContribution, parent2traitContribution)
  }

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

