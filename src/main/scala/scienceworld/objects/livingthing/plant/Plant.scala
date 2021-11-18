package scienceworld.objects.livingthing.plant

import scienceworld.objects.Apple
import scienceworld.objects.devices.Stove
import scienceworld.objects.livingthing.LivingThing
import scienceworld.processes.PlantReproduction
import scienceworld.processes.lifestage.PlantLifeStages
import scienceworld.properties.{FlowerMatterProp, IsNotContainer, IsOpenUnclosableContainer, LifePropertiesPlant, PlantMatterProp, PollenMatterProp, PollinationProperties}
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._
import util.StringHelpers

import scala.util.control.Breaks._


class Plant extends LivingThing {
  this.name = "plant"
  this.objType = "plant"


  // This is alive, and has a certain temperature range/etc
  this.propLife = Some(new LifePropertiesPlant())
  this.propMaterial = Some(new PlantMatterProp())
  this.propContainer = Some(new IsOpenUnclosableContainer())

  // Life cycle
  lifecycle = Some( PlantLifeStages.mkPlantLifeCycle(this) )

  /*
   * Helpers
   */
  def isSeed():Boolean = {
    return PlantLifeStages.isSeed(this.lifecycle.get)
  }

  def getPlantType():String = {
    return this.propLife.get.lifeformType
  }


  override def tick():Boolean = {
    // Life cycle tick
    println ("### TICK!")
    lifecycle.get.tick()


    super.tick()
  }

  override def getReferents(): Set[String] = {
    var out = Set("living thing", "organism", this.name, this.name + " in the " + lifecycle.get.getCurStageName() + " stage", lifecycle.get.getCurStageName() + " plant")
    out ++= Set(this.getDescriptName(), this.getDescriptName() + " in the " + lifecycle.get.getCurStageName() + " stage")

    // If ill, append ill referents too
    if (this.propLife.get.isSickly) {
      val sicklyDesc = out.map("sick " + _)
      return out ++ sicklyDesc
    }

    // Return
    out
  }

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder

    // If dead, simplify the name
    if (propLife.get.isDead) {
      os.append("a dead " + this.name)
      return os.toString()
    }

    // If alive, give a verbose name
    os.append("a " + this.getDescriptName() + " in the " + lifecycle.get.getCurStageName() + " stage")
    if (propLife.get.isSickly) os.append(" (that looks unwell)")

    val cObjs = this.getContainedObjectsNotHidden()
    if (cObjs.size > 0) {
      os.append(". ")
      os.append("On the plant you see: " + StringHelpers.objectListToStringDescription(cObjs, this, mode = MODE_CURSORY_DETAIL, multiline = false) + ". ")
    }

    if (mode == MODE_DETAILED) {
      // Extended detail
    }

    os.toString
  }

}


/*
 * Pollen
 */
class Pollen(val parentPlant:Plant) extends EnvObject {
  this.name = "pollen"


  this.propContainer = Some(new IsNotContainer())
  this.propMaterial = Some(new PollenMatterProp())

  def getPlantType():String = this.parentPlant.getPlantType()

  override def tick():Boolean = {
    // TODO: Add genes?
    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("pollen", this.name, getPlantType() + " pollen", this.getDescriptName())
  }

  override def getDescription(mode: Int): String = {
    val os = new StringBuilder

    if (mode == MODE_CURSORY_DETAIL) {
      os.append(this.getDescriptName())
    } else if (mode == MODE_DETAILED) {
      os.append(getPlantType() + this.getDescriptName())
    }

    // Return
    os.toString()
  }

}

/*
 * Flower
 */
class Flower(parentPlant:Plant) extends EnvObject {
  this.name = "flower"

  this.propContainer = Some(new IsOpenUnclosableContainer())
  this.propMaterial = Some(new FlowerMatterProp())

  // A step refereing to where the flower is in the pollination -> fruiting process.  If the pollenation step is zero, the flower hasn't been pollinated.
  this.propPollination = Some( new PollinationProperties() )


  def pollinate(pollen:Pollen):Boolean = {
    // Step 1A: check to see if the pollen is this plant's pollen, or a different plant's pollen
    if (pollen.parentPlant.uuid == this.parentPlant.uuid) {
      // The pollen comes from this plant -- do not pollinate
      println ("#### POLLEN COMES FROM SAME PLANT")
      return false
    }

    // Step 1B: Check to see that the pollen comes from the correct plant type
    if (pollen.getPlantType() != parentPlant.getPlantType()) {
      // The pollen comes from a different plant (e.g. apple vs orange) -- do not pollinate
      println ("#### POLLEN COMES FROM DIFFERENT TYPE OF PLANT")
      return false
    }

    // If we reach here, the pollen should be valid.

    // Step 2: Consume pollen
    pollen.delete()

    println ("####* POLLENATION SUCCESSFUL")

    // Step 3: Get the flower -> fruit conversion going
    this.propPollination.get.pollinationStep = 1

    // Return
    return true
  }

  // Adds this plant's pollen to this flower, if it doesn't have any
  def addPollen(): Unit = {

    // Step 1: Check if some of this plant's pollen already exists in this flower
    for (cObj <- this.getContainedObjects()) {
      cObj match {
        case p:Pollen => {
          if (p.parentPlant.uuid == this.parentPlant.uuid) {
            // Some of this plant's pollen is already in this flower
            return
          }
        }
        case _ => { }
      }
    }

    // Step 2: If we reach here, we need to add pollen
    val pollen = new Pollen(parentPlant = this.parentPlant)
    this.addObject(pollen)

  }


  /*
   * Regular functions
   */

  override def tick():Boolean = {
    // Flower tick

    // If currently pollinated, continue the pollination process
    if (this.propPollination.get.pollinationStep > 0) {
      // Increment step
      this.propPollination.get.pollinationStep += 1

      if (this.propPollination.get.pollinationStep > this.propPollination.get.stepsUntilFruitingBodyForms) {
        // TODO: Change into fruit
        if (this.getContainer().isDefined) {
          println("FRUIT MADE")
          // Create appropriate fruit
          val fruit = PlantReproduction.createFruit(this.parentPlant.getPlantType())
          if (fruit.isDefined) {
            this.getContainer().get.addObject( fruit.get )
          }

          // Delete flower
          this.delete(expelContents = true)
        }

      }

    } else {
      // If not pollenated:
      // Step 2A: Add pollen of this plant, if needed
      this.addPollen()

      // Step 2B: Check if any of the things in the flower contain (valid) pollen -- if so, start the pollination process.
      breakable {
        for (cObj <- this.getContainedObjects()) { // For every object in the flower
          cObj match {
            case p: Pollen => {
              // Check to see if this is valid pollen, and if so, begin the pollination process
              if (this.pollinate(p) == true) break()
            }
            case obj: EnvObject => {
              // Check objects contained in the flower (e.g. bees) to see if they contain pollen
              for (ccObj <- obj.getContainedObjects()) {
                ccObj match {
                  case p: Pollen => {
                    // Check to see if this is valid pollen, and if so, begin the pollination process
                    if (this.pollinate(p) == true) break()
                  }
                  case _ => { }
                }
              }
            }
          }
        }

      }
    }

    super.tick()
  }


  override def getReferents(): Set[String] = {

    // If flower is pollinated, then it begins to wilt
    if (this.propPollination.get.pollinationStep > 0) {
      return Set("flower", this.name, "wilting flower", "wilting " + this.name, this.getDescriptName(), "wilting " + this.getDescriptName())
    }

    // Normal (non-wilted) flower
    return Set("flower", this.name, this.getDescriptName())
  }


  override def getDescription(mode:Int): String = {
    val os = new StringBuilder

    // If flower is pollinated, then it begins to wilt
    if (this.propPollination.get.pollinationStep > 0) {
      os.append("a wilting " + this.getDescriptName())
    } else {
      // Normal (non-wilted) flower
      os.append("a " + this.getDescriptName())
    }

    if (mode == MODE_DETAILED) {
      // Extended detail
      val cObjs = this.getContainedObjectsNotHidden()
      os.append(". ")
      os.append("Inside the flower is: " + StringHelpers.objectListToStringDescription(cObjs, this, mode = MODE_CURSORY_DETAIL, multiline = false))
    }

    os.toString
  }
}