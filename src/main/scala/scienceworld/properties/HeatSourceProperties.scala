package scienceworld.properties


class HeatSourceProperties(var maxTemp:Double, var curSetTemp:Option[Double]) {

  def setOnMax(): Unit = {
    curSetTemp = Some(maxTemp)
  }

  def setOff(): Unit = {
    curSetTemp = None
  }

}



class HeatSourcePropertiesStove extends HeatSourceProperties(maxTemp = 260.0f, curSetTemp = None) {

}

class HeatSourcePropertiesOven extends HeatSourceProperties(maxTemp = 540.0f, curSetTemp = None) {

}

class HeatSourcePropertiesWoodFire extends HeatSourceProperties(maxTemp = 600.0f, curSetTemp = None) {

}
