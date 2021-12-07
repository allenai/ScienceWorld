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

    for (traitName <- this.getTraitNames()) {
      val phenotype = this.getPhenotype(traitName)
      out.append( phenotype.get )
    }

    out.toArray
  }

  def getPhenotype(traitName:String):Option[GeneticTrait] = {
    val alleleP1 = parent1.getTrait(traitName)
    if (alleleP1.isEmpty) throw new RuntimeException("ERROR: ChromosomePair.gePhenotype(): One parent is missing critical genes (" + alleleP1.get.traitName + ").")
    val alleleP2 = parent2.getTrait(traitName)
    if (alleleP2.isEmpty) throw new RuntimeException("ERROR: ChromosomePair.gePhenotype(): One parent is missing critical genes (" + alleleP1.get.traitName + ").")

    // Figure out ultimate trait phenotype
    // If both recessive, then the value will be recessive
    if (alleleP1.get.isRecessiveValue() && alleleP2.get.isRecessiveValue()) {
      val phenotype = new GeneticTrait(alleleP1.get.traitName, alleleP1.get.valueDominant, alleleP1.get.valueRecessive, strSuffix = alleleP1.get.strSuffix, GeneticTrait.RECESSIVE)
      return Some(phenotype)
    } else {
      // Otherwise, if one or more dominant genes are present, then the phenotype will be dominant
      val phenotype = new GeneticTrait(alleleP1.get.traitName, alleleP1.get.valueDominant, alleleP1.get.valueRecessive, strSuffix = alleleP1.get.strSuffix, GeneticTrait.DOMINANT)
      return Some(phenotype)
    }

    // Otherwise (note, execution should never reach here -- if graceful failure is desired, add a return None instead of throwing an exception to the checks at the top)
    return None
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

  /*
   * String methods
   */
  override def toString():String = {
    val os = new StringBuilder

    val sortedNames = this.getTraitNames().toArray.sorted
    for (traitName <- sortedNames) {
      val genotype = this.getGenotype(traitName)
      val phenotype = this.getPhenotype(traitName)
      os.append("\t" + traitName.formatted("%20s") + "\t" + genotype.get._1.formatted("%15s") + "\t" + genotype.get._2.formatted("%15s") + "\t" + phenotype.get.formatted("%15s") + "\n")
    }

    os.toString()
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
class GeneticTrait(val traitName:String, val valueDominant:String, val valueRecessive:String, val strSuffix:String, dominantOrRecessive:String) {

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

  def mkHumanReadableDescStr():String = {
    this.getValue() + " " + strSuffix
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
object GeneticTraitPeas {

  def mkTraitPlantHeight(dominantOrRecessive:String):GeneticTrait = {
    new GeneticTrait(TRAIT_PLANT_HEIGHT, valueDominant = "tall", valueRecessive = "short", strSuffix = "height", dominantOrRecessive)
  }

  def mkTraitPeaShape(dominantOrRecessive:String):GeneticTrait = {
    new GeneticTrait(TRAIT_PEA_SHAPE, valueDominant = "round", valueRecessive = "wrinkly", strSuffix = "peas", dominantOrRecessive)
  }

  def mkTraitPeaColor(dominantOrRecessive:String):GeneticTrait = {
    new GeneticTrait(TRAIT_PEA_COLOR, valueDominant = "green", valueRecessive = "orange", strSuffix = "peas", dominantOrRecessive)
  }

  def mkTraitFlowerColor(dominantOrRecessive:String):GeneticTrait = {
    new GeneticTrait(TRAIT_FLOWER_COLOR, valueDominant = "purple", valueRecessive = "white", strSuffix = "flowers", dominantOrRecessive)
  }


  def mkRandom():Array[GeneticTrait] = {
    val out = new ArrayBuffer[GeneticTrait]()

    out.append( mkTraitPlantHeight(this.mkRandomDomRec()) )
    out.append( mkTraitPeaShape(this.mkRandomDomRec()) )
    out.append( mkTraitPeaColor(this.mkRandomDomRec()) )
    out.append( mkTraitFlowerColor(this.mkRandomDomRec()) )

    // Return
    out.toArray
  }

  def mkRandomDomRec():String = {
    if (Random.nextInt(2) == 0) return GeneticTrait.DOMINANT
    // Return
    return GeneticTrait.RECESSIVE
  }
}



object GeneticTest {

  def main(args:Array[String]): Unit = {

    val plantGP1aGenes = new Chromosomes( GeneticTraitPeas.mkRandom() )
    val plantGP1bGenes = new Chromosomes( GeneticTraitPeas.mkRandom() )
    val plant1ChromosomePairs = new ChromosomePair(plantGP1aGenes, plantGP1bGenes)
    println("Plant 1")
    println(plant1ChromosomePairs.toString())

    println("")

    val plantGP2aGenes = new Chromosomes( GeneticTraitPeas.mkRandom() )
    val plantGP2bGenes = new Chromosomes( GeneticTraitPeas.mkRandom() )
    val plant2ChromosomePairs = new ChromosomePair(plantGP2aGenes, plantGP2bGenes)
    println("Plant 2")
    println(plant2ChromosomePairs.toString())


  }

}