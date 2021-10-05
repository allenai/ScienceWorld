package scienceworld.objects.portal

import scienceworld.properties.{IsContainer, MoveableProperties, PortalProperties}
import scienceworld.struct.EnvObject


class Portal (val isOpen:Boolean, val connectsFrom:EnvObject, val connectsTo:EnvObject) extends EnvObject {

  propPortal = Some( new PortalProperties(isOpen=isOpen, connectsFrom=connectsFrom, connectsTo=connectsTo) )
  propMoveable = Some( new MoveableProperties(isMovable = false) )


  // Get where the container connects to
  def getConnectsTo(perspectiveContainer:EnvObject):Option[EnvObject] = {
    if (perspectiveContainer == connectsFrom) return Some(connectsTo)
    if (perspectiveContainer == connectsTo) return Some(connectsFrom)
    // Otherwise
    return None
  }

  /*
   * Rerefents/description (from perspective of one side of the portal)
   */

  override def getReferents():Set[String] = {
    Set("ERROR: SHOULD USE OVERRIDE FOR PORTAL.")
  }

  def getReferents(perspectiveContainer:EnvObject): Set[String] = {
    if (perspectiveContainer == connectsFrom) {
      return Set(this.name, this.name + " to " + connectsTo.name, connectsTo.name + " " + this.name)
    } else if (perspectiveContainer == connectsTo) {
      return Set(this.name, this.name + " to " + connectsFrom.name, connectsFrom.name + " " + this.name)
    }

    // Catch all
    return Set(this.name, this.name + " from " + connectsFrom.name + " to " + connectsTo.name, this.name + " from " + connectsTo.name + " to " + connectsFrom.name)
  }


  override def getDescription(mode: Int): String = {
    return "ERROR: SHOULD USE OVERRIDE FOR PORTAL."
  }
  def getDescription(mode:Int, perspectiveContainer:EnvObject): String = {
    val os = new StringBuilder

    if (perspectiveContainer == connectsFrom) {
      os.append("A " + this.name + " to the " + connectsTo.name)
    } else if (perspectiveContainer == connectsTo) {
      os.append("A " + this.name + " to the " + connectsFrom.name)
    } else {
      os.append("A " + this.name + " that connects the " + connectsFrom.name + " to the " + connectsTo.name)
    }

    // Return
    os.toString
  }

}


class Door(isOpen:Boolean, connectsFrom:EnvObject, connectsTo:EnvObject) extends Portal(isOpen, connectsFrom, connectsTo) {

  def this(connectsFrom:EnvObject, connectsTo:EnvObject) = this(isOpen=false, connectsFrom, connectsTo)

  this.name = "door"

}

