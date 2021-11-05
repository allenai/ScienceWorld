package scienceworld.objects

import scienceworld.properties.{AppleJuiceProp, ChocolateProp, EdibilityProperties, Edible, MarshmallowProp, OrangeJuiceProp}
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

  override def getDescription(mode:Int): String = {
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

  override def getDescription(mode:Int): String = {
    return "an orange"
  }

}

class OrangeJuice extends Food {
  this.name = "orange juice"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new OrangeJuiceProp())

  var color = "orange"

  override def getReferents(): Set[String] = {
    Set("orange juice", this.name)
  }

  override def getDescription(mode:Int): String = {
    return this.name
  }

}

class AppleJuice extends Food {
  this.name = "apple juice"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new AppleJuiceProp())

  override def getReferents(): Set[String] = {
    Set("apple juice", this.name)
  }

  override def getDescription(mode:Int): String = {
    return this.name
  }

}

class Banana extends Food {
  this.name = "banana"
  this.propEdibility = Some(new Edible())

  var color = "yellow"

  override def getReferents(): Set[String] = {
    Set("banana", this.name)
  }

  override def getDescription(mode:Int): String = {
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

  override def getDescription(mode:Int): String = {
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

  override def getDescription(mode:Int): String = {
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

  override def getDescription(mode:Int): String = {
    return "a potato"
  }

}

/*
 * Chocolate
 */
class Chocolate extends Food {
  this.name = "chocolate"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new ChocolateProp())

  override def getReferents(): Set[String] = {
    Set("chocolate", this.name)
  }

  override def getDescription(mode:Int): String = {
    return this.name
  }

}

class Marshmallow extends Food {
  this.name = "marshmallow"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new MarshmallowProp())

  override def getReferents(): Set[String] = {
    Set("marshmallow", this.name)
  }

  override def getDescription(mode:Int): String = {
    return this.name
  }

}

