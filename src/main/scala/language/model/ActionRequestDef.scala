package language.model

import scala.util.parsing.input.Positional

case class ActionRequestDef(val name:String, val paramSigList:ParamSigList, val triggers:List[ActionTrigger]) extends Statement {

  def getVarType(varName:String):String = {
    paramSigList.getVarType(varName)
  }

  override def toString():String = {
    val os = new StringBuilder

    os.append("ActionRequestDef(")
    os.append("Name: " + name + ", ")
    os.append("ParamSigList: " + paramSigList.toString() + ", ")
    os.append("ActionTrigger: " + triggers.mkString(", "))
    os.append(")")

    // Return
    os.toString()
  }

}

case class ActionTrigger(val pattern:List[ActionExpr]) extends Positional {

  override def toString():String = {
    val os = new StringBuilder

    os.append("ActionTrigger(")
    os.append("Pattern: " + pattern)
    os.append(")")

    // Return
    os.toString()
  }
}

class ActionExpr() extends Positional {

}

case class ActionExprOR(val orElements:List[String]) extends ActionExpr {
  override def toString():String = return "ActionExprOR(orElements: " + orElements.mkString(",") + ")"
}

case class ActionExprIdentifier(val identifier:String) extends ActionExpr {
  override def toString():String = return "ActionExprIdentifier(identifier: " + identifier + ")"
}
