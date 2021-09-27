package scienceworld.Objects

import scienceworld.Properties.{GlassProp, IsContainer, SteelProp}
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



class MetalPot extends Container {
  this.name = "metal pot"
  this.propContainer = Some(new IsContainer())
  this.propMaterial = Some(new SteelProp())

  override def getReferents(): Set[String] = {
    Set("pot", "metal pot", this.name)
  }

  override def getDescription(): String = {
    return "a metal pot"
  }
}


class GlassCup extends Container {
  this.name = "glass cup"
  this.propContainer = Some(new IsContainer())
  this.propMaterial = Some(new GlassProp())

  override def getReferents(): Set[String] = {
    Set("cup", "glass cup", this.name)
  }

  override def getDescription(): String = {
    return "a glass cup"
  }
}

