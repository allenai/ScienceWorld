package scienceworld.objects.electricalcomponent

import scienceworld.objects.electricalcomponent.ElectricalComponent.{ROLE_VOLTAGE_GENERATOR, VOLTAGE_GENERATOR, VOLTAGE_GROUND}
import scienceworld.properties.{IsActivableDeviceOn, IsNotActivableDeviceOn}

/*
 *  Generators
 */

class Generator extends PolarizedElectricalComponent {
  this.name = "generator"

  this.propDevice = Some(new IsActivableDeviceOn())
  this.electricalRole = ROLE_VOLTAGE_GENERATOR  // Component uses voltage, rather than generating it

  override def tick(): Boolean = {
    // If this generator is activated, then generate a voltage potential at the terminals.
    if (this.propDevice.get.isActivated) {
      this.anode.voltage = Some(VOLTAGE_GENERATOR)
      this.cathode.voltage = Some(VOLTAGE_GROUND)
    } else {
      this.anode.voltage = None
      this.cathode.voltage = None
    }
    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("generator", this.name)
  }

  override def getDescription(mode:Int):String = {
    val os = new StringBuilder

    os.append("a " + this.name + "")
    //if (mode == MODE_DETAILED) {
    os.append(". ")
    os.append("its anode is connected to: " + this.anode.propElectricalConnection.get.getConnectedToStr() + ". ")
    os.append("its cathode is connected to: " + this.cathode.propElectricalConnection.get.getConnectedToStr() + ". ")
    //}

    os.toString
  }

}

class Battery extends Generator {
  this.name = "battery"

  this.propDevice = Some(new IsNotActivableDeviceOn())      // Always on

}