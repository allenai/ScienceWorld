package scienceworld.objects.livingthing

import scienceworld.objects.electricalcomponent.ElectricalComponent.VOLTAGE_GENERATOR
import scienceworld.processes.lifestage.PlantLifeStages
import scienceworld.properties.{LifeProperties, LifePropertiesPlant, PlantMatterProp}
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._

class LivingThing extends EnvObject {
  this.name = "living thing"

  override def tick():Boolean = {
    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("living thing", "organism", this.name)
  }

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder

    os.append("a " + this.name + ". ")
    if (mode == MODE_DETAILED) {
      // Extended detail
    }

    os.toString
  }

}



class Plant extends LivingThing {
  this.name = "plant"

  // This is alive, and has a certain temperature range/etc
  this.propLife = Some(new LifePropertiesPlant())
  this.propMaterial = Some(new PlantMatterProp())

  // Life cycle
  val lifecycle = PlantLifeStages.mkPlantLifeCycle(this)


  override def tick():Boolean = {
    // Life cycle tick
    println ("### TICK!")
    lifecycle.tick()


    super.tick()
  }

  override def getReferents(): Set[String] = {
    val out = Set("living thing", "organism", this.name, this.name + " in the " + lifecycle.getCurStageName() + " stage", lifecycle.getCurStageName() + " plant")

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
    os.append("a " + this.name + " in the " + lifecycle.getCurStageName() + " stage")
    if (propLife.get.isSickly) os.append(" (that looks unwell)")

    if (mode == MODE_DETAILED) {
      // Extended detail
    }

    os.toString
  }

}
