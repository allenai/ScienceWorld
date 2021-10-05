package scienceworld.objects.portal

import scienceworld.properties.{IsContainer, MoveableProperties, PortalProperties}
import scienceworld.struct.EnvObject


class Portal (val connectsFrom:EnvObject, val connectsTo:EnvObject) extends EnvObject {

  // Get where the container connects to
  def getConnectsTo(perspectiveContainer:EnvObject):Option[EnvObject] = {
    if (perspectiveContainer == connectsFrom) return Some(connectsTo)
    if (perspectiveContainer == connectsTo) return Some(connectsFrom)
    // Otherwise
    return None
  }

  def getReferents(perspectiveContainer:EnvObject): Set[String] = {
    super.getReferents()
  }

  def getDescription(mode: Int, perspectiveContainer:EnvObject): String = {
    super.getDescription(mode)
  }
}


class Door(val isOpen:Boolean, connectsFrom:EnvObject, connectsTo:EnvObject) extends Portal(connectsFrom, connectsTo) {

  def this(connectsFrom:EnvObject, connectsTo:EnvObject) = this(isOpen=false, connectsFrom, connectsTo)

  this.name = "door"

  propPortal = Some( new PortalProperties(isOpen=isOpen, connectsFrom=connectsFrom, connectsTo=connectsTo) )
  propMoveable = Some( new MoveableProperties(isMovable = false) )


  override def getReferents():Set[String] = {
    Set("ERROR: SHOULD USE OVERRIDE FOR PORTAL.")
  }

  override def getReferents(perspectiveContainer:EnvObject): Set[String] = {
    if (perspectiveContainer == connectsFrom) {
      return Set("door", "door to " + connectsTo.name, connectsTo.name + " door")
    } else if (perspectiveContainer == connectsTo) {
      return Set("door", "door to " + connectsFrom.name, connectsFrom.name + " door")
    }

    // Catch all
    return Set("door", "door from " + connectsFrom.name + " to " + connectsTo.name, "door from " + connectsTo.name + " to " + connectsFrom.name)
  }


  override def getDescription(mode: Int): String = {
    return "ERROR: SHOULD USE OVERRIDE FOR PORTAL."
  }
  override def getDescription(mode:Int, perspectiveContainer:EnvObject): String = {
    val os = new StringBuilder

    if (perspectiveContainer == connectsFrom) {
      os.append("A door to the " + connectsTo.name)
    } else if (perspectiveContainer == connectsTo) {
      os.append("A door to the " + connectsFrom.name)
    } else {
      os.append("A door that connects the " + connectsFrom.name + " to the " + connectsTo.name)
    }

    // Return
    os.toString
  }
}

