package scienceworld.objects.electricalcomponent

import scienceworld.objects.electricalcomponent.ElectricalComponent.{ROLE_VOLTAGE_GENERATOR, ROLE_VOLTAGE_USER}
import scienceworld.properties.{ElectricalConnectionProperties, IsActivableDeviceOff, IsNotActivableDeviceOff, MoveableProperties}
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._

class ElectricalComponent extends EnvObject {
  this.name = "component"

  this.propDevice = Some(new IsNotActivableDeviceOff())                       // By default, not activable, and is off
  this.propMoveable = Some( new MoveableProperties(isMovable = false) )       // Not moveable

  // Each electrical component has an anode and a cathode
  val anode = new Anode()
  val cathode = new Cathode()
  this.addObject(anode)
  this.addObject(cathode)

  // Default forward voltage (the voltage that this component uses)
  var forwardVoltage:Double = 0.0f

  // Electrical role (generator, or consumer)
  var electricalRole = ROLE_VOLTAGE_USER
  // If a voltage generator, how much voltage does it generate?
  var generatorVoltage:Double = 0.0

  def getAnodeVoltage():Option[Double] = {
    if (electricalRole == ROLE_VOLTAGE_GENERATOR) {
      // If this is a generator, return it's generation voltage
      return Some(this.generatorVoltage)
    } else {
      // If this is a user, then travel along the chain until we find the source voltage, and then subtract each component's forward voltage from it.

      // Step 1: Sum the input voltages on the anode
      val anodeConnections = this.anode.propElectricalConnection.get.getConnections()
      if (anodeConnections.size == 0) return None      // No-connection case, return None
      var inputVoltage:Double = 0.0f
      for (obj <- anodeConnections) {
        obj match {
          case x:ElectricalComponent => {
            val av = x.getAnodeVoltage()
            if (av.isEmpty) return None               // Case: no connection
            inputVoltage += av.get                    // Case: valid connection
          }
          case _ => { }
        }
      }
      // Step 2: Then, subtract the forward voltage
      return Some(inputVoltage - this.forwardVoltage)
    }
  }

  def getCathodeVoltage():Option[Double] = {
    if (electricalRole == ROLE_VOLTAGE_GENERATOR) {
      // If this is a generator, return ground
      return Some(0.0f)
    } else {
      // If this is a user, then travel along the chain until we find the source voltage, and then subtract each component's forward voltage from it.

      // Step 1: Sum the input voltages on the cathode
      val cathodeConnections = this.cathode.propElectricalConnection.get.getConnections()
      if (cathodeConnections.size == 0) return None     // No-connection case, return None
      var inputVoltage:Double = 0.0f
      for (obj <- cathodeConnections) {
        obj match {
          case x:ElectricalComponent => {
            val cv = x.getCathodeVoltage()
            if (cv.isEmpty) return None               // Case: no connection
            inputVoltage += cv.get                    // Case: valid connection
          }
          case _ => { }
        }
      }

      return Some(inputVoltage)
    }
  }

  // Calculate the potential difference
  // Reminder: Cathode is the input, Anode is the output
  def calculatePotentialDifference():Double = {
    val anodeVoltage = this.getAnodeVoltage()
    val cathodeVoltage = this.getCathodeVoltage()

    if (anodeVoltage.isEmpty) return 0.0f
    if (cathodeVoltage.isEmpty) return 0.0f

    val potentialDifference = anodeVoltage.get - cathodeVoltage.get
    return potentialDifference
  }

  override def tick():Boolean = {
    // If this is an electrical component, check to see if it should be activated
    val potentialDifference = this.calculatePotentialDifference()
    println(" * ElectricalComponent (" + this.name + "): Potential difference: " + potentialDifference)

    if (potentialDifference >= this.forwardVoltage) {
      // Activated!
      this.propDevice.get.isActivated = true
    } else {
      // Not enough energy to activate
      this.propDevice.get.isActivated = false
    }

    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("component", this.name)
  }

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder

    os.append("a " + this.name + ". ")
    os.append("its anode is connected to: " + this.anode.propElectricalConnection.get.getConnectedToStr() + ". ")
    os.append("its cathode is connected to: " + this.cathode.propElectricalConnection.get.getConnectedToStr() + ". ")

    //if (this.propDevice.get.isActivated) { os.append("on") } else { os.append("off") }

    os.toString
  }

}

object ElectricalComponent {
  val ROLE_VOLTAGE_GENERATOR    =   1
  val ROLE_VOLTAGE_USER         =   2
}


/*
 * Anode
 */
class Anode extends EnvObject {
  this.name = "anode"

  propMoveable = Some( new MoveableProperties(isMovable = false) )                        // Not moveable
  propElectricalConnection = Some( new ElectricalConnectionProperties() )                 // Electrical connection point

  override def tick():Boolean = {

    super.tick()
  }

  override def getReferents():Set[String] = {
    Set("anode", this.name)
  }

  override def getDescription(mode: Int): String = {
    return "an anode"
  }
}

/*
 * Cathode
 */
class Cathode extends EnvObject {
  this.name = "cathode"

  propMoveable = Some( new MoveableProperties(isMovable = false) )                        // Not moveable
  propElectricalConnection = Some( new ElectricalConnectionProperties() )                 // Electrical connection point

  override def tick():Boolean = {

    super.tick()
  }

  override def getReferents():Set[String] = {
    Set("cathode", this.name)
  }

  override def getDescription(mode: Int): String = {
    return "a cathode"
  }
}


/*
 * Other components
 */

class LightBulb extends ElectricalComponent {
  this.name = "light bulb"

  this.propDevice = Some(new IsNotActivableDeviceOff())

  // Forward voltage required to function
  this.forwardVoltage = 2.0

  override def tick(): Boolean = {
    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("light", "light bulb", this.name)
  }

  override def getDescription(mode:Int):String = {
    val os = new StringBuilder

    os.append("a " + this.name + ", which is ")
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


