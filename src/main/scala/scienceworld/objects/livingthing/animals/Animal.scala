package scienceworld.objects.livingthing.animals

import scienceworld.objects.livingthing.LivingThing
import scienceworld.processes.lifestage.{AnimalLifeStage}
import scienceworld.properties.{LifePropertiesAnimal}
import scienceworld.struct.EnvObject._
import util.StringHelpers

class Animal extends LivingThing {
  this.name = "animal"

  propLife = Some(new LifePropertiesAnimal())

  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )

  override def tick():Boolean = {
    lifecycle.get.tick()

    /*
    var sickly:Boolean = false

    // Check to see if it's out of temperature range -- if so, it dies.
    val temperatureC = this.propMaterial.get.temperatureC
    if ((temperatureC >= this.propLife.get.maxTemp) || (temperatureC <= this.propLife.get.minTemp)) {
      this.propLife.get.durationOutOfTemperatureBounds += 1
      sickly = true
      if (this.propLife.get.durationOutOfTemperatureBounds >= this.propLife.get.maxDurationOutOfTemperatureBounds) {
        lifecycle.get.changeStage(AnimalLifeStage.ANIMAL_STAGE_DEATH)
      }
    } else {
      this.propLife.get.durationOutOfTemperatureBounds = 0
    }



    // Set illness status
    this.propLife.get.isSickly = sickly

    */

    super.tick()
  }

  override def getReferents(): Set[String] = {
    super.getReferents()
  }

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder

    // If dead, simplify the name
    if (propLife.get.isDead) {
      os.append("a dead " + this.getDescriptName())
      return os.toString()
    }

    // If alive, give a verbose name
    os.append("a " + lifecycle.get.getCurStageName() + " " +  this.getDescriptName())
    if (propLife.get.isSickly) os.append(" (that looks unwell)")

    /*
    val cObjs = this.getContainedObjectsNotHidden()
    if (cObjs.size > 0) {
      os.append(". ")
      os.append("On it you see: " + StringHelpers.objectListToStringDescription(cObjs, this, mode = MODE_CURSORY_DETAIL, multiline = false) + ". ")
    }
     */

    if (mode == MODE_DETAILED) {
      // Extended detail
    }

    os.toString
  }
}
