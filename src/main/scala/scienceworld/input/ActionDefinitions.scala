package scienceworld.input

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger, ParamSig, ParamSigList}
import scienceworld.actions.{ActionActivate, ActionCloseDoor, ActionDeactivate, ActionEat, ActionFocus, ActionLookAround, ActionLookAt, ActionLookIn, ActionMoveObject, ActionMoveThroughDoor, ActionOpenDoor, ActionPourObject}

import scala.collection.mutable.ArrayBuffer

class ActionDefinitions {

}

object ActionDefinitions {

  /*
   * Helper functions
   */
  def mkActionRequest(name:String, triggerPhrase:List[ActionTrigger]):ActionRequestDef = {
    new ActionRequestDef(name, new ParamSigList(List.empty[ParamSig]), triggerPhrase)
  }

  def mkActionRequest(name:String, triggerPhrase:ActionTrigger):ActionRequestDef = {
    new ActionRequestDef(name, new ParamSigList(List.empty[ParamSig]), List(triggerPhrase))
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


    // Return
    actionHandler
  }


}
