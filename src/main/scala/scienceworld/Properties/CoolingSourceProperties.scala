package scienceworld.Properties


class CoolingSourceProperties(var minTemp:Double, var curSetTemp:Option[Double]) {

  def setOnMin(): Unit = {
    curSetTemp = Some(minTemp)
  }

  def setOff(): Unit = {
    curSetTemp = None
  }

}



class CoolingSourcePropertiesFridge extends CoolingSourceProperties(minTemp = 2.0f, curSetTemp = None) {

}

class CoolingSourcePropertiesFreezer extends CoolingSourceProperties(minTemp = -18.0f, curSetTemp = None) {

}