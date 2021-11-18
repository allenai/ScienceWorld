package scienceworld.objects.containers

import scienceworld.objects.Water
import scienceworld.objects.document.Book
import scienceworld.properties._
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._
import util.StringHelpers

import scala.util.Random

class Container extends EnvObject {
  this.name = "container"
  this.propContainer = Some(new IsContainer())

  override def getReferents(): Set[String] = {
    Set("container", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    val containedObjects = this.getContainedObjectsAndPortals()
    if (containedObjects.size == 0) {
      return "an empty " + this.getDescriptName()
    } else {
      return "a " + this.getDescriptName() + " (containing " + StringHelpers.objectListToStringDescription(containedObjects, this, mode=MODE_CURSORY_DETAIL, multiline = false) + ")"
    }
  }

}


/*
 * Pots
 */

class MetalPot extends Container {
  this.name = "metal pot"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new SteelProp())

  override def getReferents(): Set[String] = {
    Set("pot", "metal pot", this.name, this.getDescriptName())
  }

}


/*
 * Cups
 */
class GlassCup extends Container {
  this.name = "glass cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new GlassProp())

  override def getReferents(): Set[String] = {
    Set("cup", "glass cup", this.name, this.getDescriptName())
  }

}

class PlasticCup extends Container {
  this.name = "plastic cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new PlasticProp())

  override def getReferents(): Set[String] = {
    Set("cup", "plastic cup", this.name, this.getDescriptName())
  }

}

class WoodCup extends Container {
  this.name = "wood cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new WoodProp())

  override def getReferents(): Set[String] = {
    Set("cup", "wood cup", this.name, this.getDescriptName())
  }

}

class TinCup extends Container {
  this.name = "tin cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new TinProp())

  override def getReferents(): Set[String] = {
    Set("cup", "tin cup", this.name, this.getDescriptName())
  }


}

class PaperCup extends Container {
  this.name = "paper cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new PaperProp())

  override def getReferents(): Set[String] = {
    Set("cup", "paper cup", this.name, this.getDescriptName())
  }

}

class CeramicCup extends Container {
  this.name = "ceramic cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new CeramicProp())

  override def getReferents(): Set[String] = {
    Set("cup", "ceramic cup", this.name, this.getDescriptName())
  }

}

class WoodBowl extends Container {
  this.name = "bowl"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new WoodProp())

  override def getReferents(): Set[String] = {
    Set("bowl", this.propMaterial.get.substanceName + " bowl", this.name, this.getDescriptName())
  }

}

class Jug extends Container {
  this.name = "jug"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new PlasticProp())

  override def getReferents(): Set[String] = {
    Set("jug", this.name, this.getDescriptName())
  }

}

/*
 * Flower pot
 */
class FlowerPot extends Container {
  this.name = "flower pot"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new CeramicProp())

  override def tick(): Boolean = {
    // DEBUG: Add water if there is none

    println ("### FLOWER POT: Infinite water")
    if (this.getContainedObjectsOfType[Water]().size == 0) {
      this.addObject( new Water() )
    }


    super.tick()
  }


  override def getReferents(): Set[String] = {
    Set("pot", "flower pot", this.name, this.getDescriptName())
  }


}


/*
 * Shelf
 */
class BookShelf extends Container {
  this.name = "book shelf"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new WoodProp())

  override def getReferents(): Set[String] = {
    Set("shelf", "book shelf", this.name, this.getDescriptName())
  }

}

object BookShelf {

  def mkRandom(): EnvObject = {
    val numBooks = Random.nextInt(4)

    val bookshelf = new BookShelf()
    for (i <- 0 until numBooks) {
      bookshelf.addObject( Book.mkRandom() )
    }

    return bookshelf
  }
}
