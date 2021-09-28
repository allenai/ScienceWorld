package scienceworld.struct

import scienceworld.Properties.{ContainerProperties, DeviceProperties, EdibilityProperties, HeatSourceProperties, MaterialProperties, PortalProperties}
import scienceworld.processes.HeatTransfer
import util.UniqueIdentifier

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


class EnvObject(var name:String, var objType:String) {

  // Alternate constructors
  def this() = this(name = "", objType = "")

  // Contained objects
  private val containedObjects = mutable.Set[EnvObject]()
  private var inContainer:Option[EnvObject] = None

  // Unique identifier
  val uuid = UniqueIdentifier.getNextID()

  // Properties
  var propMaterial:Option[MaterialProperties] = None
  var propEdibility:Option[EdibilityProperties] = None
  var propContainer:Option[ContainerProperties] = None
  var propDevice:Option[DeviceProperties] = None
  var propHeatSource:Option[HeatSourceProperties] = None
  var propPortal:Option[PortalProperties] = None


  /*
   * Object containment
   */

  def getContainer():Option[EnvObject] = this.inContainer

  def getContainedObjects():Set[EnvObject] = this.containedObjects.toSet

  // Add an object to this container
  def addObject(objIn:EnvObject): Unit = {
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
   * Helpers
   */
  def getName():String = this.name

  def setName(strIn:String) { this.name = strIn }

  def getType():String = this.objType

  def setType(strIn:String) { this.objType = strIn }


  /*
   * Text-based simulation methods
   */
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


    // Run tick for all objects further down in the object tree
    for (containedObj <- this.getContainedObjects()) {
      containedObj.tick()
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

  def getDescription():String = {
    return "An object, called " + this.name + ", of type " + this.objType
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

}
