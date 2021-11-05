package scienceworld.objects

import scienceworld.properties.{AirProp, CaesiumProp, GalliumProp, LeadProp, MercuryProp, RubberProp, SoapProp, TinProp, WaterProp}
import scienceworld.struct.EnvObject

class Substance extends EnvObject {
  this.name = "substance"

  override def getReferents(): Set[String] = {
    Set("substance", this.name)
  }

  override def getDescription(mode:Int): String = {
    return "a substance called " + this.name
  }
}


class Air extends Substance {
  this.name = "air"

  this.propMaterial = Some(new AirProp())

}


class Water extends Substance {
  this.name = "water"

  this.propMaterial = Some(new WaterProp())

}

class Ice extends Substance {
  this.name = this.propMaterial.get.nameInStateOfMatter("liquid")
  this.propMaterial = Some(new WaterProp())
  this.propMaterial.get.temperatureC = -10.0f
}


// Metals
class Lead extends Substance {
  this.name = "lead"
  this.propMaterial = Some(new LeadProp())
}

class Tin extends Substance {
  this.name = "tin"
  this.propMaterial = Some(new TinProp())
}

class Mercury extends Substance {
  this.name = "mercury"
  this.propMaterial = Some(new MercuryProp())
}

class Gallium extends Substance {
  this.name = "gallium"
  this.propMaterial = Some(new GalliumProp())
}

class Caesium extends Substance {
  this.name = "caesium"
  this.propMaterial = Some(new CaesiumProp)
}

// Other
class Soap extends Substance {
  this.name = "soap"
  this.propMaterial = Some(new SoapProp)
}

class Rubber extends Substance {
  this.name = "rubber"
  this.propMaterial = Some (new RubberProp)
}