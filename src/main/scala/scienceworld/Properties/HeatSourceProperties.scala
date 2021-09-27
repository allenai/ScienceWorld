package scienceworld.Properties


class HeatSourceProperties(var maxTemp:Double, var curSetTemp:Double) {

  def setOnMax(): Unit = {
    curSetTemp = maxTemp
  }

  def setOff(): Unit = {
    curSetTemp = 0.0
  }

}



class HeatSourcePropertiesStove extends HeatSourceProperties(maxTemp = 260.0f, curSetTemp = 0.0f) {

}

class HeatSourcePropertiesWoodFire extends HeatSourceProperties(maxTemp = 600.0f, curSetTemp = 0.0f) {

}
