package scienceworld.input

import language.model.{ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger, ParamSig, ParamSigList}

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

    // Action: Open door
    val triggerPhraseOpenDoor1 = new ActionTrigger(List(
      new ActionExprOR(List("open")),
      new ActionExprIdentifier("door")
    ))
    val actionOpenDoor1 = mkActionRequest("open door", triggerPhraseOpenDoor1)
    actionHandler.addAction(actionOpenDoor1)

    // Action: Close door
    val triggerPhraseCloseDoor1 = new ActionTrigger(List(
      new ActionExprOR(List("close")),
      new ActionExprIdentifier("door")
    ))
    val actionCloseDoor1 = mkActionRequest("close door", triggerPhraseCloseDoor1)
    actionHandler.addAction(actionCloseDoor1)


    // Action: Move through door
    val triggerPhraseMoveThroughDoor1 = new ActionTrigger(List(
      new ActionExprOR(List("go through", "walk through", "move through", "go to", "walk to", "move to")),
      new ActionExprIdentifier("door")
    ))
    val actionMoveThroughDoor1 = mkActionRequest("move through door", triggerPhraseMoveThroughDoor1)
    actionHandler.addAction(actionMoveThroughDoor1)


    // Action: Eat
    val triggerPhraseEat = new ActionTrigger(List(
      new ActionExprOR(List("eat", "consume")),
      new ActionExprIdentifier("food")
    ))
    val actionEat = mkActionRequest("eat", triggerPhraseEat)
    actionHandler.addAction(actionEat)


    // Action: Look around
    val triggerPhraseLookAround = new ActionTrigger(List(
      new ActionExprOR(List("look", "look around")),
    ))
    val actionLookAround = mkActionRequest("look around", triggerPhraseLookAround)
    actionHandler.addAction(actionLookAround)


    // Action: Activate
    val triggerPhraseActivate = new ActionTrigger(List(
      new ActionExprOR(List("activate", "turn on")),
      new ActionExprIdentifier("device")
    ))
    val actionActivate = mkActionRequest("activate", triggerPhraseActivate)
    actionHandler.addAction(actionActivate)

    // Action: Deactivate
    val triggerPhraseDeactivate = new ActionTrigger(List(
      new ActionExprOR(List("deactivate", "turn off")),
      new ActionExprIdentifier("device")
    ))
    val actionDeactivate = mkActionRequest("deactivate", triggerPhraseDeactivate)
    actionHandler.addAction(actionDeactivate)


    // Action: Move
    val triggerPhraseMove = new ActionTrigger(List(
      new ActionExprOR(List("move")),
      new ActionExprIdentifier("obj"),
      new ActionExprOR(List("to")),
      new ActionExprIdentifier("moveTo")
    ))
    val actionMove = mkActionRequest("move", triggerPhraseMove)
    actionHandler.addAction(actionMove)





    // Return
    actionHandler
  }


}
