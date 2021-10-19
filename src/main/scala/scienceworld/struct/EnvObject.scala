package scienceworld.struct

import scienceworld.objects.portal.Portal
import scienceworld.properties.{ContainerProperties, CoolingSourceProperties, DeviceProperties, EdibilityProperties, ElectricalConnectionProperties, HeatSourceProperties, LifeProperties, MaterialProperties, MoveableProperties, PortalProperties}
import scienceworld.processes.{ElectricalConductivity, HeatTransfer, StateOfMatter}
import util.UniqueIdentifier

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import EnvObject._
import scienceworld.objects.electricalcomponent.ElectricalComponent.ROLE_VOLTAGE_USER
import scienceworld.objects.electricalcomponent.Terminal

import scala.reflect.ClassTag


class EnvObject(var name:String, var objType:String, includeElectricalTerminals:Boolean = true) {

  // Alternate constructors
  def this() = this(name = "", objType = "")

  // Contained objects
  private val containedObjects = mutable.Set[EnvObject]()
  private var inContainer:Option[EnvObject] = None

  // Portals
  private val portals = mutable.Set[Portal]()

  // Is this object visible, or a faux/hidden object?
  private var _isHidden:Boolean = false

  // Each (potentially) electrical component has two terminals
  val terminal1:Option[Terminal] = if (includeElectricalTerminals) { Some( new Terminal(this, "terminal 1") ) } else { None }
  val terminal2:Option[Terminal] = if (includeElectricalTerminals) { Some( new Terminal(this, "terminal 2") ) } else { None }
  if (includeElectricalTerminals) {
    // Terminals on normal objects are 'faux'/hidden
    terminal1.get.setHidden(true)
    terminal2.get.setHidden(true)

    this.addObject(terminal1.get)
    this.addObject(terminal2.get)
  }
  // Electrical role (generator, or consumer)
  var electricalRole = ROLE_VOLTAGE_USER


  // Unique identifier
  val uuid = UniqueIdentifier.getNextID()

  // Properties
  var propMaterial:Option[MaterialProperties] = None
  var propEdibility:Option[EdibilityProperties] = None
  var propContainer:Option[ContainerProperties] = None
  var propDevice:Option[DeviceProperties] = None
  var propHeatSource:Option[HeatSourceProperties] = None
  var propCoolingSource:Option[CoolingSourceProperties] = None
  var propPortal:Option[PortalProperties] = None
  var propMoveable:Option[MoveableProperties] = Some( new MoveableProperties(isMovable = true) )
  var propElectricalConnection:Option[ElectricalConnectionProperties] = None
  var propLife:Option[LifeProperties] = None


  /*
   * Portals
   */
  def addPortal(portalIn:Portal): Unit = {
    this.portals.add(portalIn)
  }

  def removePortal(portalIn:Portal):Boolean = {
    if (!this.portals.contains(portalIn)) return false
    this.portals.remove(portalIn)
  }

  def getPortals():Set[Portal] = this.portals.toSet

  // Get both portals and objects
  def getContainedObjectsAndPortals():Set[EnvObject] = (this.containedObjects ++ this.portals).toSet

  /*
   * Object containment
   */

  def getContainer():Option[EnvObject] = this.inContainer

  def getContainedObjects():Set[EnvObject] = this.containedObjects.toSet

  def getContainedObjectsOfType[T:ClassTag]():Set[EnvObject] = {
    val out = mutable.Set[EnvObject]()
    for (obj <- this.containedObjects) {
      obj match {
        case x:T => out.add(obj)
        case _ => {}
      }
    }
    return out.toSet
  }

  // Add an object to this container
  def addObject(objIn:EnvObject): Unit = {
    if (objIn == this) return // throw new RuntimeException("ERROR: addObject(): Attempted to add self to container (" + this.name + ").")

    objIn.removeAndResetContainer()    // Remove from it's previous container
    this.containedObjects.add(objIn)
    objIn.inContainer = Some(this)
  }

  // Remove an object from this container
  def removeObjectFromContainer(objIn:EnvObject):Boolean = {
    // Check that object is contained in this container -- if not, return an error
    if (!containedObjects.contains(objIn)) return false

    // If we reach here, the object is contained in this container.
    containedObjects.remove(objIn)    // Remove from this container
    objIn.removeAndResetContainer()            // Update the object's back reference to show that it has no container
    return true
  }

  // Removes *this* object from it's container
  def removeAndResetContainer(): Unit = {
    // If this object is in some container, remove it from the container
    if (this.inContainer.isDefined) {
      this.inContainer.get.removeObjectFromContainer(this)
    }
    // Update this object's back reference to show that it has no container
    this.inContainer = None
  }

  // Check to see if an object is in this container
  def contains(objIn:EnvObject): Boolean = {
    if (this.containedObjects.contains(objIn)) return true
    // Otherwise
    return false
  }

  def equals(that:EnvObject):Boolean = {
    if (this.uuid == that.uuid) return true
    // Otherwise
    return false
  }

  /*
   * Contains (recursive)
   */
  def containsRecursive(objIn:EnvObject):Boolean = {
    if (this.contains(objIn)) return true
    if (this.getContainer().isDefined) {
      // If this object is in a container, then continue searching by recursing down through the containers
      return this.containsRecursive(objIn)
    } else {
      // If this object is not in a container, then we've hit the bottom of the object tree, and this container isn't in it
      return false
    }
  }

  /*
   * Helpers
   */
  def getName():String = this.name

  def setName(strIn:String) { this.name = strIn }

  def getType():String = this.objType

  def setType(strIn:String) { this.objType = strIn }

  // Visibility
  def isHidden():Boolean = this._isHidden
  def setHidden(value:Boolean) { this._isHidden = value }


  /*
   * Delete (remove) object from simulation
   */
  def delete(): Unit = {
    // Disconnect electrically (if connected)
    this.disconnectElectricalTerminals()

    // Remove from containers
    this.removeAndResetContainer()

    // Append special deleted flag, to notice the object when debugging if it happens to be left floating around in the simulation
    this.name += " (deleted)"
  }


  /*
   * Simulation methods (electrical conductivity)
   */
  def isElectricallyConnected():Boolean = {
    if (terminal1.isDefined && terminal1.get.propElectricalConnection.get.size() > 0) return true
    if (terminal2.isDefined && terminal2.get.propElectricalConnection.get.size() > 0) return true
    return false
  }

  def hasUnpolarizedElectricalTerminals():Boolean = {
    if (terminal1.isDefined && terminal2.isDefined) return true
    // Otherwise
    return false
  }

  // Given one terminal, get the other (connected) terminal.
  def getOtherElectricalTerminal(terminalIn:EnvObject):Option[Terminal] = {
    if ((terminal1.isEmpty) || (terminal2.isEmpty)) return None

    if (terminalIn == terminal1.get) return terminal2
    if (terminalIn == terminal2.get) return terminal1

    // Otherwise
    return None
  }

  def getUnconnectedElectricalTerminal():Option[Terminal] = {
    if ((terminal1.isEmpty) || (terminal2.isEmpty)) return None

    if (terminal1.get.propElectricalConnection.get.size() == 0) return terminal1
    if (terminal2.get.propElectricalConnection.get.size() == 0) return terminal2

    // Otherwise
    return None
  }

  def disconnectElectricalTerminals() {
    if (terminal1.isDefined) this.terminal1.get.disconnect()
    if (terminal2.isDefined) this.terminal2.get.disconnect()
  }



  /*
   * Text-based simulation methods
   */
  def useWith(patientObj:EnvObject):(Boolean, String) = {
    return (false, "")
  }

  def tick():Boolean = {
    // Heat transfer: Conductive heat transfer between this container and all objects in the container (container to obj)
    for (containedObj <- this.getContainedObjects()) {
      HeatTransfer.heatTransferTouchingObjects(this, containedObj)
    }

    // Heat transfer: Conductive heat transfer between all objects in this container (obj to obj)
    val containedObjs = this.getContainedObjects().toArray
    for (i <- 0 until containedObjs.length) {
      for (j <- 0 until i) {
        if (i != j) {
          HeatTransfer.heatTransferTouchingObjects(containedObjs(i), containedObjs(j) )
        }
      }
    }

    // State of matter: Change state of matter based on temperature
    StateOfMatter.ChangeOfState(this)

    // Electrical conductivity: Potentially conduct electricity, if an electrical conductor and connected to other conductors
    ElectricalConductivity.unpolarizedElectricalConductivityTick(this, activateDeviceIfPowered = false)


    // Run tick for all objects further down in the object tree
    for (containedObj <- this.getContainedObjects()) {
      containedObj.tick()
    }
    for (portalObj <- this.getPortals()) {      //## TODO: Verify that the portal tick was run only once?
      portalObj.tick()
    }


    // Return
    true
  }

  def getReferents():Set[String] = {
    val out = mutable.Set[String]()
    out.add("object")
    out.add(this.name)
    // Return
    out.toSet
  }

  // Enumerates referents with their container (e.g. water becomes water, water in pot) to allow for disambiguation
  def getReferentsWithContainers(perspectiveContainer:EnvObject):Set[String] = {
    val out = mutable.Set[String]()
    var referents = this match {
      case x:Portal => x.getReferents(perspectiveContainer)
      case _ => this.getReferents()
    }

    for (ref <- referents) {
      out.add(ref.toLowerCase)
      val container = this.getContainer()
      if (container.isDefined) {
        for (containerRef <- container.get.getReferents()) {
          out.add( (ref + " in " + containerRef).toLowerCase )
          out.add( (ref + " on " + containerRef).toLowerCase )
        }
      }
    }

    out.toSet
  }

  def getDescription(mode:Int = MODE_CURSORY_DETAIL):String = {
    return "An object, called " + this.name + ", of type " + this.objType
  }

  // If the object is hidden, returns None
  def getDescriptionSafe(mode:Int = MODE_CURSORY_DETAIL):Option[String] = {
    if (this.isHidden()) return None
    Some(this.getDescription(mode))
  }

  /*
   * Operators
   */
  override def equals(that:Any):Boolean = {
    that match {
      case that:EnvObject => {
        if (this.uuid == that.uuid) {
          return true
        } else {
          return false
        }
      }
      case _ => return false
    }
  }


  /*
   * String methods
   */
  def toStringIncludingContents(indent:Int = 0):String = {
    val os = new StringBuilder

    // This object
    os.append( ("\t" * indent) + this.toString() + "\n")
    // Recurse through containing objects
    for (obj <- this.getContainedObjects()) {
      os.append( obj.toStringIncludingContents(indent+1))
    }

    os.toString()
  }

  def toStringMinimal():String = {
    val os = new StringBuilder
    val displayProps = Array("name", "type")

    os.append("ObjectID (" + uuid + ", name: " + this.name + ", type: " + this.objType + ")")

    os.toString()
  }

  override def toString(): String = {
    val os = new mutable.StringBuilder()

    os.append("ObjectID (" + uuid + ", name: " + this.name + ", type: " + this.objType + ")")

    // Object Contents
    os.append("), Contents (")
    val contentNames = new ArrayBuffer[String]()
    for (obj <- this.containedObjects) {
      contentNames.append(obj.getName() + "/" + obj.getType())
    }
    os.append( contentNames.mkString(", ") )

    os.append(")")

    // Return
    os.toString()
  }

}


object EnvObject {
  val MODE_CURSORY_DETAIL  =   0
  val MODE_DETAILED        =   1
}
