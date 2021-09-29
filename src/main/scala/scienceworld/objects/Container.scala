package scienceworld.objects

import scienceworld.properties.{CeramicProp, GlassProp, IsContainer, IsOpenUnclosableContainer, PaperProp, PlasticProp, SteelProp, TinProp, WoodProp}
import scienceworld.struct.EnvObject

class Container extends EnvObject {
  this.name = "container"
  this.propContainer = Some(new IsContainer())

  override def getReferents(): Set[String] = {
    Set("container", this.name)
  }

  override def getDescription(): String = {
    return "a container"
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

  override def getDescription(): String = {
    return "a metal pot"
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

  override def getDescription(): String = {
    return "a glass cup"
  }
}

class PlasticCup extends Container {
  this.name = "plastic cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new PlasticProp())

  override def getReferents(): Set[String] = {
    Set("cup", "plastic cup", this.name)
  }

  override def getDescription(): String = {
    return "a plastic cup"
  }
}

class WoodCup extends Container {
  this.name = "wood cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new WoodProp())

  override def getReferents(): Set[String] = {
    Set("cup", "wood cup", this.name)
  }

  override def getDescription(): String = {
    return "a wood cup"
  }
}

class TinCup extends Container {
  this.name = "tin cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new TinProp())

  override def getReferents(): Set[String] = {
    Set("cup", "tin cup", this.name)
  }

  override def getDescription(): String = {
    return "a tin cup"
  }
}

class PaperCup extends Container {
  this.name = "paper cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new PaperProp())

  override def getReferents(): Set[String] = {
    Set("cup", "paper cup", this.name)
  }

  override def getDescription(): String = {
    return "a paper cup"
  }
}

class CeramicCup extends Container {
  this.name = "ceramic cup"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new CeramicProp())

  override def getReferents(): Set[String] = {
    Set("cup", "ceramic cup", this.name)
  }

  override def getDescription(): String = {
    return "a ceramic cup"
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

  override def getDescription(): String = {
    return "a flower pot"
  }
}