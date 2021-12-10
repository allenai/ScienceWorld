package scienceworld.objects.livingthing.plant

import scienceworld.processes.genetics.{GeneticTrait, GeneticTraitPeas}
import scienceworld.processes.lifestage.PlantLifeStages
import scienceworld.properties.{Edible, LifePropertiesApple, LifePropertiesPea}
import scienceworld.struct.EnvObject.{MODE_CURSORY_DETAIL, MODE_DETAILED}
import util.StringHelpers


class PeaPlant extends Tree {
  this.name = "pea"

  this.propEdibility = Some(new Edible())
  propLife = Some(new LifePropertiesPea())
  this.propChromosomePairs = Some( GeneticTraitPeas.mkRandomChromosomePair() )

  override def getPlantName():String = {
    // Check if we're in the seed stage
    if (this.isSeed()) {
      // Seed
      val seedName = propLife.get.lifeformType + " seed"
      return this.getDescriptName(seedName)
    } else {
      // Plant in some stage of growth
      val plantName = propLife.get.lifeformType + " plant"
      return this.getDescriptName(plantName)
    }

  }


  override def tick():Boolean = {
    // Update name based on current life stage
    this.name = this.getPlantName()

    // Edibe when in seed form
    if (this.isSeed()) {
      propEdibility.get.isEdible = true
    } else {
      propEdibility.get.isEdible = false
    }

    super.tick()
  }


  override def getReferents(): Set[String] = {
    val plantName = this.getPlantName()

    var out = Set("living thing", "organism", this.name, this.name + " in the " + lifecycle.get.getCurStageName() + " stage", lifecycle.get.getCurStageName() + " plant",
      plantName, plantName + " in the " + lifecycle.get.getCurStageName() + " stage", lifecycle.get.getCurStageName() + " " + plantName)

    out ++= Set(this.getDescriptName(), this.getDescriptName() + " in the " + lifecycle.get.getCurStageName() + " stage")

    // If ill, append ill referents too
    if (this.propLife.get.isSickly) {
      val sicklyDesc = out.map("sick " + _)
      return out ++ sicklyDesc
    }

    // Return
    out
  }

  // Make PLANT-SPECIFIC (not flower or seed-specific) text to add to the description, based off genetic traits
  override def mkGeneticTraitsStr():String = {
    val os = new StringBuilder()

    if (this.lifecycle.isDefined) {
      if ((this.lifecycle.get.getCurStageName() == PlantLifeStages.PLANT_STAGE_ADULT_PLANT) || (this.lifecycle.get.getCurStageName() == PlantLifeStages.PLANT_STAGE_REPRODUCING)) {
        if (propChromosomePairs.isDefined) os.append(" with a " + propChromosomePairs.get.getTraitPhenotypeHumanReadableStr(GeneticTrait.TRAIT_PLANT_HEIGHT).get)
      }
    }

    return os.toString()
  }

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder
    val plantName = this.getPlantName()


    // If dead, simplify the name
    if (propLife.get.isDead) {
      os.append("a dead " + plantName)
      return os.toString()
    }

    // SEED
    if (this.isSeed()) {
      os.append("a " + plantName)
      if (mode == MODE_DETAILED) {
        // ...
      }
      return os.toString()
    }

    os.append("a " + plantName + " in the " + lifecycle.get.getCurStageName() + " stage")

    // Property: Various genetic traits
    os.append( this.mkGeneticTraitsStr() )

    // Property: Sick
    if (propLife.get.isSickly) os.append(" (that looks unwell)")

    // Property: Contains
    val cObjs = this.getContainedObjectsNotHidden()
    if (cObjs.size > 0) {
      os.append(". ")
      os.append("On the " + plantName + " you see: " + StringHelpers.objectListToStringDescription(cObjs, this, mode = MODE_CURSORY_DETAIL, multiline = false) + ". ")
    }

    if (mode == MODE_DETAILED) {
      // Extended detail
    }

    os.toString
  }


}
