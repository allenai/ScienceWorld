package scienceworld.Objects

import scienceworld.Properties.WaterProp
import scienceworld.struct.EnvObject

class Substance extends EnvObject {
  this.name = "substance"
}



class Water extends Substance {
  this.name = "water"

  this.propMaterial = Some(new WaterProp())


}





