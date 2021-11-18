package scienceworld.objects

import scienceworld.properties.{AirProp, AshProp, CaesiumProp, GalliumProp, LeadProp, MercuryProp, RubberProp, SaltWaterProp, SoapProp, SodiumChlorideProp, SodiumProp, TinProp, WaterProp, WoodProp}
import scienceworld.struct.EnvObject

class Substance extends EnvObject {
  this.name = "substance"

  override def getReferents(): Set[String] = {
    Set("substance", this.name, this.getDescriptName())
  }

  override def getDescription(mode:Int): String = {
    return "a substance called " + this.getDescriptName()
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
  this.propMaterial = Some(new WaterProp())
  this.propMaterial.get.temperatureC = -10.0f

  this.name = this.propMaterial.get.nameInStateOfMatter("liquid")
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

class Wood extends Substance {
  this.name = "wood"
  this.propMaterial = Some (new WoodProp)
}

// Combustion products
class Ash extends Substance {
  this.name = "ash"
  this.propMaterial = Some (new AshProp)
}


// Substances
class Sodium extends Substance {
  this.name = "sodium"
  this.propMaterial = Some( new SodiumProp )
}

class SodiumChloride extends Substance {
  this.name = "sodium chloride"
  this.propMaterial = Some( new SodiumChlorideProp )
}

class SaltWater extends Substance {
  this.name = "salt water"
  this.propMaterial = Some( new SaltWaterProp )
}