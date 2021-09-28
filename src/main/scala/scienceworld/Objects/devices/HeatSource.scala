package scienceworld.Objects.devices

import scienceworld.Properties.{HeatSourceProperties, HeatSourcePropertiesStove}
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