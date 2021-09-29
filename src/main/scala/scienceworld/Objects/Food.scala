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


class Orange extends Food {
  this.name = "orange"
  this.propEdibility = Some(new Edible())

  var color = "orange"

  override def getReferents(): Set[String] = {
    Set("orange", this.name)
  }

  override def getDescription(): String = {
    return "an orange"
  }

}

class Banana extends Food {
  this.name = "banana"
  this.propEdibility = Some(new Edible())

  var color = "yellow"

  override def getReferents(): Set[String] = {
    Set("banana", this.name)
  }

  override def getDescription(): String = {
    return "a banana"
  }

}

class Onion extends Food {
  this.name = "onion"
  this.propEdibility = Some(new Edible())

  var color = "red"

  override def getReferents(): Set[String] = {
    Set("onion", this.color + " onion", this.name)
  }

  override def getDescription(): String = {
    return "a " + this.color + " onion"
  }

}

class Garlic extends Food {
  this.name = "garlic"
  this.propEdibility = Some(new Edible())

  var color = "white"

  override def getReferents(): Set[String] = {
    Set("garlic", this.name)
  }

  override def getDescription(): String = {
    return "garlic"
  }

}

class Potato extends Food {
  this.name = "potato"
  this.propEdibility = Some(new Edible())

  var color = "brown"

  override def getReferents(): Set[String] = {
    Set("potato", this.name)
  }

  override def getDescription(): String = {
    return "a potato"
  }

}