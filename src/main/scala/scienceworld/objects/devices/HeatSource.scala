package scienceworld.objects.devices

import scienceworld.properties.{HeatSourceProperties, HeatSourcePropertiesOven, HeatSourcePropertiesStove, IsContainer, IsOpenUnclosableContainer, MoveableProperties}
import scienceworld.processes.HeatTransfer

class HeatSource extends Device {
  this.name = "heat source"

  this.propHeatSource = Some(new HeatSourcePropertiesStove())

  override def tick():Boolean = {

    // Step 1: Heat transfer between this heat source and all contained objects
    for (containedObj <- this.getContainedObjects()) {
      HeatTransfer.heatTransferHeatSource(this, containedObj)
    }

    // Step 2: Tick
    super.tick()
  }

}




class Stove extends HeatSource {
  this.name = "stove"

  this.propHeatSource = Some(new HeatSourcePropertiesStove)
  this.propContainer = Some( new IsOpenUnclosableContainer() )
  this.propMoveable = Some(new MoveableProperties(isMovable = false))

  override def tick():Boolean = {
    // If it's activated, then set max temp.  If deactivated, set temp to off.
    if (this.propDevice.isDefined) {
      if (this.propDevice.get.isActivated) {
        this.propHeatSource.get.setOnMax()
      } else {
        this.propHeatSource.get.setOff()
      }
    }

    super.tick()
  }

  override def getReferents():Set[String] = {
    Set("stove", this.name)
  }

  override def getDescription():String = {
    val os = new StringBuilder

    os.append("a " + this.name + ", which is turned ")
    if (this.propDevice.get.isActivated) { os.append("on") } else { os.append("off") }
    os.append(". ")

    os.append("On the stove is: ")
    os.append( this.getContainedObjects().map(_.getDescription()).mkString(", ") )
    os.append(".")

    os.toString()
  }

}

class HotPlate extends HeatSource {
  this.name = "hot plate"

  this.propHeatSource = Some(new HeatSourcePropertiesStove)
  this.propContainer = Some( new IsOpenUnclosableContainer() )
  this.propMoveable = Some(new MoveableProperties(isMovable = false))

  override def tick():Boolean = {
    // If it's activated, then set max temp.  If deactivated, set temp to off.
    if (this.propDevice.isDefined) {
      if (this.propDevice.get.isActivated) {
        this.propHeatSource.get.setOnMax()
      } else {
        this.propHeatSource.get.setOff()
      }
    }

    super.tick()
  }

  override def getReferents():Set[String] = {
    Set("hot plate", this.name)
  }

  override def getDescription():String = {
    val os = new StringBuilder

    os.append("a " + this.name + ", which is turned ")
    if (this.propDevice.get.isActivated) { os.append("on") } else { os.append("off") }
    os.append(". ")

    os.append("On the hot plate is: ")
    os.append( this.getContainedObjects().map(_.getDescription()).mkString(", ") )
    os.append(".")

    os.toString()
  }

}

class Oven extends HeatSource {
  this.name = "oven"

  this.propHeatSource = Some(new HeatSourcePropertiesOven)
  this.propContainer = Some( new IsContainer() )
  this.propMoveable = Some(new MoveableProperties(isMovable = false))

  override def tick():Boolean = {
    // If it's activated, then set max temp.  If deactivated, set temp to off.
    if (this.propDevice.isDefined) {
      if (this.propDevice.get.isActivated) {
        this.propHeatSource.get.setOnMax()
      } else {
        this.propHeatSource.get.setOff()
      }
    }

    super.tick()
  }

  override def getReferents():Set[String] = {
    Set("oven", this.name)
  }

  override def getDescription():String = {
    val os = new StringBuilder

    os.append("a " + this.name + ", which is turned ")
    if (this.propDevice.get.isActivated) { os.append("on") } else { os.append("off") }
    os.append(". ")

    os.append("In the oven is: ")
    os.append( this.getContainedObjects().map(_.getDescription()).mkString(", ") )
    os.append(".")

    os.toString()
  }

}