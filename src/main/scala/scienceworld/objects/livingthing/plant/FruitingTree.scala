package scienceworld.objects.livingthing.plant

import scienceworld.processes.lifestage.PlantLifeStages
import scienceworld.properties.{Edible, LifePropertiesApple, LifePropertiesAvocado, LifePropertiesBanana, LifePropertiesCherry, LifePropertiesLemon, LifePropertiesOrange, LifePropertiesPeach}
import scienceworld.struct.EnvObject.{MODE_CURSORY_DETAIL, MODE_DETAILED}
import util.StringHelpers

/*
 * Generic Fruiting Tree
 */
class Tree extends Plant {

}

class FruitingTree extends Tree {
  this.propEdibility = Some(new Edible())

  def getPlantName():String = {
    // Check if we're in the seed stage
    if (this.isSeed()) {
      // Seed
      val seedName = propLife.get.lifeformType + " seed"
      return this.getDescriptName(seedName)
    } else {
      // Plant in some stage of growth
      val treeName = propLife.get.lifeformType + " tree"
      return this.getDescriptName(treeName)
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

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder
    val plantName = this.getPlantName()

    // If dead, simplify the name
    if (propLife.get.isDead) {
      os.append("a dead " + plantName)
      return os.toString()
    }

    // If alive, give a verbose name
    if (this.isSeed()) {
      os.append("a " + plantName)
    } else {
      os.append("a " + plantName + " in the " + lifecycle.get.getCurStageName() + " stage")
    }

    if (propLife.get.isSickly) os.append(" (that looks unwell)")

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



/*
 * Specific trees
 */

class AppleTree extends FruitingTree {
  propLife = Some(new LifePropertiesApple())

}

class AvocadoTree extends FruitingTree {
  propLife = Some(new LifePropertiesAvocado())

}

class BananaTree extends FruitingTree {
  propLife = Some(new LifePropertiesBanana())

}

class CherryTree extends FruitingTree {
  propLife = Some(new LifePropertiesCherry())

}

class LemonTree extends FruitingTree {
  propLife = Some(new LifePropertiesLemon())

}

class OrangeTree extends FruitingTree {
  propLife = Some(new LifePropertiesOrange())

}

class PeachTree extends FruitingTree {
  propLife = Some(new LifePropertiesPeach())
}


