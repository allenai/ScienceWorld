package scienceworld.Objects

import scienceworld.Properties.{AirProp, WaterProp}
import scienceworld.struct.EnvObject

class Substance extends EnvObject {
  this.name = "substance"
}


class Air extends Substance {
  this.name = "air"

  this.propMaterial = Some(new AirProp())

}


class Water extends Substance {
  this.name = "water"

  this.propMaterial = Some(new WaterProp())


}





