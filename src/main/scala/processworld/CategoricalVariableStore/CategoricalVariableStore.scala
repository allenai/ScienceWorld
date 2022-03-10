package processworld.CategoricalVariableStore

import processworld.struct.{CategoricalVariable, stdProp}
import processworld.struct.CategoricalVariable._

/*
 * Hard-coded store of categorical variables
 */

class CategoricalVariableStore {
  val categoricalVariableStore = scala.collection.mutable.Map[String, CategoricalVariable]()
  val categoricalVariableDescriptions = scala.collection.mutable.Map[String, String]()


  /*
   * Definitions
   */

  // State of matter ( STATE_OF_MATTER )
  categoricalVariableDescriptions(stdProp.STATE_OF_MATTER) = "A substance's state of matter."
  categoricalVariableStore(stdProp.STATE_OF_MATTER) = new CategoricalVariable(values = Array("solid", "liquid", "gas"), defaultValueIdx = 0, varType = CATVAR_TYPE_STRING)



  // Clean or dirty ( CLEAN )
  categoricalVariableDescriptions(stdProp.CLEANLINESS) = "Is an object clean or dirty?"
  categoricalVariableStore(stdProp.CLEANLINESS) = new CategoricalVariable(values = Array("clean", "a little dirty", "dirty"), defaultValueIdx = 0, varType = CATVAR_TYPE_STRING)



  // Container lid open/closed
  categoricalVariableDescriptions(stdProp.CONTAINER_OPEN) = "Is a container open or closed?"
  categoricalVariableStore(stdProp.CONTAINER_OPEN) = new CategoricalVariable(values = Array("closed", "open"), defaultValueIdx = 0, varType = CATVAR_TYPE_STRING)

  // Drawers/Dishwashers
  categoricalVariableDescriptions(stdProp.DRAWER_OUT) = "Is a drawer in or out?"
  categoricalVariableStore(stdProp.DRAWER_OUT) = new CategoricalVariable(values = Array("in", "out"), defaultValueIdx = 0, varType = CATVAR_TYPE_STRING)

  // Porous containers
  categoricalVariableDescriptions(stdProp.CONTAINER_POROUS) = "Is a container porous? (objects are interactable from outside the container for some actions)"
  categoricalVariableStore(stdProp.CONTAINER_POROUS) = new CategoricalVariable(values = Array("not porous", "porous"), defaultValueIdx = 0, varType = CATVAR_TYPE_STRING)


  // Activated/deactivated
  categoricalVariableDescriptions(stdProp.ACTIVATED) = "Is a device activated?"
  categoricalVariableStore(stdProp.ACTIVATED) = new CategoricalVariable(values = Array("deactivated", "activated"), defaultValueIdx = 0, varType = CATVAR_TYPE_STRING)


  /*
   * Accessors
   */

  def getCategoricalVariable(name:String):CategoricalVariable = {
    categoricalVariableStore(name)
  }

  def getCategoricalVariableDesc(name:String):String = {
    categoricalVariableDescriptions(name)
  }

}



object CategoricalVariableStore {
  val cvStore = new CategoricalVariableStore()
}