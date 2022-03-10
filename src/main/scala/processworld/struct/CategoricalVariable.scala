package processworld.struct

/*
 * Storage class for a categorical variable
 */

class CategoricalVariable(val values:Array[String], val defaultValueIdx:Int, val varType:String) {

  // Returns true if the value is a valid possible value of this categorical variable
  def isValidValue(value:String): Boolean = {
    if (values.contains(value)) return true
    // Default return
    false
  }

}

// An instance of a categorical variable
class CategoricalVariableInstance(val categoricalVariable:CategoricalVariable, var valueIdx:Int) {

  def setValue(valueIdxToSet:Int):Boolean = {
    if ((valueIdx >= 0) && (valueIdx < categoricalVariable.values.length)) {
      valueIdx = valueIdxToSet
      return true
    } else {
      // ERROR: Value does not exist
      return false
    }
  }

  def getValue():String = {
    return categoricalVariable.values(valueIdx)
  }

}


object CategoricalVariable {
  val CATVAR_TYPE_STRING    = "STRING"
  val CATVAR_TYPE_DOUBLE    = "DOUBLE"

}
