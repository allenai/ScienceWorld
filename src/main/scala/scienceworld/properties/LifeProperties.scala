package scienceworld.properties

class LifeProperties {
  // Temperature range -- beyond these ranges, the life form will die
  var minTemp:Double = 0.0
  var maxTemp:Double = 50.0

  // Whether the lifeform is ill or not
  var isSickly:Boolean = false
  var isDead:Boolean = false

}



class LifePropertiesPlant extends LifeProperties {
  this.minTemp = 0
  this.maxTemp = 50
}