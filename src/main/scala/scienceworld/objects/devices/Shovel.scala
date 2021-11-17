package scienceworld.objects.devices

import scienceworld.processes.Shovelling
import scienceworld.properties.{GlassProp, IsContainer, IsNotContainer, IsOpenUnclosableContainer, IsUsable, IsUsableNonActivable, MetalProp, MoveableProperties}
import scienceworld.struct.EnvObject


class Shovel extends Device {
  this.name = "shovel"

  this.propMaterial = Some(new MetalProp())
  this.propContainer = Some( new IsNotContainer() )
  this.propDevice = Some(new IsUsableNonActivable())
  this.propMoveable = Some(new MoveableProperties(isMovable = true))


  override def useWith(patientObj:EnvObject):(Boolean, String) = {
    return Shovelling.doShovel(patientObj)
  }

  override def getReferents(): Set[String] = {
    Set("shovel", this.name)
  }

  override def getDescription(mode:Int): String = {
    val os = new StringBuilder

    os.append("a shovel")

    os.toString
  }

}

