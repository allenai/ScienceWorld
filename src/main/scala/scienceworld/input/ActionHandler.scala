package scienceworld.input

import language.model.{ActionExpr, ActionExprIdentifier, ActionExprOR, ActionRequestDef, ActionTrigger, ParamSig, ParamSigList}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class ActionHandler {
  val actions = mutable.Map[String, ActionRequestDef]()


  def getActions():Array[ActionRequestDef] = this.actions.map(_._2).toArray


  def addAction(actionName:String, triggerPhrase:List[ActionTrigger]): Unit = {
    actions(actionName) = new ActionRequestDef(actionName, new ParamSigList(List.empty[ParamSig]), triggerPhrase)
  }




}

