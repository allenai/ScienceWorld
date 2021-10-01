package scienceworld.objects.devices

import scienceworld.objects.Water
import scienceworld.properties.{IsActivableDeviceOff, IsOpenUnclosableContainer, MoveableProperties, SteelProp}
import scienceworld.struct.EnvObject

class Device extends EnvObject {
  this.name = "device"

  this.propDevice = Some(new IsActivableDeviceOff())

  override def getDescription(): String = {
    val os = new StringBuilder

    os.append("a " + this.name + ", which is turned ")
    if (this.propDevice.get.isActivated) { os.append("on") } else { os.append("off") }

    os.toString
  }

}


class Sink extends Device {
  this.name = "sink"

  this.propMaterial = Some(new SteelProp())
  this.propContainer = Some( new IsOpenUnclosableContainer() )
  this.propDevice = Some(new IsActivableDeviceOff())
  this.propMoveable = Some(new MoveableProperties(isMovable = false))

  override def tick():Boolean = {
    // If sink is activated, put water into containers inside it
    if (this.propDevice.get.isActivated) {

      // Step 1: Put water into any containers inside the sink
      for (obj <- this.getContainedObjects()) {
        if ((obj.propContainer.isDefined) && (obj.propContainer.get.isContainer)) {
          // The object is a container -- check to see if it's open
          if (obj.propContainer.get.isOpen) {
            // The object is open -- check to see if it already contains water
            val existingWater = obj.getContainedObjectsOfType[Water]()
            if (existingWater.size == 0) {
              // Add new water
              obj.addObject( new Water() )
            }
          }

        }
      }

      // TODO: Make objects wet?
      // TODO: Objects that react with water? (e.g. paper?)

    }

    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("sink", this.name)
  }

  override def getDescription(): String = {
    val os = new StringBuilder

    os.append("a sink, which is turned ")
    if (this.propDevice.get.isActivated) { os.append("on") } else { os.append("off") }
    os.append(". ")
    os.append("In the sink is: ")
    if (this.getContainedObjects().size > 0) {
      os.append(this.getContainedObjects().map(_.getDescription()).mkString(", "))
    } else {
      os.append("nothing")
    }
    os.append(".")


    os.toString
  }
}

