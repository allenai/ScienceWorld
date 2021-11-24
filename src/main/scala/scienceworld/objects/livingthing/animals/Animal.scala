package scienceworld.objects.livingthing.animals

import scienceworld.objects.livingthing.LivingThing
import scienceworld.processes.lifestage.AnimalLifeStage
import scienceworld.properties.{LifePropertiesAnimal, LifePropertiesAnt, LifePropertiesBeaver, LifePropertiesBrownBear, LifePropertiesChameleon, LifePropertiesChipmunk, LifePropertiesCommonToad, LifePropertiesCrocodile, LifePropertiesDragonfly, LifePropertiesElephant, LifePropertiesGiantTortoise, LifePropertiesHedgehog, LifePropertiesMouse, LifePropertiesParrot, LifePropertiesRabbit, LifePropertiesWolf}
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
    Set("living thing", "organism", this.name, this.getDescriptName(), lifecycle.get.getCurStageName() + " " + this.name, lifecycle.get.getCurStageName() + " " + this.getDescriptName())
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


/*
 * Common animals
 */

class GiantTortoise extends Animal {
  this.name = "giant tortoise"
  this.propLife = Some( new LifePropertiesGiantTortoise() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class Parrot extends Animal {
  this.name = "parrot"
  this.propLife = Some( new LifePropertiesParrot() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class Elephant extends Animal {
  this.name = "elephant"
  this.propLife = Some( new LifePropertiesElephant() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class Crocodile extends Animal {
  this.name = "crocodile"
  this.propLife = Some( new LifePropertiesCrocodile() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class BrownBear extends Animal {
  this.name = "brown bear"
  this.propLife = Some( new LifePropertiesBrownBear() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}


class Beaver extends Animal {
  this.name = "beaver"
  this.propLife = Some( new LifePropertiesBeaver() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class Wolf extends Animal {
  this.name = "wolf"
  this.propLife = Some( new LifePropertiesWolf() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class Chipmunk extends Animal {
  this.name = "chipmunk"
  this.propLife = Some( new LifePropertiesChipmunk() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class Toad extends Animal {
  this.name = "common toad"
  this.propLife = Some( new LifePropertiesCommonToad() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class Rabbit extends Animal {
  this.name = "rabbit"
  this.propLife = Some( new LifePropertiesRabbit() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}


class Mouse extends Animal {
  this.name = "mouse"
  this.propLife = Some( new LifePropertiesMouse() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class Chameleon extends Animal {
  this.name = "chameleon"
  this.propLife = Some( new LifePropertiesChameleon() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class Dragonfly extends Animal {
  this.name = "dragonfly"
  this.propLife = Some( new LifePropertiesDragonfly() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class Ant extends Animal {
  this.name = "ant"
  this.propLife = Some( new LifePropertiesAnt() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}

class Hedgehog extends Animal {
  this.name = "hedgehog"
  this.propLife = Some( new LifePropertiesHedgehog() )
  lifecycle = Some( AnimalLifeStage.mkAnimalLifeCycle(this) )
}