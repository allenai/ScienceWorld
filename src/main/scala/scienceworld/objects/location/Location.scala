package scienceworld.objects.location

import scienceworld.objects.Air
import scienceworld.properties.{ContainerProperties, IsContainer, IsOpenUnclosableContainer, MoveableProperties}
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._
import util.StringHelpers


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
    os.append( StringHelpers.objectListToStringDescription(this.getContainedObjectsAndPortals(), perspectiveContainer=this, multiline = true)  )
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
    os.append( StringHelpers.objectListToStringDescription(this.getContainedObjectsAndPortals(), perspectiveContainer=this, multiline = true)  )
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
    os.append( StringHelpers.objectListToStringDescription(this.getContainedObjects(), perspectiveContainer=this, multiline = true)  )

    os.append("You also see:\n")
    os.append( StringHelpers.portalListToStringDescription(this.getPortals(), perspectiveContainer=this, multiline = true)  )

    os.toString()
  }
}
