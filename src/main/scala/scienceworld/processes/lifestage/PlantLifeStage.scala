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
    println ("PLANT TICK: " + obj.name)

    this.incrementDuration()

    // Check to see if the container that the seed is in has water
    val (hasWater, objWater) = this.checkContainerHas("water")
    if (!hasWater) {
      println ("* Plant (" + obj.name + " / " + this.stageName + "): Criteria not met: Missing water")
      return
    }

    // Check to see if the container the seed is in has soil
    val (hasSoil, objSoil) = this.checkContainerHas("water")
    if (!hasSoil) {
      println ("* Plant (" + obj.name + " / " + this.stageName + "): Criteria not met: Missing soil")
      return
    }

    ticksMeetingCriteria += 1
    if (ticksMeetingCriteria >= stageDuration) {
      println ("* Plant (" + obj.name + " / " + this.stageName + "): Criteria met: (ticks: " + ticksMeetingCriteria + ")")
      // TODO: Consume the water

      // Move onto next stage
      lifecycle.changeStage(PlantLifeStages.PLANT_STAGE_SEEDLING)
    }

  }

}


// Seed stage
class PlantLifeStageSeedling(obj:EnvObject, lifecycle:LifeCycle) extends LifeStage(PlantLifeStages.PLANT_STAGE_SEEDLING, obj, lifecycle) {
  var ticksMeetingCriteria:Int = 0
  val stageDuration:Int = 10

  override def tick(): Unit = {
    println ("Seedling tick")
    this.incrementDuration()

    // Check to see if the container that the seed is in has water
    val (hasWater, objWater) = this.checkContainerHas("water")
    if (!hasWater) return

    // Check to see if the container the seed is in has soil
    val (hasSoil, objSoil) = this.checkContainerHas("water")
    if (!hasSoil) return

    ticksMeetingCriteria += 1
    if (ticksMeetingCriteria >= stageDuration) {
      // TODO: Consume the water

      // Move onto next stage
      //lifecycle.changeStage(PlantLifeStages.PLANT_STAGE_SEEDLING)
    }
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

    // Return
    lifecycle
  }

}