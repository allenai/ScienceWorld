package scienceworld.Objects

import scienceworld.Properties.{EdibilityProperties, Edible}
import scienceworld.struct.EnvObject

class Food extends EnvObject {

  this.propEdibility = Some(new Edible())

}


class Apple extends Food {
  this.name = "apple"
  this.propEdibility = Some(new Edible())

  var color = "red"

  override def getDescription(): String = {
    return "a " + this.color + " apple"
  }

}