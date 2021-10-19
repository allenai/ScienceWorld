package scienceworld.objects.livingthing

import scienceworld.properties.{Edible, SoilProp}
import scienceworld.struct.EnvObject

class Soil extends EnvObject {
  this.name = "soil"

  this.propMaterial = Some(new SoilProp)

  override def getReferents(): Set[String] = {
    Set("soil", this.name)
  }

  override def getDescription(mode:Int): String = {
    return this.name
  }

}
