package scienceworld.Objects.location

import scienceworld.Objects.Air
import scienceworld.struct.EnvObject

class Location extends EnvObject {
  this.name = "location"

  override def getReferents(): Set[String] = {
    Set("location", this.name)
  }

  override def getDescription(): String = {
    val os = new StringBuilder()

    os.append("A location. ")
    os.append("In this location, you see: ")
    os.append( this.getContainedObjects().map(_.getDescription()).mkString(", ") )
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

  override def getDescription(): String = {
    val os = new StringBuilder()

    os.append("A Universe. ")
    os.append("In this Universe, you see: ")
    os.append( this.getContainedObjects().map(_.getDescription()).mkString(", ") )
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

  override def getDescription(): String = {
    val os = new StringBuilder()

    os.append("This room is called the " + this.name + ". ")
    os.append("In it, you see: \n")
    for (containedObj <- this.getContainedObjects()) {
      os.append( "\t" + containedObj.getDescription() + "\n")
    }

    os.toString()
  }
}
