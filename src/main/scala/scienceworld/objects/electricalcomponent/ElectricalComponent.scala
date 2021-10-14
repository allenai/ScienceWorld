package scienceworld.objects.electricalcomponent

import scienceworld.objects.electricalcomponent.ElectricalComponent.{ROLE_VOLTAGE_GENERATOR, ROLE_VOLTAGE_SWITCH, ROLE_VOLTAGE_USER, VOLTAGE_GENERATOR, VOLTAGE_GROUND}
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


  // Given one terminal, get the other (connected) terminal.
  def getOtherTerminal(terminalIn:EnvObject):Option[Terminal] = {
    if (terminalIn == anode) return Some(cathode)
    if (terminalIn == cathode) return Some(anode)

    // Otherwise
    return None
  }

  override def tick():Boolean = {
    println ("TICK: " + this.name)

    // If this is an electrical component, check to see if it should be activated
    if (electricalRole == ROLE_VOLTAGE_USER) {
      this.propDevice.get.isActivated = false

      // Check to see if the ground is connected
      if (this.anode.connectsToGround() && this.cathode.connectsToVoltage()) {
        // Ground is connected, voltage is connected
        this.anode.voltage = Some(VOLTAGE_GENERATOR)
        this.propDevice.get.isActivated = true
      }
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
  val ROLE_VOLTAGE_SWITCH       =   3

  val VOLTAGE_GROUND            =   0.0
  val VOLTAGE_GENERATOR         =   9.0

}


/*
 * Terminal
 */
class Terminal(val parentObject:EnvObject) extends EnvObject {
  this.name = "terminal"

  propMoveable = Some( new MoveableProperties(isMovable = false) )                        // Not moveable
  propElectricalConnection = Some( new ElectricalConnectionProperties() )                 // Electrical connection point

  var voltage:Option[Double] = None

  // Does this terminal connect to a voltage source?
  def connectsToVoltage():Boolean = {
    // For each connectected object
    for (obj <- propElectricalConnection.get.getConnections()) {
      obj match {
        case co:Terminal => {
          if ((co.voltage.isDefined) && (co.voltage.get == VOLTAGE_GENERATOR)) return true
        }
        case _ => {
          println("#### OTHER")
        }
      }
    }

    return false
  }


  // Check to see if this terminal (ultimately) connects to ground
  def connectsToGround(maxSteps:Int = 10):Boolean = {
    println(" * connectsToGround(" + this.name + " / " + this.parentObject.name + " / " + maxSteps + "):")

    // For each connectected object
    for (obj <- propElectricalConnection.get.getConnections()) {
      println ("\tconnected to object: " + obj.toStringMinimal())
      obj match {
        case co:Terminal => {
          val parentObject = co.parentObject
          println ("\t\tparent object: " + parentObject.toStringMinimal())
          parentObject match {
            case po:PolarizedElectricalComponent => {
              println("\t\tPolarized")
              // Step 1: Find the parent object to see if it's a generator, and if this is connected to ground (if so, return true)
              if (po.isInstanceOf[Battery]) {
                println ("\t\tAppears to be connected to battery: " + co.name + " " + co.voltage)
                if ((co.voltage.isDefined) && (co.voltage.get == VOLTAGE_GROUND)) {
                  // Connected to ground
                  println("true1")
                  return true
                } else {
                  // Not connected to ground -- and, stop traversal through the battery
                  println("false1")
                  return false
                }
              }

              // Step 2: If the parent object isn't a generator, traverse through the object, IF the two terminals are "connected" (i.e. a light bulb, a switch that's open, etc).
              val otherTerminal = po.getOtherTerminal(co)
              if (otherTerminal.isEmpty) {
                println("false2")
                return false
              }         // Other terminal doesn't exist or is not connected in a switch, return false
              // Other terminal exists, traverse/recurse
              if ( otherTerminal.get.connectsToGround(maxSteps-1) == true) {
                println("true2")
                return true
              }      // If the recursive case returns true, then that pin connects to ground.  If it doesn't, continue on other connections.

            }
            case _ => {
              // Other non-electrical component object
              print("### OTHER")
            }
          }


        }

        case _ => {
          // Other object
          print("### OTHER")
        }
      }

    }

    println("false (default)")
    false
  }

  override def tick():Boolean = {

    super.tick()
  }

  override def getReferents():Set[String] = {
    Set("terminal", this.name)
  }

  override def getDescription(mode: Int): String = {
    if (mode == MODE_CURSORY_DETAIL) {
      return "a " + this.name + " on " + parentObject.name
    } else if (mode == MODE_DETAILED) {
      return "a " + this.name + ".  it is connected to: " + this.propElectricalConnection.get.getConnectedToStr() + ". "
    }

    // Default return
    return "a " + this.name + " on " + parentObject.name
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

/*
 * Switches
 */
class Switch extends PolarizedElectricalComponent {
  this.name = "switch"
  this.electricalRole = ROLE_VOLTAGE_SWITCH
  this.propDevice = Some( new IsActivableDeviceOff() )

  // Given one terminal, get the other (connected) terminal.
  override def getOtherTerminal(terminalIn:EnvObject):Option[Terminal] = {
    // If the switch is deactivated, do not allow any flow
    if (!this.propDevice.get.isActivated) return None

    if (terminalIn == anode) return Some(cathode)
    if (terminalIn == cathode) return Some(anode)

    // Otherwise
    return None
  }


  override def tick():Boolean = {
    println ("TICK: " + this.name)

    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("component", this.name)
  }

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder

    os.append("a " + this.name + ", which is ")
    //os.append("its anode is connected to: " + this.anode.propElectricalConnection.get.getConnectedToStr() + ". ")
    //os.append("its cathode is connected to: " + this.cathode.propElectricalConnection.get.getConnectedToStr() + ". ")

    if (this.propDevice.get.isActivated) { os.append("on") } else { os.append("off") }

    os.toString
  }

}

/*
 *  Generators
 */

class Battery extends PolarizedElectricalComponent {
  this.name = "battery"

  this.propDevice = Some(new IsNotActivableDeviceOn())

  this.electricalRole = ROLE_VOLTAGE_GENERATOR  // Component uses voltage, rather than generating it
  this.forwardVoltage = 0.0                     // The battery does not use any voltage

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