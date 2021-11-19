package scienceworld.objects.electricalcomponent

import scienceworld.objects.electricalcomponent.ElectricalComponent.ROLE_VOLTAGE_USER
import scienceworld.properties.IsNotActivableDeviceOff
import scienceworld.struct.EnvObject._

/*
 * Light Bulb
 */

class LightBulb(val color:String = "") extends PolarizedElectricalComponent {
  this.name = (color + " light bulb").trim

  this.propDevice = Some(new IsNotActivableDeviceOff())

  this.electricalRole = ROLE_VOLTAGE_USER     // Component uses voltage, rather than generating it

  override def tick(): Boolean = {
    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("light", "light bulb", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int):String = {
    val os = new StringBuilder

    os.append("a " + this.getDescriptName() + ", which is ")
    if (this.propDevice.get.isActivated) {
      os.append("on")
    } else {
      os.append("off")
    }

    if (mode == MODE_DETAILED) {
      os.append(". ")
      os.append("its anode is connected to: " + this.anode.propElectricalConnection.get.getConnectedToStr() + ". ")
      os.append("its cathode is connected to: " + this.cathode.propElectricalConnection.get.getConnectedToStr() + ". ")
    }

    os.toString
  }

}
