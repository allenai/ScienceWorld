package processworld.Objects.Foods

import processworld.struct.{stdObject, stdProp}

class Food(objectName:String) extends processworld.struct.Object(objectName) {

}


class Apple extends Food(objectName = stdObject.OBJ_APPLE) {
  // Register properties
  this.setVarDouble(stdProp.VOLUME_LITER,0.1)

}

class Orange extends Food(objectName = stdObject.OBJ_ORANGE) {
  // Register properties
  this.setVarDouble(stdProp.VOLUME_LITER,0.1)

}

class Grape extends Food(objectName = stdObject.OBJ_GRAPE) {
  // Register properties
  this.setVarDouble(stdProp.VOLUME_LITER,0.01)

}
