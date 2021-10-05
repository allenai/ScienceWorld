package scienceworld.objects.containers

import scienceworld.properties._
import scienceworld.struct.EnvObject

class Container extends EnvObject {
  this.name = "container"
  this.propContainer = Some(new IsContainer())

  override def getReferents(): Set[String] = {
    Set("container", this.name)
  }

  override def getDescription(mode:Int): String = {
    val containedObjects = this.getContainedObjectsAndPortals()
    if (containedObjects.size == 0) {
      return "an empty " + this.name
    } else {
      return "a " + this.name + " (containing " + containedObjects.map(_.getName()).mkString(", ") + ")"
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
    Set("pot", "metal pot", this.name)
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
    Set("cup", "glass cup", this.name)
  }

}

class PlasticCup extends Container {
  this.name = "plastic cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new PlasticProp())

  override def getReferents(): Set[String] = {
    Set("cup", "plastic cup", this.name)
  }

}

class WoodCup extends Container {
  this.name = "wood cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new WoodProp())

  override def getReferents(): Set[String] = {
    Set("cup", "wood cup", this.name)
  }

}

class TinCup extends Container {
  this.name = "tin cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new TinProp())

  override def getReferents(): Set[String] = {
    Set("cup", "tin cup", this.name)
  }


}

class PaperCup extends Container {
  this.name = "paper cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new PaperProp())

  override def getReferents(): Set[String] = {
    Set("cup", "paper cup", this.name)
  }

}

class CeramicCup extends Container {
  this.name = "ceramic cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new CeramicProp())

  override def getReferents(): Set[String] = {
    Set("cup", "ceramic cup", this.name)
  }

}


/*
 * Flower pot
 */
class FlowerPot extends Container {
  this.name = "flower pot"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new CeramicProp())

  override def getReferents(): Set[String] = {
    Set("pot", "flower pot", this.name)
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
    Set("shelf", "book shelf", this.name)
  }

}
