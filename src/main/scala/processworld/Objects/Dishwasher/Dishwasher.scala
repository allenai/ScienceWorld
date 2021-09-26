package processworld.Objects.Dishwasher

import processworld.CategoricalVariableStore.CategoricalVariableStore
import processworld.Objects.Substances.Water
import processworld.struct.{Action, ActionInfo, stdAction, stdObject, stdParams, stdProp}

class Dishwasher extends processworld.struct.Object(objectName = stdObject.OBJ_DISHWASHER) {
  // Register parts
  this.addObjectToContainer( new DishwasherRack )
  this.addObjectToContainer( new DishwasherRack )
  this.addObjectToContainer( new DishwasherSoapCup )
  this.addObjectToContainer( new DishwasherSprayer )

  // Register properties
  this.setVarDouble(stdProp.VOLUME_LITER,320.0)        // Actually an average dishwasher volume
  this.setVarDouble(stdProp.CONTAINER_CAPACITY_LITERS,300.0)        // For all the dishwasher parts, and the dishes

  // Opened/closed
  this.defineVarCategorical(stdProp.CONTAINER_OPEN, CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CONTAINER_OPEN))

  // Activated/deactivated
  this.defineVarCategorical(stdProp.ACTIVATED, CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.ACTIVATED))
  this.setVarDouble(stdProp.ACTIVATION_STAGE, stdParams.DEACTIVATED)


  // Register Actions
  val actionTurnOn = new Action(name = stdAction.TURN_ON, callback = actionCallbackTurnOn)
  this.addAction(actionTurnOn)

  val actionOpen = new Action(stdAction.CONTAINER_OPEN, callback = actionCallbackOpenContainer)
  this.addAction(actionOpen)

  val actionClose = new Action(stdAction.CONTAINER_CLOSE, callback = actionCallbackCloseContainer)
  this.addAction(actionClose)


  val actionActivated = new Action(stdAction.ACTIVATION_CYCLE, isAutomatic = true, callback = actionCallbackWashProcessTick)
  this.addAction(actionActivated)


  /*
   * Action Callbacks
   */
  // Container: Open
  override def actionCallbackOpenContainer(actionInfo:ActionInfo):Boolean = {
    return super.actionCallbackOpenContainer(actionInfo)
  }

  // Container: Close
  // Note: can't close if the dishracks are "out"
  override def actionCallbackCloseContainer(actionInfo:ActionInfo):Boolean = {
    // Check if dishracks are out
    val dishracks = this.getContainedObjectsWithName(stdObject.OBJ_DISHWASHER_RACK, allowPorousContainers = true)
    val valueIn = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.DRAWER_OUT).values(0) // 0: "in"
    for (dishrack <- dishracks) {
      if (dishrack.getVarCategorical(stdProp.DRAWER_OUT, "") != valueIn) {
        // At least one dishrack is not "in" -- so the container can't be closed
        return failure("At least one dishrack is not 'in', so the dishwasher can't be closed.")
      }
    }

    // Super
    return super.actionCallbackCloseContainer(actionInfo)
  }

  // Start the wash process
  def actionCallbackTurnOn(actionInfo:ActionInfo): Boolean = {
    // Set to "turned on"
    val valueActivated = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.ACTIVATED).values(1)   // 1: "activated"
    this.setVarCategorical(stdProp.ACTIVATED, valueActivated)

    // Begin stage at the start, unless the stage is already part way through
    val curWashStage = this.getVarDouble(stdProp.ACTIVATION_STAGE, stdParams.DEACTIVATED)
    if (curWashStage == stdParams.DEACTIVATED) {
      this.setVarDouble(stdProp.ACTIVATION_STAGE, 0)      // 0 = start
    }

    true
  }

  def actionCallbackWashProcessTick(actionInfo:ActionInfo):Boolean = {
    val curWashStage = this.getVarDouble(stdProp.ACTIVATION_STAGE, stdParams.DEACTIVATED)

    // Step 1: If not activated, do nothing
    if (curWashStage == stdParams.DEACTIVATED) return false                 // If dishwasher is not on, then exit

    addLog("Dishwasher Process Tick: At Stage " + curWashStage)
    // Step 2: If activated, do tasks for current step
    curWashStage match {
      case 0 => {
        addLog("Dishwasher Process Tick: Opening soap cup...")
        // Open the dishwasher cup
        val soapCup = this.getContainedObjectsWithName(stdObject.OBJ_DISHWASHER_SOAP_CUP)
        if (soapCup.length > 0) {
          for (sc <- soapCup) sc.doAction(stdAction.CONTAINER_OPEN, new ActionInfo())       // TODO: Will need to specify the agent?
          this.incVarDouble(stdProp.ACTIVATION_STAGE, 1)                           // Increment to next stage
        } else {
          // Error -- soap cup is missing.
          this.setVarDouble(stdProp.ACTIVATION_STAGE, stdParams.DEACTIVATED)
        }
      }
      case x if 1 to 10 contains x => {
        addLog("Dishwasher Process Tick: Activating sprayer...")
        // Activate the sprayer -- the idea being that the sprayer will spray water, which will combine with the soap and wash the dishes.
        val sprayer = this.getContainedObjectsWithName(stdObject.OBJ_DISHWASHER_SPRAYER)
        if (sprayer.length > 0) {
          for (sp <- sprayer) sp.doAction(stdAction.SPRAY_WATER, new ActionInfo())
          this.incVarDouble(stdProp.ACTIVATION_STAGE, 1)
        } else {
          // Error -- sprayer is missing
          this.setVarDouble(stdProp.ACTIVATION_STAGE, stdParams.DEACTIVATED)
        }
      }
      case 11 => {
        addLog("Dishwasher Process Tick: Flush water and soap...")
        // Flush the water and soap out of the dishwasher ("down the drain", but this isn't currently modelled)

        // Remove all water
        val objsWater = this.getContainedObjectsWithName(stdObject.OBJ_WATER)
        for (obj <- objsWater) obj.markDestroyed()
        // Remove all soap
        val objsSoap = this.getContainedObjectsWithName(stdObject.OBJ_SOAP)
        for (obj <- objsSoap) obj.markDestroyed()

        this.incVarDouble(stdProp.ACTIVATION_STAGE, 1)                           // Increment to next stage
      }
      case 12 => {
        addLog("Dishwasher Process Tick: Deactivate...")
        // All done?
        this.setVarDouble(stdProp.ACTIVATION_STAGE, stdParams.DEACTIVATED)                // Updates stage only. When reaching the code below, the device will automatically be marked as deactivated
      }

    }


    // Step 3: If the ending stage is 'deactivated', then set the ACTIVATED property to "deactivated".  Otherwise, set to "activated".
    val endingWashStage = this.getVarDouble(stdProp.ACTIVATION_STAGE, -1)
    if (endingWashStage == stdParams.DEACTIVATED) {
      val valueDeactivated = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.ACTIVATED).values(0)   // 0: "deactivated"
      this.setVarCategorical(stdProp.ACTIVATED, valueDeactivated)
    } else {
      val valueActivated = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.ACTIVATED).values(1)   // 1: "activated"
      this.setVarCategorical(stdProp.ACTIVATED, valueActivated)
    }


    true
  }

}



class DishwasherRack extends processworld.struct.Object(objectName = stdObject.OBJ_DISHWASHER_RACK) {
  // Register properties
  this.setVarDouble(stdProp.VOLUME_LITER,100.0)
  this.setVarDouble(stdProp.CONTAINER_CAPACITY_LITERS,90.0)        // For all the loaded dishes

  // Drawer in/out (default in)
  this.defineVarCategorical(stdProp.DRAWER_OUT, CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.DRAWER_OUT))

  // Set to a porous container
  this.defineVarCategorical(stdProp.CONTAINER_POROUS, CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CONTAINER_POROUS))
  val valuePorous = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CONTAINER_POROUS).values(1)     // 1: Porous
  this.setVarCategorical(stdProp.CONTAINER_POROUS, valuePorous)

  // Register Actions
  val actionSlideOut = new Action(name = stdAction.DRAWER_SLIDEOUT, callback = actionCallbackDrawerSlideOut)      // Could also be an 'open/close' container action, though here it can only happen if the dishwasher is open
  this.addAction(actionSlideOut)

  val actionSlideIn = new Action(name = stdAction.DRAWER_SLIDEIN, callback = actionCallbackDrawerSlideIn)
  this.addAction(actionSlideIn)


  /*
   * Action Callbacks
   */
  // Drawer: Slide Out
  override def actionCallbackDrawerSlideOut(actionInfo:ActionInfo):Boolean = {
    // Check that the rack is in the dishwasher
    if (this.inContainer.isEmpty) return failure("Dishrack must be in dishwasher.")
    if (this.inContainer.get.objectName != stdObject.OBJ_DISHWASHER) return failure("Dishrack must be in dishwasher.")

    // Check that the dishwasher is open
    val dishwasherContainerState = this.inContainer.get.getVarCategorical(stdProp.CONTAINER_OPEN, "")
    val dishwasherOpen = CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CONTAINER_OPEN).values(1) // 1: "open"
    if (dishwasherContainerState != dishwasherOpen) return failure("Dishwasher must be open for dishrack to slide out. ")

    // Super
    return super.actionCallbackDrawerSlideOut(actionInfo)
  }

  // Drawer: Slide In
  override def actionCallbackDrawerSlideIn(actionInfo:ActionInfo):Boolean = {
    return super.actionCallbackDrawerSlideIn(actionInfo)
  }

}



class DishwasherSoapCup extends processworld.struct.Object(objectName = stdObject.OBJ_DISHWASHER_SOAP_CUP) {
  // Register properties
  this.setVarDouble(stdProp.VOLUME_LITER,0.5)
  this.setVarDouble(stdProp.CONTAINER_CAPACITY_LITERS,0.1)        // For soap


  this.defineVarCategorical(stdProp.CONTAINER_OPEN, CategoricalVariableStore.cvStore.getCategoricalVariable(stdProp.CONTAINER_OPEN))

  // Register Actions
  val actionOpenCup = new Action(name = stdAction.CONTAINER_OPEN, callback = actionCallbackOpenContainer)
  this.addAction(actionOpenCup)

  val actionCloseCup = new Action(name = stdAction.CONTAINER_CLOSE, callback = actionCallbackCloseContainer)
  this.addAction(actionCloseCup)

}



class DishwasherSprayer extends processworld.struct.Object(objectName = stdObject.OBJ_DISHWASHER_SPRAYER) {
  // Register properties
  this.setVarDouble(stdProp.VOLUME_LITER,5.0)
  this.setVarDouble(stdProp.CONTAINER_CAPACITY_LITERS,0.2)      // For water

  // Register Actions
  val actionSprayWater = new Action(name = stdAction.SPRAY_WATER, callback = actionCallbackSprayWater)
  this.addAction(actionSprayWater)

  def actionCallbackSprayWater(actionInfo:ActionInfo):Boolean = {
    // Step 1: Make a bit of water, and add it to the contents
    val objWater = new Water()
    this.addObjectToContainer(objWater)

    // Step 2: Expel some water from sprayer
    objWater.expelFromContainer()
  }

}
