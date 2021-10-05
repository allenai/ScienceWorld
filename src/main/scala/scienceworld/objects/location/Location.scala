package scienceworld.objects.location

import scienceworld.objects.Air
import scienceworld.properties.{ContainerProperties, IsContainer, IsOpenUnclosableContainer, MoveableProperties}
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._


class Location extends EnvObject {
  this.name = "location"

  propMoveable = Some( new MoveableProperties(isMovable = false) )
  propContainer = Some( new IsOpenUnclosableContainer() )

  override def getReferents(): Set[String] = {
    Set("location", this.name)
  }

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder()

    os.append("A location. ")
    os.append("In this location, you see: ")
    os.append( this.getContainedObjectsAndPortals().map(_.getDescription()).mkString(", ") )
    os.append(".")

    os.toString()
  }

}


// Object tree root
class Universe extends Location {
  this.name = "universe"

  override def getReferents(): Set[String] = {
    Set("universe")
  }

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder()

    os.append("A Universe. ")
    os.append("In this Universe, you see: ")
    os.append( this.getContainedObjectsAndPortals().map(_.getDescription()).mkString(", ") )
    os.append(".")

    os.toString()
  }
}


// One room (i.e. in a house/other structure)
class Room(_name:String) extends Location {
  def this() = this(_name = "room")

  this.name = _name

  // Add air
  val air = new Air()
  air.propMaterial.get.temperatureC = 25.0f
  this.addObject( air )


  override def getReferents(): Set[String] = {
    Set("room", this.name)
  }

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder()

    os.append("This room is called the " + this.name + ". ")
    os.append("In it, you see: \n")
    for (containedObj <- this.getContainedObjects()) {
      os.append( "\t" + containedObj.getDescription() + "\n")
    }

    os.append("You also see:\n")
    for (portalObj <- this.getPortals()) {
      os.append("\t" + portalObj.getDescription(mode = MODE_CURSORY_DETAIL, perspectiveContainer = this) + "\n")
    }

    os.toString()
  }
}
