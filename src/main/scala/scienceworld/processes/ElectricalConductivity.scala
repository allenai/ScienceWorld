package scienceworld.processes

import scienceworld.objects.electricalcomponent.ElectricalComponent.VOLTAGE_GENERATOR
import scienceworld.struct.EnvObject
import scienceworld.objects.electricalcomponent.ElectricalComponent._



object ElectricalConductivity {


  def unpolarizedElectricalConductivityTick(obj:EnvObject, activateDeviceIfPowered:Boolean = false): Unit = {

    // Only continue if this is an electrical component with unpolarized terminals
    if (!obj.hasUnpolarizedElectricalTerminals()) return

    // If this is an electrical component, check to see if it should be activated
    if (obj.electricalRole == ROLE_VOLTAGE_USER) {
      if (activateDeviceIfPowered) obj.propDevice.get.isActivated = false

      // Check to see if the ground is connected to one side
      // Case 1: Ground is connected to Terminal 1, Voltage is connected to Terminal 2
      if (obj.terminal1.get.connectsToGround() && obj.terminal2.get.connectsToVoltage()) {
        println ("Terminal 1 is ground, Terminal 2 is voltage")
        obj.terminal1.get.voltage = Some(VOLTAGE_GENERATOR)
        if (activateDeviceIfPowered) obj.propDevice.get.isActivated = true
      } else if (obj.terminal2.get.connectsToGround() && obj.terminal1.get.connectsToVoltage()) {
        // Case 2: Ground is connected to Terminal 2, Voltage is connected to Terminal 1
        println ("Terminal 2 is ground, Terminal 1 is voltage")
        obj.terminal2.get.voltage = Some(VOLTAGE_GENERATOR)
        if (activateDeviceIfPowered) obj.propDevice.get.isActivated = true
      }
    }

  }

}
