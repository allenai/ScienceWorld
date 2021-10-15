package scienceworld.processes.lifestage

import scienceworld.struct.EnvObject

/*
 * Plant life stages
 */

// Seed stage
class PlantLifeStageSeed(obj:EnvObject, lifecycle:LifeCycle) extends LifeStage(PlantLifeStages.PLANT_STAGE_SEED, obj, lifecycle) {
  var ticksMeetingCriteria:Int = 0
  val stageDuration:Int = 10

  override def tick(): Unit = {
    this.incrementDuration()

    // Check to see if the container that the seed is in has water
    val (hasWater, objWater) = this.checkContainerHas("water")
    if (!hasWater) {
      println ("* Plant (" + obj.name + " / " + this.stageName + "): Criteria not met: Missing water")
      return
    }

    // Check to see if the container the seed is in has soil
    val (hasSoil, objSoil) = this.checkContainerHas("soil")
    if (!hasSoil) {
      println ("* Plant (" + obj.name + " / " + this.stageName + "): Criteria not met: Missing soil")
      return
    }

    ticksMeetingCriteria += 1
    if (ticksMeetingCriteria >= stageDuration) {
      println ("* Plant (" + obj.name + " / " + this.stageName + "): Criteria met: (ticks: " + ticksMeetingCriteria + ")")
      // Consume the water (delete it from the simulation)
      objWater.get.delete()

      // Move onto next stage
      lifecycle.changeStage(PlantLifeStages.PLANT_STAGE_SEEDLING)
    }

  }

}


// Seed stage
class PlantLifeStageSeedling(obj:EnvObject, lifecycle:LifeCycle) extends LifeStage(PlantLifeStages.PLANT_STAGE_SEEDLING, obj, lifecycle) {
  var ticksMeetingCriteria:Int = 0
  val stageDuration:Int = 10

  var durationWithoutWater:Int = 0
  val maxDurationWithoutWater:Int = 20

  var durationWithoutSoil:Int = 0
  val maxDurationWithoutSoil:Int = 5

  var durationOutOfTemperatureBounds:Int = 0
  val maxDurationOutOfTemperatureBounds:Int = 5

  override def tick(): Unit = {
    var stressed:Boolean = false
    var sickly = false
    this.incrementDuration()

    // Check to see if the container that the seed is in has water
    val (hasWater, objWater) = this.checkContainerHas("water")
    if (hasWater) {
      // In a container with water:  Consume the water
      durationWithoutWater = 0          // Reset counter that counts how long it's been without water
    } else {
      stressed = true
      // Not in a container with water: Keep track of how long the plant is dry.  If for too long, look sickly, then die.
      durationWithoutWater += 1
      if (durationWithoutWater >= (maxDurationWithoutWater/2)) {
        sickly = true
      }
      if (durationWithoutWater >= maxDurationWithoutWater) {
        lifecycle.changeStage(PlantLifeStages.PLANT_STAGE_DEATH)
      }
    }

    // Check to see if the container the seedling is in has soil
    val (hasSoil, objSoil) = this.checkContainerHas("soil")
    if (hasSoil) {
      durationWithoutSoil = 0
    } else {
      stressed = true

      // If the plant is out of soil for a while, then it dies.
      durationWithoutSoil += 1
      if (durationWithoutSoil >= (maxDurationWithoutSoil/2)) {
        sickly = true
      }
      if (durationWithoutSoil >= maxDurationWithoutSoil) {
        lifecycle.changeStage(PlantLifeStages.PLANT_STAGE_DEATH)
      }
    }

    // Check to see if it's out of temperature range -- if so, it dies.
    val temperatureC = obj.propMaterial.get.temperatureC
    if ((temperatureC >= obj.propLife.get.maxTemp) || (temperatureC <= obj.propLife.get.minTemp)) {
      durationOutOfTemperatureBounds += 1
      sickly = true
      if (durationOutOfTemperatureBounds >= maxDurationOutOfTemperatureBounds) {
        lifecycle.changeStage(PlantLifeStages.PLANT_STAGE_DEATH)
      }
    } else {
      durationOutOfTemperatureBounds = 0
    }



    // Set illness status
    obj.propLife.get.isSickly = sickly

    // Don't continue to life stage progression if stressed
    if (stressed) return

    ticksMeetingCriteria += 1
    if (ticksMeetingCriteria >= stageDuration) {
      // Consume the water (delete it from the simulation)
      objWater.get.delete()

      // Move onto next stage
      lifecycle.changeStage(PlantLifeStages.PLANT_STAGE_ADULT_PLANT)
    }
  }

}

// Seed stage
class PlantLifeStageAdult(obj:EnvObject, lifecycle:LifeCycle) extends LifeStage(PlantLifeStages.PLANT_STAGE_ADULT_PLANT, obj, lifecycle) {
  var ticksMeetingCriteria:Int = 0
  val stageDuration:Int = 5

  var durationWithoutWater:Int = 0
  val maxDurationWithoutWater:Int = 50

  var durationWithoutSoil:Int = 0
  val maxDurationWithoutSoil:Int = 5

  var durationOutOfTemperatureBounds:Int = 0
  val maxDurationOutOfTemperatureBounds:Int = 5


  override def tick(): Unit = {
    var stressed:Boolean = false
    var sickly:Boolean = false
    this.incrementDuration()

    // Check to see if the container that the seed is in has water
    val (hasWater, objWater) = this.checkContainerHas("water")
    if (hasWater) {
      // In a container with water:  Consume the water
      durationWithoutWater = 0          // Reset counter that counts how long it's been without water
      objWater.get.delete()             // Consume the water
    } else {
      stressed = true
      // Not in a container with water: Keep track of how long the plant is dry.  If for too long, look sickly, then die.
      durationWithoutWater += 1
      if (durationWithoutWater >= (maxDurationWithoutWater/2)) {
        sickly = true
      }
      if (durationWithoutWater >= maxDurationWithoutWater) {
        lifecycle.changeStage(PlantLifeStages.PLANT_STAGE_DEATH)
      }
    }

    // Check to see if the container the seed is in has soil
    val (hasSoil, objSoil) = this.checkContainerHas("soil")
    if (hasSoil) {
      durationWithoutSoil = 0
    } else {
      stressed = true
      // If the plant is out of soil for a while, then it dies.
      durationWithoutSoil += 1
      if (durationWithoutSoil >= (maxDurationWithoutSoil/2)) {
        sickly = true
      }
      if (durationWithoutSoil >= maxDurationWithoutSoil) {
        lifecycle.changeStage(PlantLifeStages.PLANT_STAGE_DEATH)
      }
    }

    // Check to see if it's out of temperature range -- if so, it dies.
    val temperatureC = obj.propMaterial.get.temperatureC
    if ((temperatureC >= obj.propLife.get.maxTemp) || (temperatureC <= obj.propLife.get.minTemp)) {
      durationOutOfTemperatureBounds += 1
      sickly = true
      if (durationOutOfTemperatureBounds >= maxDurationOutOfTemperatureBounds) {
        lifecycle.changeStage(PlantLifeStages.PLANT_STAGE_DEATH)
      }
    } else {
      durationOutOfTemperatureBounds = 0
    }


    // Set illness status
    obj.propLife.get.isSickly = sickly

    // Don't continue to life stage progression if stressed
    if (stressed) return

    ticksMeetingCriteria += 1
    if (ticksMeetingCriteria >= stageDuration) {
      // Consume the water
      objWater.get.delete()             // Consume the water

      // Move onto next stage
      //lifecycle.changeStage(PlantLifeStages.PLANT_STAGE_SEEDLING)
    }
  }

}

// Seed stage
class PlantLifeStageDeath(obj:EnvObject, lifecycle:LifeCycle) extends LifeStage(PlantLifeStages.PLANT_STAGE_DEATH, obj, lifecycle) {
  var ticksMeetingCriteria:Int = 0

  override def tick(): Unit = {
    // The plant is dead -- do nothing
    obj.propLife.get.isDead = true

    this.incrementDuration()
  }

}

/*
 * Plant life stages
 */
object PlantLifeStages {
  // Plant life cycle
  val PLANT_STAGE_SEED          = "seed"
  val PLANT_STAGE_SEEDLING      = "seedling"
  val PLANT_STAGE_ADULT_PLANT   = "adult"
  val PLANT_STAGE_REPRODUCING   = "reproducing"
  val PLANT_STAGE_DEATH         = "dead"


  def mkPlantLifeCycle(plant:EnvObject):LifeCycle = {
    val lifecycle = new LifeCycle("plant life cycle")

    lifecycle.addStage( new PlantLifeStageSeed(plant, lifecycle), isDefault = true )
    lifecycle.addStage( new PlantLifeStageSeedling(plant, lifecycle) )
    lifecycle.addStage( new PlantLifeStageAdult(plant, lifecycle) )
    // TODO: Reproducing
    lifecycle.addStage( new PlantLifeStageDeath(plant, lifecycle) )

    // Return
    lifecycle
  }

}