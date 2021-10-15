package scienceworld.objects.livingthing

import scienceworld.objects.electricalcomponent.ElectricalComponent.VOLTAGE_GENERATOR
import scienceworld.processes.lifestage.PlantLifeStages
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

  // Life cycle
  val lifecycle = PlantLifeStages.mkPlantLifeCycle(this)


  override def tick():Boolean = {
    // Life cycle tick
    println ("### TICK!")
    lifecycle.tick()


    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("living thing", "organism", this.name)
  }

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder

    os.append("a " + this.name + " in the " + lifecycle.getCurStageName() + " stage. ")
    if (mode == MODE_DETAILED) {
      // Extended detail
    }

    os.toString
  }

}
