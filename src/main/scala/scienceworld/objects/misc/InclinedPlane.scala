package scienceworld.objects.misc

import scienceworld.properties.{IsContainer, IsOpenUnclosableContainer, SteelProp}
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject.MODE_CURSORY_DETAIL
import util.StringHelpers

import scala.collection.mutable


class InclinedPlane (val angleDeg:Double = 45.0f) extends EnvObject {
  this.name = "inclined plane"
  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new SteelProp())

  // Inclined plane variables
  val planeRun = 1.0f                                                     // 1 meter run
  val planeRise = planeRun * math.tan( math.toRadians(this.angleDeg) )    // Calculate the rise given the run and angle
  val planeLength = math.sqrt( math.pow(planeRise, 2) + math.pow(planeRun, 2) )

  // Keep track of the location of each object
  lazy val normalizedLocations = mutable.Map[Long, Double]()     // (uuid, position 0-1)

  /*
   * Inclined plane mechanics
   */
  def setObjectToTopOfPlane(obj:EnvObject): Unit = {
    this.normalizedLocations(obj.uuid) = 1.0f
  }

  def getObjectPositionOnPlane(obj:EnvObject):Option[Double] = {
    if (!normalizedLocations.contains(obj.uuid)) return None
    return Some(normalizedLocations(obj.uuid))
  }

  def slideObjectDownPlane(obj:EnvObject): Unit = {
    val planeFriction:Double = 0.50f

    // Check that the object is on the plane
    if (!normalizedLocations.contains(obj.uuid)) return

    // Change sliding behavior based on state of matter (if defined)
    if (obj.propMaterial.isDefined) {
      if (obj.propMaterial.get.stateOfMatter == "liquid") {
        // Liquid -- move to the bottom of the ramp.
        normalizedLocations(obj.uuid) = 0.0
        return
      } else if (obj.propMaterial.get.stateOfMatter == "gas") {
        // Gas -- move to the top of the ramp.
        normalizedLocations(obj.uuid) = 1.0
        return
      }
    }
    // Other: Solid (normal case) or undefined (assume solid)

    // Check that object isn't already at the bottom of the plane
    if (normalizedLocations(obj.uuid) <= 0) return

    // Calculate 'speed'
    //val g = 9.81f
    val g = 0.1f
    val forceDown = g * math.sin( math.toRadians(this.angleDeg) )
    // Pretend that speed is constant instead of accelerating

    val oldPosition = normalizedLocations(obj.uuid)
    val delta = forceDown
    val newPosition = math.max(oldPosition - delta, 0.0f)     // Calculate new position.  If less than zero, set to zero
    normalizedLocations(obj.uuid) = newPosition

  }


  // If an object is added to the inclined plane, it starts at the top
  override def addObject(objIn: EnvObject): Unit = {
    this.setObjectToTopOfPlane(objIn)
    super.addObject(objIn)
  }

  override def tick(): Boolean = {
    // TODO: Sliding/friction calculation
    print("## TICK (PLANE) ")
    for (cObj <- this.getContainedObjectsNotHidden()) {
      println(" -- " + cObj.name)
      this.slideObjectDownPlane(cObj)
    }

    super.tick()
  }

  /*
   * Normal functions
   */

  override def getReferents(): Set[String] = {
    Set("inclined plane", "plane", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    val containedObjects = this.getContainedObjectsAndPortals(includeHidden = false)
    if (containedObjects.size == 0) {
      return "an empty " + this.getDescriptName()
    } else {
      //return "a " + this.getDescriptName() + " (containing " + StringHelpers.objectListToStringDescription(containedObjects, this, mode=MODE_CURSORY_DETAIL, multiline = false) + ")"
      val os = new StringBuilder()
      os.append("a " + this.getDescriptName() + ", with: ")
      for (cObj <- containedObjects) {
        val objPosition = this.getObjectPositionOnPlane(cObj)
        os.append(" a " + cObj.getDescriptName())
        if (objPosition.isDefined) {
          if (objPosition.get == 0.0f) {
            os.append(" at the bottom of the plane")
          } else if (objPosition.get >= 0.99f) {
            os.append(" at the top of the plane")
          } else {
            val percentDownPlane = 100 - (100 * objPosition.get)


            os.append(" approximately " + percentDownPlane.formatted("%3.0f").trim() + "%" + " down the plane\n")
          }
        }
      }

      os.toString()
    }
  }

}