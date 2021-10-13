package scienceworld.objects.electricalcomponent

import scienceworld.objects.electricalcomponent.ElectricalComponent.{ROLE_VOLTAGE_GENERATOR, ROLE_VOLTAGE_USER}
import scienceworld.properties.{ElectricalConnectionProperties, IsActivableDeviceOff, IsNotActivableDeviceOff, IsNotActivableDeviceOn, MoveableProperties}
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._

//## TODO: Also add UnpolarizedElectricalComponent

class PolarizedElectricalComponent extends EnvObject {
  this.name = "component"

  this.propDevice = Some(new IsNotActivableDeviceOff())                       // By default, not activable, and is off
  this.propMoveable = Some( new MoveableProperties(isMovable = false) )       // Not moveable

  // Each electrical component has an anode and a cathode
  val anode = new Anode(this)
  val cathode = new Cathode(this)
  this.addObject(anode)
  this.addObject(cathode)

  // Default forward voltage (the voltage that this component uses)
  var forwardVoltage:Double = 0.0f

  // Electrical role (generator, or consumer)
  var electricalRole = ROLE_VOLTAGE_USER
  // If a voltage generator, how much voltage does it generate?
  var generatorVoltage:Double = 0.0

  // Check if a complete circuit
  var circuitCheckLabel:Option[Int] = None

  def isContinuousCircuit(labelIdx:Int = 0):Boolean = {
    // This circuit node
    circuitCheckLabel = Some(labelIdx)
    // Progress along path towards cathode
    val cathodeConnections = this.cathode.propElectricalConnection.get.getConnections().toArray
    println("isContinuousCircuit(): " + this.name )
    println("cathodeConnections: " + cathodeConnections.length)
    if (cathodeConnections.size == 0) return false

    val cathodeConnection = cathodeConnections(0)


    cathodeConnection match {
      case t:Terminal => {
        t.parentObject match {
          case x:PolarizedElectricalComponent => {
            println (this.name + " connects to " + x.name)
            println (x.name + ".circuitCheckLabel: " + x.circuitCheckLabel)
            if (x.circuitCheckLabel.isEmpty) {
              // Recurse case
              println ("\tRecurse: " + x.name + " " + labelIdx + 1)
              val result = x.isContinuousCircuit (labelIdx + 1)
              this.circuitCheckLabel = None
              return result
            } else {
              println ("\tIs populated: " + x.circuitCheckLabel)
              if (x.circuitCheckLabel.get == 0) {
                // Complete circuit
                this.circuitCheckLabel = None
                return true
              } else {
                // Partial circuit
                this.circuitCheckLabel = None
                return false
              }
            }
          }
          case _ => {
            println("Unknown (" + cathodeConnection.name + ")" )
            this.circuitCheckLabel = None
            return false
          }
        }
      }
      case _ => {
        println("Unknown (" + cathodeConnection.name + ")" )
        // Unknown component
        this.circuitCheckLabel = None
        return false
      }
    }

  }


  def getAnodeVoltage():Option[Double] = {
    if (electricalRole == ROLE_VOLTAGE_GENERATOR) {
      // If this is a generator, return ground
      return Some(0.0f)
    } else {
      // If this is a user, then travel along the chain until we find the source voltage, and then subtract each component's forward voltage from it.

      // Step 1: Sum the input voltages on the anode
      val anodeConnections = this.anode.propElectricalConnection.get.getConnections()
      if (anodeConnections.size == 0) return None      // No-connection case, return None
      var inputVoltage:Double = 0.0f
      for (obj <- anodeConnections) {
        obj match {
          case x:Terminal => {
            val av = x.voltage
            println ("* anode: " + this.name + " connected to " + x.parentObject.name + " : " + av)
            if (av.isEmpty) return None               // Case: no connection
            inputVoltage += av.get                    // Case: valid connection
          }
          case _ => { }
        }
      }
      // Step 2: Then, subtract the forward voltage
      return Some(inputVoltage)
    }
  }

  def getCathodeVoltage():Option[Double] = {
    if (electricalRole == ROLE_VOLTAGE_GENERATOR) {
      // If this is a generator, return it's generation voltage
      return Some(this.generatorVoltage)
    } else {
      // If this is a user, then travel along the chain until we find the source voltage, and then subtract each component's forward voltage from it.
      // Step 1: Sum the input voltages on the cathode
      val cathodeConnections = this.cathode.propElectricalConnection.get.getConnections()
      if (cathodeConnections.size == 0) return None     // No-connection case, return None
      var inputVoltage:Double = 0.0f
      for (obj <- cathodeConnections) {
        obj match {
          case x:Terminal => {
            val cv = x.voltage
            println ("* cathode: " + this.name + " connected to " + x.parentObject.name + " : " + cv)
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
    anode.voltage = anodeVoltage
    val cathodeVoltage = this.getCathodeVoltage()
    cathode.voltage = cathodeVoltage

    println(" * ElectricalComponent (" + this.name + "): Anode Voltage: " + anodeVoltage + "  Cathode Voltage: " + cathodeVoltage)

    if (anodeVoltage.isEmpty) return 0.0f
    if (cathodeVoltage.isEmpty) return 0.0f

    val potentialDifference = anodeVoltage.get - cathodeVoltage.get
    return potentialDifference
  }

  override def tick():Boolean = {
    // If this is an electrical component, check to see if it should be activated
    val potentialDifference = this.calculatePotentialDifference()
    println(" * ElectricalComponent (" + this.name + "): Potential difference: " + potentialDifference)
    println("\t isContinuousCircuit(): " + this.isContinuousCircuit() )
    println("----------------")

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
 * Terminal
 */
class Terminal(val parentObject:EnvObject) extends EnvObject {
  this.name = "terminal"

  propMoveable = Some( new MoveableProperties(isMovable = false) )                        // Not moveable
  propElectricalConnection = Some( new ElectricalConnectionProperties() )                 // Electrical connection point

  var voltage:Option[Double] = None


  override def tick():Boolean = {

    super.tick()
  }

  override def getReferents():Set[String] = {
    Set("terminal", this.name)
  }

  override def getDescription(mode: Int): String = {
    return "a " + this.name + ".  it is connected to: " + this.propElectricalConnection.get.getConnectedToStr() + ". "
  }
}

/*
 * Anode
 */
class Anode(parentObject:EnvObject) extends Terminal(parentObject) {
  this.name = "anode"

}

/*
 * Cathode
 */
class Cathode(parentObject:EnvObject) extends Terminal(parentObject) {
  this.name = "cathode"

}


/*
 * Other components
 */

class LightBulb extends PolarizedElectricalComponent {
  this.name = "light bulb"

  this.propDevice = Some(new IsNotActivableDeviceOff())

  this.electricalRole = ROLE_VOLTAGE_USER     // Component uses voltage, rather than generating it
  this.forwardVoltage = 2.0                   // Forward voltage required to function


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


class Battery extends PolarizedElectricalComponent {
  this.name = "battery"

  this.propDevice = Some(new IsNotActivableDeviceOn())

  this.electricalRole = ROLE_VOLTAGE_GENERATOR  // Component uses voltage, rather than generating it
  this.forwardVoltage = 0.0                     // The battery does not use any voltage
  this.generatorVoltage = 9.0                   // This battery generates 9V

  override def tick(): Boolean = {
    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("battery", this.name)
  }

  override def getDescription(mode:Int):String = {
    val os = new StringBuilder

    os.append("a " + this.name + "")
    if (mode == MODE_DETAILED) {
      os.append(". ")
      os.append("its anode is connected to: " + this.anode.propElectricalConnection.get.getConnectedToStr() + ". ")
      os.append("its cathode is connected to: " + this.cathode.propElectricalConnection.get.getConnectedToStr() + ". ")
    }

    os.toString
  }

}