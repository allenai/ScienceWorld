package scienceworld.properties

class LifeProperties {
  var lifeformType:String = ""

  // Temperature range -- beyond these ranges, the life form will die
  var minTemp:Double = 0.0
  var maxTemp:Double = 50.0

  // Whether the lifeform is ill or not
  var isSickly:Boolean = false
  var isDead:Boolean = false

}



class LifePropertiesPlant extends LifeProperties {
  this.lifeformType = "plant"

  this.minTemp = 0
  this.maxTemp = 50
}


class LifePropertiesApple extends LifePropertiesPlant {
  this.lifeformType = ""
}

