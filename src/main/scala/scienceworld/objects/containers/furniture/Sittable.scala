package scienceworld.objects.containers.furniture

import scienceworld.objects.containers.Container
import scienceworld.properties.{CottonClothProp, IsOpenUnclosableContainer, WoodProp}
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._

import scala.util.Random

class Sittable extends Container {

  this.name = "sittable"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new WoodProp())

  override def getReferents(): Set[String] = {
    Set("sittable", this.propMaterial.get.substanceName + " sittable", this.name)
  }


  override def getDescription(mode: Int): String = {
    val os = new StringBuilder

    os.append("a " + this.name + ". ")

    os.append("On the " + this.name + " is: ")
    if (this.getContainedObjects().size > 0) {
      os.append(this.getContainedObjectsAndPortals().map(_.getDescription(mode = MODE_CURSORY_DETAIL)).mkString(", "))
    } else {
      os.append("nothing")
    }
    os.append(".")

    os.toString
  }

}



class Chair extends Sittable {
  this.name = "chair"
  this.propMaterial = Some(new WoodProp())
}


class Couch extends Sittable {
  this.name = "couch"
  this.propMaterial = Some(new CottonClothProp())

  val pillow = new Pillow()
  this.addObject(pillow)
}


class Bed extends Sittable {
  this.name = "bed"
  this.propMaterial = Some(new WoodProp())

  val mattress = new Mattress()
  this.addObject(mattress)

  val pillow = new Pillow()
  mattress.addObject(pillow)
}


class Mattress extends Sittable {
  this.name = "mattress"
  this.propMaterial = Some(new CottonClothProp)
}

class Pillow extends EnvObject {
  this.name = "pillow"
  this.propMaterial = Some(new CottonClothProp)

  override def getDescription(mode: Int): String = {
    return "a " + this.propMaterial.get.color + " pillow"
  }

}