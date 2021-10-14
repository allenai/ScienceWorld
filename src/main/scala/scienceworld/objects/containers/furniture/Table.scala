package scienceworld.objects.containers.furniture

import scienceworld.objects.containers.Container
import scienceworld.properties.{IsContainer, IsOpenUnclosableContainer, WoodProp}
import scienceworld.struct.EnvObject._


/*
 * Tables/Desks/etc
 */
class Table extends Container {
  this.name = "table"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new WoodProp())

  override def getReferents(): Set[String] = {
    Set("table", this.propMaterial.get.substanceName + " table", this.name)
  }


  override def getDescription(mode: Int): String = {
    val os = new StringBuilder

    os.append("a " + this.name + ". ")

    if (mode == MODE_CURSORY_DETAIL) {
      os.append("On the " + this.name + " is: ")
      if (this.getContainedObjects().size > 0) {
        os.append(this.getContainedObjectsAndPortals().map(_.getDescription()).mkString(", "))
      } else {
        os.append("nothing")
      }
      os.append(".")
    } else if (mode == MODE_DETAILED) {
      os.append("On the " + this.name + " is: \n")
      if (this.getContainedObjects().size > 0) {
        for (cObj <- this.getContainedObjectsAndPortals()) {
          os.append("\t" + cObj.getDescription(mode = MODE_DETAILED) + "\n")
        }
      } else {
        os.append("nothing.")
      }
    }

    os.toString
  }

}


class Desk extends Container {
  this.name = "desk"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new WoodProp())

  // Create a drawer
  val drawer = new Drawer()
  this.addObject(drawer)

  override def getReferents(): Set[String] = {
    Set("desk", this.propMaterial.get.substanceName + " desk", this.name)
  }


  override def getDescription(mode: Int): String = {
    val os = new StringBuilder

    os.append("a " + this.name + ". ")

    if (mode == MODE_CURSORY_DETAIL) {
      os.append("On the " + this.name + " is: ")
      if (this.getContainedObjects().size > 0) {
        os.append(this.getContainedObjectsAndPortals().map(_.getDescription()).mkString(", "))
      } else {
        os.append("nothing")
      }
      os.append(".")
    } else if (mode == MODE_DETAILED) {
      os.append("On the " + this.name + " is: \n")
      if (this.getContainedObjects().size > 0) {
        for (cObj <- this.getContainedObjectsAndPortals()) {
          os.append("\t" + cObj.getDescription(mode = MODE_DETAILED) + "\n")
        }
      } else {
        os.append("nothing.")
      }
    }

    os.toString
  }

}

class Counter extends Container {
  this.name = "counter"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new WoodProp())

  // Create a drawer
  val drawer = new Drawer()
  this.addObject(drawer)

  override def getReferents(): Set[String] = {
    Set("counter", this.propMaterial.get.substanceName + " counter", this.name)
  }


  override def getDescription(mode: Int): String = {
    val os = new StringBuilder

    os.append("a " + this.name + ". ")

    if (mode == MODE_CURSORY_DETAIL) {
      os.append("On the " + this.name + " is: ")
      if (this.getContainedObjects().size > 0) {
        os.append(this.getContainedObjectsAndPortals().map(_.getDescription()).mkString(", "))
      } else {
        os.append("nothing")
      }
      os.append(".")
    } else if (mode == MODE_DETAILED) {
      os.append("On the " + this.name + " is: \n")
      if (this.getContainedObjects().size > 0) {
        for (cObj <- this.getContainedObjectsAndPortals()) {
          os.append("\t" + cObj.getDescription(mode = MODE_DETAILED) + "\n")
        }
      } else {
        os.append("nothing.")
      }
    }

    os.toString
  }

}


class Cupboard extends Container {
  this.name = "cupboard"
  this.propContainer = Some(new IsContainer())
  this.propMaterial = Some(new WoodProp())

  // Create a drawer
  val drawer = new Drawer()
  this.addObject(drawer)

  override def getReferents(): Set[String] = {
    Set("cupboard", this.propMaterial.get.substanceName + " desk", this.name)
  }


  override def getDescription(mode: Int): String = {
    val os = new StringBuilder

    os.append("a " + this.name + ". ")

    os.append("The " + this.name + " door is ")
    if (this.propContainer.get.isOpen) {
      os.append("open. ")
    } else {
      os.append("closed. ")
    }

    if (this.propContainer.get.isOpen) {
      val contents = this.getContainedObjectsAndPortals().map(_.getDescription())
      if (mode == MODE_CURSORY_DETAIL) {
        os.append("In the " + this.name + " is: ")
        if (contents.size == 0) {
          os.append("nothing")
        } else {
          os.append(contents.mkString(", "))
        }
        os.append(".")
      } else if (mode == MODE_DETAILED) {
        os.append("In the " + this.name + " is: \n")
        if (this.getContainedObjects().size > 0) {
          for (cObj <- this.getContainedObjectsAndPortals()) {
            os.append("\t" + cObj.getDescription(mode = MODE_DETAILED) + "\n")
          }
        } else {
          os.append("nothing.")
        }
      }
    }

    os.toString
  }

}


class Closet extends Container {
  this.name = "closet"
  this.propContainer = Some(new IsContainer())
  this.propMaterial = Some(new WoodProp())


  override def getReferents(): Set[String] = {
    Set("closet", this.propMaterial.get.substanceName + " closet", this.name)
  }


  override def getDescription(mode: Int): String = {
    val os = new StringBuilder

    os.append("a " + this.name + ". ")

    os.append("The " + this.name + " door is ")
    if (this.propContainer.get.isOpen) {
      os.append("open. ")
    } else {
      os.append("closed. ")
    }

    if (this.propContainer.get.isOpen) {
      val contents = this.getContainedObjectsAndPortals().map(_.getDescription())
      if (mode == MODE_CURSORY_DETAIL) {
        os.append("In the " + this.name + " is: ")
        if (contents.size == 0) {
          os.append("nothing")
        } else {
          os.append(contents.mkString(", "))
        }
        os.append(".")
      } else if (mode == MODE_DETAILED) {
        os.append("In the " + this.name + " is: \n")
        if (this.getContainedObjects().size > 0) {
          for (cObj <- this.getContainedObjectsAndPortals()) {
            os.append("\t" + cObj.getDescription(mode = MODE_DETAILED) + "\n")
          }
        } else {
          os.append("nothing.")
        }
      }
    }

    os.toString
  }

}

/*
 * Drawer
 */

class Drawer extends Container {
  this.name = "drawer"
  this.propContainer = Some(new IsContainer())
  this.propMaterial = Some(new WoodProp())

  override def getReferents(): Set[String] = {
    Set("drawer", this.propMaterial.get.substanceName + " table", this.name)
  }


  override def getDescription(mode: Int): String = {
    val os = new StringBuilder

    os.append("a " + this.name)

    if (mode == MODE_DETAILED) {
      os.append(". ")
      if (!this.propContainer.get.isOpen) {
        os.append(" The drawer is closed.")
      } else {
        val contents = this.getContainedObjectsAndPortals().map(_.getDescription())
        if (mode == MODE_CURSORY_DETAIL) {
          os.append("In the " + this.name + " is: ")
          if (contents.size == 0) {
            os.append("nothing")
          } else {
            os.append(contents.mkString(", "))
          }
          os.append(".")
        } else if (mode == MODE_DETAILED) {
          os.append("In the " + this.name + " is: \n")
          if (this.getContainedObjects().size > 0) {
            for (cObj <- this.getContainedObjectsAndPortals()) {
              os.append("\t" + cObj.getDescription(mode = MODE_DETAILED) + "\n")
            }
          } else {
            os.append("nothing.")
          }
        }
      }
    }

    os.toString
  }

}