package scienceworld.objects

import scienceworld.properties.{AirProp, WaterProp}
import scienceworld.struct.EnvObject

class Substance extends EnvObject {
  this.name = "substance"

  override def getReferents(): Set[String] = {
    Set("substance", this.name)
  }

  override def getDescription(mode:Int): String = {
    return "a substance called " + this.name
  }
}


class Air extends Substance {
  this.name = "air"

  this.propMaterial = Some(new AirProp())

}


class Water extends Substance {
  this.name = "water"

  this.propMaterial = Some(new WaterProp())

}

class Ice extends Substance {
  this.name = this.propMaterial.get.nameInStateOfMatter("liquid")
  this.propMaterial = Some(new WaterProp())
  this.propMaterial.get.temperatureC = -10.0f
}




