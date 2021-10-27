package scienceworld.objects.taskitems

import scienceworld.objects.Substance
import scienceworld.properties.{MetalProp, WaterProp}
import scienceworld.struct.EnvObject

import scala.util.Random

/*
 * An unknown substance that is either (randomly) electrically conductive or non-conductive.
 */

class UnknownSubstanceElectricalConductivity(letterName:String = "A", isConductive:Boolean) extends EnvObject {
  this.name = "unknown substance " + letterName

  this.propMaterial = Some(new MetalProp)
  this.propMaterial.get.electricallyConductive = isConductive

  override def getReferents(): Set[String] = {
    Set(this.name, "unknown substance")
  }

  override def getDescription(mode: Int): String = {
    return this.name
  }

}


object UnknownSubstanceElectricalConductivity {

  // Make a random substance that is either electrically conductive or non-conductive
  def mkRandomSubstanceElectricalConductive():EnvObject = {
    val randDouble = Random.nextFloat()
    val randLetter = (97 + Random.nextInt(25)).toChar.toString

    if (randDouble < 0.50) {
      return new UnknownSubstanceElectricalConductivity(letterName = randLetter, isConductive = false)
    } else {
      return new UnknownSubstanceElectricalConductivity(letterName = randLetter, isConductive = true)
    }

  }

}