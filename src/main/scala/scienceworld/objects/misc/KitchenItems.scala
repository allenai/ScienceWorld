package scienceworld.objects.misc

import scienceworld.properties.{AirProp, PlasticProp, SteelProp}
import scienceworld.struct.EnvObject


class ForkMetal extends EnvObject {
  this.name = "metal fork"

  this.propMaterial = Some(new SteelProp())

  override def getReferents(): Set[String] = {
    Set(this.name, this.propMaterial.get.substanceName + " fork")
  }

  override def getDescription(mode:Int): String = {
    return "A " + this.propMaterial.get.substanceName + " fork"

  }
}

class ForkPlastic extends EnvObject {
  this.name = "plastic fork"

  this.propMaterial = Some(new PlasticProp())

  override def getReferents(): Set[String] = {
    Set(this.name, this.propMaterial.get.substanceName + " fork")
  }

  override def getDescription(mode:Int): String = {
    return "A " + this.propMaterial.get.substanceName + " fork"

  }
}