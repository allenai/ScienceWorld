package scienceworld.objects.livingthing

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



