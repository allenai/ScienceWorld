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

  override def getReferents(): Set[String] = {
    Set("apple", this.color + " apple", this.name)
  }

  override def getDescription(): String = {
    return "a " + this.color + " apple"
  }

}