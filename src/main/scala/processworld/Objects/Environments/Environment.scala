package processworld.Objects.Environments

import processworld.struct.stdProp

class Environment(objectName:String) extends processworld.struct.Object(objectName) {

}


class Kitchen extends Environment("kitchen") {
  // Register properties
  this.setVarDouble(stdProp.VOLUME_LITER,100000.0)
  this.setVarDouble(stdProp.CONTAINER_CAPACITY_LITERS,100000.0)


}
