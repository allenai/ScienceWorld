package scienceworld.Objects.devices

import scienceworld.Properties.{CoolingSourcePropertiesFreezer, CoolingSourcePropertiesFridge, HeatSourcePropertiesStove, IsActivableDeviceOn, IsContainer, IsOpenUnclosableContainer, MoveableProperties}
import scienceworld.processes.HeatTransfer

class CoolingSource extends Device {
  this.name = "cooling source"

  this.propCoolingSource = Some(new CoolingSourcePropertiesFridge)

  override def tick():Boolean = {

    // Step 1: Heat transfer between this heat source and all contained objects
    for (containedObj <- this.getContainedObjects()) {
      HeatTransfer.heatTransferCoolingSource(this, containedObj)
    }

    // Step 2: Tick
    super.tick()
  }

}




class Fridge extends CoolingSource {
  this.name = "fridge"

  this.propCoolingSource = Some(new CoolingSourcePropertiesFridge)
  this.propCoolingSource.get.setOnMin()
  this.propContainer = Some( new IsContainer() )
  this.propMoveable = Some(new MoveableProperties(isMovable = false))
  this.propDevice = Some(new IsActivableDeviceOn)

  override def getReferents():Set[String] = {
    Set("fridge", this.name)
  }

  override def getDescription():String = {
    val os = new StringBuilder

    os.append("a " + this.name + ", which is turned ")
    if (this.propDevice.get.isActivated) { os.append("on") } else { os.append("off") }
    os.append(". ")

    val contents = this.getContainedObjects().map(_.getDescription())
    os.append("In the fridge is: ")
    if (contents.size == 0) {
      os.append("nothing")
    } else {
      os.append(contents.mkString(", "))
    }
    os.append(".")

    os.toString()
  }

}

class Freezer extends CoolingSource {
  this.name = "freezer"

  this.propCoolingSource = Some(new CoolingSourcePropertiesFreezer)
  this.propCoolingSource.get.setOnMin()
  this.propContainer = Some( new IsContainer() )
  this.propMoveable = Some(new MoveableProperties(isMovable = false))
  this.propDevice = Some(new IsActivableDeviceOn)

  override def getReferents():Set[String] = {
    Set("freezer", this.name)
  }

  override def getDescription():String = {
    val os = new StringBuilder

    os.append("a " + this.name + ", which is turned ")
    if (this.propDevice.get.isActivated) { os.append("on") } else { os.append("off") }
    os.append(". ")

    val contents = this.getContainedObjects().map(_.getDescription())
    os.append("In the freezer is: ")
    if (contents.size == 0) {
      os.append("nothing")
    } else {
      os.append(contents.mkString(", "))
    }
    os.append(".")
    os.toString()
  }

}