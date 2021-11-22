package scienceworld.properties

import scienceworld.processes.PlantReproduction

class LifeProperties {
  var lifeformType:String = ""

  // Temperature range -- beyond these ranges, the life form will die
  var minTemp:Double = 0.0
  var maxTemp:Double = 50.0

  // Whether the lifeform is ill or not
  var isSickly:Boolean = false
  var isDead:Boolean = false

  // Typical lifespan (in years) -- note, this does not currently affect the simulation, but is used for question answering.
  var lifespanTypical:Double = 0.0f

}


/*
 * Plants
 */

class LifePropertiesPlant extends LifeProperties {
  this.lifeformType = "plant"

  this.minTemp = 0
  this.maxTemp = 50
}


class LifePropertiesApple extends LifePropertiesPlant {
  this.lifeformType = PlantReproduction.PLANT_APPLE
}

class LifePropertiesAvocado extends LifePropertiesPlant {
  this.lifeformType = PlantReproduction.PLANT_AVOCADO
}

class LifePropertiesBanana extends LifePropertiesPlant {
  this.lifeformType = PlantReproduction.PLANT_BANANA
}

class LifePropertiesCherry extends LifePropertiesPlant {
  this.lifeformType = PlantReproduction.PLANT_CHERRY
}

class LifePropertiesLemon extends LifePropertiesPlant {
  this.lifeformType = PlantReproduction.PLANT_LEMON
}

class LifePropertiesOrange extends LifePropertiesPlant {
  this.lifeformType = PlantReproduction.PLANT_ORANGE
}

class LifePropertiesPeach extends LifePropertiesPlant {
  this.lifeformType = PlantReproduction.PLANT_PEACH
}


/*
 * Animals
 */
class LifePropertiesAnimal extends LifeProperties {
  this.lifeformType = "animal"

  this.minTemp = 0
  this.maxTemp = 50
}
