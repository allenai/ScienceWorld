package processworld.Objects.Dishes

import processworld.CategoricalVariableStore.CategoricalVariableStore
import processworld.Objects.Foods.Food
import processworld.Objects.Substances.Water
import processworld.struct.{Action, ActionInfo, stdAction, stdObject, stdParams, stdProp}

import scala.util.Random
import scala.util.control.Breaks._


class Dish(objectName:String) extends processworld.struct.Object(objectName) {
  // Register properties
  this.defineVarCategorical(stdProp.CLEANLINESS, CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CLEANLINESS))

  // Register Actions

  // Make dirty if the plate contains food
  val actionMakeDirty = new Action(name = stdAction.MAKE_DIRTY, isAutomatic = true, callback = actionCallbackAutoMakeDirty)
  this.addAction(actionMakeDirty)

  // Make clean if the plate is in a container with water
  val actionMakeClean = new Action(name = stdAction.MAKE_CLEAN, isAutomatic = true, callback = actionCallbackMakeClean)
  this.addAction(actionMakeClean)


  /*
   * Action callbacks
   */
  // If the dish has a food in its contents, then make it dirty
  def actionCallbackAutoMakeDirty(actionInfo:ActionInfo):Boolean = {
    val valueDirty:String = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CLEANLINESS).values(2)  // 2: "dirty"

    // Do not run if the end state already exists (for succinctness)
    breakable {
      for (obj <- this.contents) {
        obj match {
          case f: Food => {
            // The condition is met (the plate is dirty)
            if (this.getVarCategorical(stdProp.CLEANLINESS, "") != valueDirty) {
              this.setVarCategorical(stdProp.CLEANLINESS, valueDirty)
              addLog("A dish (" + getNameString() + ") contains food (" + f.getNameString() + "): Setting it to dirty.")
            }
            break()
          }
        }
      }
      // If we reach here, the condition was not met
      return false
    }

    // Return
    true
  }

  // If the dish is dirty, and it's in the same container as water, then set it to be clean
  def actionCallbackMakeClean(actionInfo:ActionInfo):Boolean = {
    val verboseDebug:Boolean = true

    val valueClean:String = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CLEANLINESS).values(0)  // 0: "clean"
    val valueLittleDirty:String = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CLEANLINESS).values(1)  // 1: "a little dirty"
    val valueDirty:String = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CLEANLINESS).values(2)  // 2: "dirty"

    // Do not run if the end state already exists (for succinctness)
    if (this.getVarCategorical(stdProp.CLEANLINESS, "") == valueClean) return false      // Already clean, exit

    // Check to make sure there's nothing that would make it dirty?
    if (actionCallbackAutoMakeDirty(actionInfo) == true) {
      addLog("A dish has some condition making it dirty, so it can't currently be cleaned.")
      return false
    } else {
      addLog("There is nothing that appears to be making the dish dirty.")
    }

    // Check if there is water available in this container as well
    if (this.inContainer.isEmpty) {
      if (verboseDebug) addLog("Make Clean: Object is not in a container.  Exiting.")
      return false
    }                                                         // Not in container, exit
    val container = this.inContainer.get

    val objsWater = container.getContainedObjectsWithName(stdObject.OBJ_WATER, allowPorousContainers = true)
    if (objsWater.length == 0) {
      if (verboseDebug) addLog("Make Clean: No water in container.  Exiting.")
      return false
    }                                                             // No water, exit

    // Search for clean water
    var cleanWater: Option[processworld.struct.Object] = None
    breakable {
      for (water <- objsWater) {
        if (water.getVarCategorical(stdProp.CLEANLINESS, "") == valueClean) {
          cleanWater = Some(water)
          break
        }
      }
    }
    if (cleanWater.isEmpty) {
      if (verboseDebug) addLog("Make Clean: No clean water in container.  Exiting.")
      return false
    }                                                               // No clean water


    val objsSoap = container.getContainedObjectsWithName(stdObject.OBJ_SOAP, allowPorousContainers = true)
    //if (objSoap.length == 0) return false                                                             // No soap, exit

    // If we reach here, the object is in a container with water (and possibly soap).
    if (objsSoap.length > 0) {
      val objSoap = objsSoap(0)
      // Has soap -- set object to clean
      addLog("A dish (" + getNameString() + ") is in a container (" + container.getNameString() + ") with water (" + cleanWater.get.getNameString() + ") and soap (" + objSoap.getNameString() + "): Setting it to clean.")
      this.setVarCategorical(stdProp.CLEANLINESS, valueClean)

      //Use up a bit of the soap
      objSoap.reduceObjectVolume(stdParams.SOAP_VOLUME_TO_WASH_1_DISH)

    } else {
      // No soap -- have a probability of setting to clean
      val randProb = stdParams.PROB_OF_CLEANING_DISH_WITHOUT_SOAP
      if (Random.nextDouble() < randProb) {
        // Randomly set object to clean
        addLog("A dish (" + getNameString() + ") is in a container (" + container.getNameString() + ") with water (" + cleanWater.get.getNameString() + ") but no soap: Randomly setting it to clean.")
        this.setVarCategorical(stdProp.CLEANLINESS, valueClean)
      } else {
        // Randomly set object to a little dirty
        addLog("A dish (" + getNameString() + ") is in a container (" + container.getNameString() + ") with water (" + cleanWater.get.getNameString() + ") but no soap: Randomly setting it to a little dirty. ")
        this.setVarCategorical(stdProp.CLEANLINESS, valueLittleDirty)
      }

    }

    // Also set water to dirty
    cleanWater.get.setVarCategorical(stdProp.CLEANLINESS, valueDirty)     // Set water to dirty




    true
  }

}



class Bowl extends Dish(objectName = stdObject.OBJ_BOWL) {
  this.setVarDouble(stdProp.VOLUME_LITER, 1.0)
  this.setVarDouble(stdProp.CONTAINER_CAPACITY_LITERS,1.0)

}

class Plate extends Dish(objectName = stdObject.OBJ_PLATE) {
  this.setVarDouble(stdProp.VOLUME_LITER, 0.5)
  this.setVarDouble(stdProp.CONTAINER_CAPACITY_LITERS,0.5)

}

class Cup extends Dish(objectName = stdObject.OBJ_CUP) {
  this.setVarDouble(stdProp.VOLUME_LITER, 0.4)
  this.setVarDouble(stdProp.CONTAINER_CAPACITY_LITERS,0.4)

}

class Fork extends Dish(objectName = stdObject.OBJ_FORK) {
  this.setVarDouble(stdProp.VOLUME_LITER, 0.1)
  this.setVarDouble(stdProp.CONTAINER_CAPACITY_LITERS,0.1)

}

class Spoon extends Dish(objectName = stdObject.OBJ_SPOON) {
  this.setVarDouble(stdProp.VOLUME_LITER, 0.1)
  this.setVarDouble(stdProp.CONTAINER_CAPACITY_LITERS,0.1)

}

class Knife extends Dish(objectName = stdObject.OBJ_KNIFE) {
  this.setVarDouble(stdProp.VOLUME_LITER, 0.1)
  this.setVarDouble(stdProp.CONTAINER_CAPACITY_LITERS,0.1)

}
