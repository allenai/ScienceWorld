package language.model

import scienceworld.struct.EnvObject

import scala.collection.mutable.ArrayBuffer
import scala.util.parsing.input.Positional

case class ActionRequestDef(val name:String, val paramSigList:ParamSigList, val triggers:List[ActionTrigger], val uniqueActionID:Int) extends Statement {

  def getVarType(varName:String):String = {
    paramSigList.getVarType(varName)
  }

  def mkHumanReadableExample():String = {
    if (triggers.length == 0) return ""
    return triggers(0).mkHumanReadableExample()
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

  def mkHumanReadableExample():String = {
    val out = new ArrayBuffer[String]

    for (elem <- pattern) {
      out.append(elem.mkHumanReadableExample())
    }

    out.mkString(" ")
  }

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
  def mkHumanReadableExample():String = return ""
}

case class ActionExprOR(val orElements:List[String]) extends ActionExpr {
  override def mkHumanReadableExample():String = {
    if (orElements.length == 0) return ""
    return orElements(0)      // Return first element
  }

  override def toString():String = return "ActionExprOR(orElements: " + orElements.mkString(",") + ")"
}

case class ActionExprIdentifier(val identifier:String) extends ActionExpr {
  override def mkHumanReadableExample():String = {
    return "OBJ"
  }

  override def toString():String = return "ActionExprIdentifier(identifier: " + identifier + ")"
}

// Storage class for holding an object match in PossibleAction
case class ActionExprObject(val obj:EnvObject, val referent:String) extends ActionExpr {
  override def mkHumanReadableExample(): String = {
    //return obj.name
    return referent
  }
}

// Storage class for holding specific text in PossibleAction
case class ActionExprText(val text:String) extends ActionExpr {
  override def mkHumanReadableExample(): String = {
    return text
  }
}