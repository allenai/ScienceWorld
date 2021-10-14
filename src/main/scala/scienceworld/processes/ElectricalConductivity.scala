package scienceworld.processes

import scienceworld.objects.electricalcomponent.ElectricalComponent.VOLTAGE_GENERATOR
import scienceworld.struct.EnvObject
import scienceworld.objects.electricalcomponent.ElectricalComponent._
import scienceworld.objects.electricalcomponent.{PolarizedElectricalComponent, UnpolarizedElectricalComponent}



object ElectricalConductivity {


  def unpolarizedElectricalConductivityTick(obj:EnvObject, activateDeviceIfPowered:Boolean = false): Unit = {

    // Only continue if this is an electrical component with unpolarized terminals
    if (!obj.hasUnpolarizedElectricalTerminals()) return

    // Check if this device can be powered/serve as an electrical conductor
    var isPossibleConductor:Boolean = false
    if (obj.isInstanceOf[PolarizedElectricalComponent] || obj.isInstanceOf[UnpolarizedElectricalComponent]) {
      // Electrical components are conductors
      isPossibleConductor = true
    } else if ((obj.propMaterial.isDefined) && (obj.propMaterial.get.electricallyConductive)) {
      // Materials that are electrically conductive are conductors
      if (obj.name.contains("fork")) println("##### " + obj.name + " IS AN ELECTRICAL CONDUCTOR!")

      isPossibleConductor = true
    }

    // If not a conductor, then clear terminals
    if (!isPossibleConductor) {
      obj.terminal1.get.voltage = None
      obj.terminal2.get.voltage = None
      return
    }

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
