package processworld.Objects.Substances

import processworld.CategoricalVariableStore.CategoricalVariableStore
import processworld.struct.{stdObject, stdProp}

class Substance(objectName:String) extends processworld.struct.Object(objectName) {

}


class Water extends Substance(objectName = stdObject.OBJ_WATER) {
  // Register properties
  this.setVarDouble(stdProp.VOLUME_LITER,0.01)
  this.defineVarCategorical(stdProp.CLEANLINESS, CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CLEANLINESS))

}


class Soap extends Substance(objectName = stdObject.OBJ_SOAP) {
  // Register properties
  this.setVarDouble(stdProp.VOLUME_LITER,0.01)

}
