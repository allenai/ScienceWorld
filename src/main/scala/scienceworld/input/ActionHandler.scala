package scienceworld.input

import language.model.{ActionExpr, ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger, ParamSig, ParamSigList}
import scienceworld.actions.{Action, ActionFocus, ActionInventory, ActionLookAround, ActionTaskDesc}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class ActionHandler {
  val actions = mutable.Map[String, ActionRequestDef]()

  val queuedActions = new ArrayBuffer[Action]()
  val actionHistory = new ArrayBuffer[ Array[Action] ]()

  /*
   * Accessors
   */
  def getActions():Array[ActionRequestDef] = this.actions.map(_._2).toArray

  def addAction(actionName:String, triggerPhrase:List[ActionTrigger], uniqueActionID:Int): Unit = {
    actions(actionName) = new ActionRequestDef(actionName, new ParamSigList(List.empty[ParamSig]), triggerPhrase, uniqueActionID)
  }

  def addAction(actionDef:ActionRequestDef): Unit = {
    actions(actionDef.name) = actionDef
  }

  def getActionID(actionName:String):Int = {
    if (!this.actions.contains(actionName)) return -1
    this.actions(actionName).uniqueActionID
  }


  /*
   * Queueing actions
   */
  def queueAction(action:Action): Unit = {
    queuedActions.append(action)
  }


  /*
   * Running actions
   */
  def runQueuedActions(): String = {
    val out = new ArrayBuffer[String]

    // First, transfer queued actions to action history
    actionHistory.append( queuedActions.toArray )
    queuedActions.clear()
    val actionsToRun = actionHistory.last

    // Then, run actions
    for (action <- actionsToRun) {
      println ("Running action: " + action.name)

      val (resultDesc, success) = action.runAction()
      out.append("(" + action.name + "):\n" + resultDesc)
    }

    // Return
    return out.mkString("\n")
  }

  /*
   * Check whether queued actions consume time
   * true = takes time
   * false = free action
   */
  def doesActionTakeTime(action:Action):Boolean = {
    action match {
      case x:ActionLookAround => return false
      case x:ActionInventory => return false
      case x:ActionTaskDesc => return false
      case _ => return true
    }
  }

  def doQueuedActionsTakeTime():Boolean = {
    for (action <- queuedActions) {
      if (this.doesActionTakeTime(action)) return true
    }
    // Return
    false
  }

  /*
   * User-facing functions
   */
  def getActionExamplesPlainText(): Array[String] = {
    val out = new ArrayBuffer[String]

    for (action <- this.getActions()) {
      out.append( action.mkHumanReadableExample() )
    }

    // Return
    out.sorted.toArray
  }

  def getActionExamplesPlainTextWithID(): Array[ExampleAction] = {
    val out = new ArrayBuffer[ExampleAction]

    for (action <- this.getActions()) {
      val example = new ExampleAction(action.mkHumanReadableExample(), action.uniqueActionID)
      out.append( example )
    }

    // Return
    out.toArray
  }


}


object ActionHandler {

}


// Storage class for an example of an action, and that action template's unique ID
class ExampleAction(val exampleStr:String, val actionID:Int) {

  def toJSON():String = {
    return "{\"action_example\":\"" + exampleStr + "\", \"template_id\":" + actionID + "}"
  }

}