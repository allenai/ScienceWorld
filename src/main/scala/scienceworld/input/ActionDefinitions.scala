package scienceworld.input

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger, ParamSig, ParamSigList}
import scienceworld.actions.{ActionActivate, ActionCloseDoor, ActionConnectElectrical, ActionDeactivate, ActionDisconnectElectrical, ActionEat, ActionFlush, ActionFocus, ActionInventory, ActionLookAround, ActionLookAt, ActionLookIn, ActionMix, ActionMoveObject, ActionMoveThroughDoor, ActionOpenDoor, ActionPickUpObjectIntoInventory, ActionPourObject, ActionPutDownObjectIntoInventory, ActionRead, ActionResetTask, ActionUseDevice, ActionWait, PossibleAction}
import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject

import scala.collection.mutable.ArrayBuffer

class ActionDefinitions {

}

object ActionDefinitions {
  val ACTION_ID_OPEN          = 0
  val ACTION_ID_CLOSE         = 1
  val ACTION_ID_MOVETHRUDOOR  = 2
  val ACTION_ID_LOOK_AROUND   = 3
  val ACTION_ID_LOOK_AT       = 4
  val ACTION_ID_LOOK_IN       = 5
  val ACTION_ID_ACTIVATE      = 6
  val ACTION_ID_DEACTIVATE    = 7
  val ACTION_ID_EAT           = 8
  val ACTION_ID_MOVEOBJECT    = 9
  val ACTION_ID_POUROBJECT    = 10
  val ACTION_ID_FOCUS         = 11
  val ACTION_ID_RESETTASK     = 12
  val ACTION_ID_USEDEVICE     = 13
  val ACTION_ID_READ          = 14
  val ACTION_ID_FLUSH         = 15
  val ACTION_ID_CONNECT       = 16
  val ACTION_ID_DISCONNECT    = 17
  val ACTION_ID_WAIT          = 18
  val ACTION_ID_INVENTORY     = 19
  val ACTION_ID_PICKUP        = 20
  val ACTION_ID_PUTDOWN       = 21
  val ACTION_ID_MIX           = 22


  /*
   * Helper functions
   */
  def mkActionRequest(name:String, triggerPhrase:List[ActionTrigger], uniqueActionID:Int):ActionRequestDef = {
    new ActionRequestDef(name, new ParamSigList(List.empty[ParamSig]), triggerPhrase, uniqueActionID)
  }

  def mkActionRequest(name:String, triggerPhrase:ActionTrigger, uniqueActionID:Int):ActionRequestDef = {
    new ActionRequestDef(name, new ParamSigList(List.empty[ParamSig]), List(triggerPhrase), uniqueActionID)
  }


  /*
   * Main action definitions
   */

  def mkActionDefinitions(): ActionHandler = {
    val actionHandler = new ActionHandler()

    // Open/close door
    ActionOpenDoor.registerAction(actionHandler)
    ActionCloseDoor.registerAction(actionHandler)

    // Move through door
    ActionMoveThroughDoor.registerAction(actionHandler)

    // Look around
    ActionLookAround.registerAction(actionHandler)
    ActionLookAt.registerAction(actionHandler)
    ActionLookIn.registerAction(actionHandler)

    // Activate/Deactivate
    ActionActivate.registerAction(actionHandler)
    ActionDeactivate.registerAction(actionHandler)

    // Eat
    ActionEat.registerAction(actionHandler)

    // Move an object to a new container
    ActionMoveObject.registerAction(actionHandler)
    ActionPourObject.registerAction(actionHandler)

    // Focus on object
    ActionFocus.registerAction(actionHandler)
    ActionResetTask.registerAction(actionHandler)

    // Use device
    ActionUseDevice.registerAction(actionHandler)

    // Read
    ActionRead.registerAction(actionHandler)

    // Flush
    ActionFlush.registerAction(actionHandler)

    // Connect (electrically)
    ActionConnectElectrical.registerAction(actionHandler)
    ActionDisconnectElectrical.registerAction(actionHandler)

    // Wait
    ActionWait.registerAction(actionHandler)

    // Inventory
    ActionInventory.registerAction(actionHandler)
    ActionPickUpObjectIntoInventory.registerAction(actionHandler)
    ActionPutDownObjectIntoInventory.registerAction(actionHandler)

    // Mix
    ActionMix.registerAction(actionHandler)

    // Return
    actionHandler
  }

  /*
   * Make possible actions
   */
  def mkPossibleActions(agent:Agent, visibleObjects:Array[EnvObject], uuid2referentLUT:Map[Long, String]):Array[PossibleAction] = {
    val out = new ArrayBuffer[PossibleAction]()

    // Open/close door
    out.insertAll(out.length, ActionOpenDoor.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))
    out.insertAll(out.length, ActionCloseDoor.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Move through door
    out.insertAll(out.length, ActionMoveThroughDoor.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Look around
    out.insertAll(out.length, ActionLookAround.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))
    out.insertAll(out.length, ActionLookAt.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))
    out.insertAll(out.length, ActionLookIn.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Activate/Deactivate
    out.insertAll(out.length, ActionActivate.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))
    out.insertAll(out.length, ActionDeactivate.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Eat
    out.insertAll(out.length, ActionEat.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Move an object to a new container
    out.insertAll(out.length, ActionMoveObject.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))
    out.insertAll(out.length, ActionPourObject.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Focus on object
    out.insertAll(out.length, ActionFocus.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))
    out.insertAll(out.length, ActionResetTask.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Use device
    out.insertAll(out.length, ActionUseDevice.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Read
    out.insertAll(out.length, ActionRead.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Flush
    out.insertAll(out.length, ActionFlush.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Connect (electrically)
    out.insertAll(out.length, ActionConnectElectrical.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))
    out.insertAll(out.length, ActionDisconnectElectrical.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Wait
    out.insertAll(out.length, ActionWait.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))

    // Inventory
    out.insertAll(out.length, ActionInventory.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))
    out.insertAll(out.length, ActionPickUpObjectIntoInventory.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))
    out.insertAll(out.length, ActionPutDownObjectIntoInventory.generatePossibleValidActions(agent, visibleObjects, uuid2referentLUT))


    // Return
    return out.toArray
  }


}
