package scienceworld.objects.substance.food

import scienceworld.properties._
import scienceworld.struct.EnvObject

class Food extends EnvObject {

  this.propEdibility = Some(new Edible())

}

/*
 * Fruits
 */
// TODO: Fruits require appropriate material properties
class Fruit extends Food {

}

class Apple extends Fruit {
  this.name = "apple"
  this.propEdibility = Some(new Edible())

  var color = "red"

  override def getReferents(): Set[String] = {
    val cannonicalName = this.color + " apple"
    Set("apple", this.color + " apple", this.name, this.getDescriptName(), this.getDescriptName(cannonicalName))
  }

  override def getDescription(mode:Int): String = {
    val cannonicalName = this.color + " apple"
    return "a " + this.getDescriptName(cannonicalName)
  }

}

class Apricot extends Fruit {
  this.name = "apricot"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new PlantMatterProp())   // for conductivity

  var color = "orange"

  override def getReferents(): Set[String] = {
    val cannonicalName = this.color + " apricot"
    Set("apricot", this.color + " apricot", this.name, this.getDescriptName(), this.getDescriptName(cannonicalName))
  }

  override def getDescription(mode:Int): String = {
    val cannonicalName = this.color + " apricot"
    return "a " + this.getDescriptName(cannonicalName)
  }

}

class Avocado extends Fruit {
  this.name = "avocado"
  this.propEdibility = Some(new Edible())

  var color = "green"

  override def getReferents(): Set[String] = {
    Set("avocado", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return "an " + this.getDescriptName()
  }

}

class Banana extends Fruit {
  this.name = "banana"
  this.propEdibility = Some(new Edible())

  var color = "yellow"

  override def getReferents(): Set[String] = {
    Set("banana", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return "a " + this.getDescriptName()
  }

}

class Cherry extends Fruit {
  this.name = "cherry"
  this.propEdibility = Some(new Edible())

  var color = "red"

  override def getReferents(): Set[String] = {
    Set("cherry", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return "a " + this.getDescriptName()
  }

}


class Lemon extends Fruit {
  this.name = "lemon"
  this.propEdibility = Some(new Edible())

  var color = "yellow"

  override def getReferents(): Set[String] = {
    Set("lemon", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return "a " + this.getDescriptName()
  }

}


class Orange extends Fruit {
  this.name = "orange"
  this.propEdibility = Some(new Edible())

  var color = "orange"

  override def getReferents(): Set[String] = {
    Set("orange", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return "an " + this.getDescriptName()
  }

}

class Grapefruit extends Fruit {
  this.name = "grapefruit"
  this.propEdibility = Some(new Edible())

  var color = "orange"

  override def getReferents(): Set[String] = {
    Set("grapefruit", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return "an " + this.getDescriptName()
  }

}

class Peach extends Fruit {
  this.name = "peach"
  this.propEdibility = Some(new Edible())

  var color = "pink"

  override def getReferents(): Set[String] = {
    Set("peach", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return "a " + this.getDescriptName()
  }

}

class Pear extends Fruit {
  this.name = "pear"
  this.propEdibility = Some(new Edible())

  var color = "green"

  override def getReferents(): Set[String] = {
    Set("pear", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return "a " + this.getDescriptName()
  }

}

/*
 * Juice
 */

class OrangeJuice extends Food {
  this.name = "orange juice"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new OrangeJuiceProp())

  var color = "orange"

  override def getReferents(): Set[String] = {
    Set("orange juice", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return this.getDescriptName()
  }

}

class AppleJuice extends Food {
  this.name = "apple juice"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new AppleJuiceProp())

  override def getReferents(): Set[String] = {
    Set("apple juice", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return this.getDescriptName()
  }

}



class Onion extends Food {
  this.name = "onion"
  this.propEdibility = Some(new Edible())

  var color = "red"

  override def getReferents(): Set[String] = {
    val cannonicalName = this.color + " onion"
    Set("onion", this.color + " onion", this.name, this.getDescriptName(), this.getDescriptName(cannonicalName))
  }

  override def getDescription(mode:Int): String = {
    val cannonicalName = this.color + " onion"
    return "a " + this.getDescriptName(cannonicalName)
  }

}

class Garlic extends Food {
  this.name = "garlic"
  this.propEdibility = Some(new Edible())

  var color = "white"

  override def getReferents(): Set[String] = {
    Set("garlic", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return this.getDescriptName()
  }

}

class Potato extends Food {
  this.name = "potato"
  this.propEdibility = Some(new Edible())

  var color = "brown"

  override def getReferents(): Set[String] = {
    Set("potato", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return "a " + this.getDescriptName()
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
    Set("chocolate", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return this.getDescriptName()
  }

}

class Marshmallow extends Food {
  this.name = "marshmallow"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new MarshmallowProp())

  override def getReferents(): Set[String] = {
    Set("marshmallow", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return this.getDescriptName()
  }

}

class Smores extends Food {
  this.name = "smores"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new MarshmallowProp())

  override def getReferents(): Set[String] = {
    Set("smores", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return this.getDescriptName()
  }

}

class IceCream extends Food {
  this.name = "ice cream"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new IceCreamProp())

  override def getReferents(): Set[String] = {
    Set("ice cream", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return this.getDescriptName()
  }

}


class Flour extends Food {
  this.name = "flour"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new FlourProp())

  var color = "white"

  override def getReferents(): Set[String] = {
    val cannonicalName = this.color + " flour"
    Set("flour", this.name, this.getDescriptName(), this.getDescriptName(cannonicalName))
  }

  override def getDescription(mode:Int): String = {
    val cannonicalName = "flour"
    return this.getDescriptName(cannonicalName)
  }

}

class Dough extends Food {
  this.name = "dough"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new FlourProp())

  var color = "white"

  override def getReferents(): Set[String] = {
    val cannonicalName = this.color + " dough"
    Set("dough", this.name, this.getDescriptName(), this.getDescriptName(cannonicalName))
  }

  override def getDescription(mode:Int): String = {
    val cannonicalName = "dough"
    return this.getDescriptName(cannonicalName)
  }

}

class Bread extends Food {
  this.name = "bread"

  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new FlourProp())

  var color = "white"

  override def getReferents(): Set[String] = {
    val cannonicalName = this.color + " bread"
    Set("bread", this.name, this.getDescriptName(), this.getDescriptName(cannonicalName))
  }

  override def getDescription(mode:Int): String = {
    val cannonicalName = "bread"
    return this.getDescriptName(cannonicalName)
  }

}

class Jam extends Food {
  this.name = "jam"

  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new JamProp())

  var color = "red"

  override def getReferents(): Set[String] = {
    val cannonicalName = this.color + " jam"
    Set("jam", this.name, this.getDescriptName(), this.getDescriptName(cannonicalName))
  }

  override def getDescription(mode:Int): String = {
    val cannonicalName = this.color + " jam"
    return this.getDescriptName(cannonicalName)
  }

}

class Sandwhich(sandwhichType:String) extends Food {
  this.name = sandwhichType + " sandwhich"

  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new FlourProp())

  override def getReferents(): Set[String] = {
    val cannonicalName = this.sandwhichType + " sandwhich"
    Set("sandwhich", this.name, this.getDescriptName(), this.getDescriptName(cannonicalName))
  }

  override def getDescription(mode:Int): String = {
    val cannonicalName = this.sandwhichType + " sandwhich"
    return this.getDescriptName(cannonicalName)
  }

}


class Peanut extends Food {
  this.name = "peanut"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new NutProp())

  override def getReferents(): Set[String] = {
    Set("peanut", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    val cannonicalName = "peanut"
    return this.getDescriptName(cannonicalName)
  }

}

class Cachew extends Food {
  this.name = "cachew"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new NutProp())

  override def getReferents(): Set[String] = {
    Set("cachew", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    val cannonicalName = "cachew"
    return this.getDescriptName(cannonicalName)
  }

}

class Almond extends Food {
  this.name = "almond"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new NutProp())

  override def getReferents(): Set[String] = {
    Set("almond", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    val cannonicalName = "almond"
    return this.getDescriptName(cannonicalName)
  }

}

class MixedNuts extends Food {
  this.name = "mixed nuts"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new NutProp())

  override def getReferents(): Set[String] = {
    Set("mixed nuts", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return this.getDescriptName("mixed nuts")
  }

}

class FruitSalad extends Food {
  this.name = "fruit salad"
  this.propEdibility = Some(new Edible())
  this.propMaterial = Some(new PlantMatterProp())

  override def getReferents(): Set[String] = {
    Set("fruit salad", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return this.getDescriptName("fruit salad")
  }

}