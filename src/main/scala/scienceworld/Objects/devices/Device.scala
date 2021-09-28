package scienceworld.Objects.devices

import scienceworld.Properties.{IsActivableDevice, IsContainer, IsOpenUnclosableContainer, MoveableProperties, SteelProp}
import scienceworld.struct.EnvObject

class Device extends EnvObject {
  this.name = "device"

  this.propDevice = Some(new IsActivableDevice())

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
  this.propDevice = Some(new IsActivableDevice())
  this.propMoveable = Some(new MoveableProperties(isMovable = false))


  override def getReferents(): Set[String] = {
    Set("sink", this.name)
  }

  override def getDescription(): String = {
    val os = new StringBuilder

    os.append("a sink, which is turned ")
    if (this.propDevice.get.isActivated) { os.append("on") } else { os.append("off") }

    os.toString
  }
}

