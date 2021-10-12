package scienceworld.runtime.pythonapi

// A storage class that mirrors the TemplateAction class in Jericho
class TemplateAction(val actionString:String, val templateID:Int, val objectIDs:List[Int]) {

  def toJSON():String = {
    return ("{ \"action\":\"" + actionString + "\", \"template_id\":" + templateID + ", \"obj_ids\":[" + objectIDs.mkString(", ") + "] }")
  }

}
