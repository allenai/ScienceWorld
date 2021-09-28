package scienceworld.processes

import scienceworld.struct.EnvObject

class StateOfMatter {

}

object StateOfMatter {


  def ChangeOfState(obj:EnvObject): Unit = {
    // Only continue if the object has material properties
    if (obj.propMaterial.isEmpty) return
    val objProp = obj.propMaterial.get

    val curStateOfMatter = objProp.stateOfMatter
    var inferredStateOfMatter = ""

    if (objProp.temperatureC < objProp.meltingPoint) {
      inferredStateOfMatter = "solid"
    } else if (objProp.temperatureC > objProp.boilingPoint) {
      inferredStateOfMatter = "gas"
    } else {
      inferredStateOfMatter = "liquid"
    }

    if (curStateOfMatter != inferredStateOfMatter) {
      println ("Changing object (" + obj.name + ") from state of matter (" + curStateOfMatter + ") to state (" + inferredStateOfMatter + ")")
      obj.propMaterial.get.stateOfMatter = inferredStateOfMatter
    }

  }


}
