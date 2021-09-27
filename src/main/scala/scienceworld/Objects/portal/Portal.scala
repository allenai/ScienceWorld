package scienceworld.Objects.portal

import scienceworld.Properties.PortalProperties
import scienceworld.struct.EnvObject


trait Portal extends EnvObject {

}


class Door(isOpen:Boolean, connectsFrom:EnvObject, connectsTo:EnvObject) extends Portal {

  def this(connectsFrom:EnvObject, connectsTo:EnvObject) = this(isOpen=false, connectsFrom, connectsTo)

  this.name = "door"

  val portalProp = new PortalProperties(isOpen=isOpen, connectsFrom=connectsFrom, connectsTo=connectsTo)

  override def getReferents(): Set[String] = {
    Set("door", "door to " + connectsTo.name, connectsTo.name + " door")
  }

  override def getDescription(): String = {
    val os = new StringBuilder

    os.append("A door to the " + connectsTo.name )

    // Return
    os.toString
  }
}

