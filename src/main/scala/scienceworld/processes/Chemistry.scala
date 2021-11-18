package scienceworld.processes

import scienceworld.objects.SaltWater
import scienceworld.struct.EnvObject

class Chemistry {

}

object Chemistry {

  // Try to find a substance in a container
  private def getSubstance(container:EnvObject, substanceName:String, stateOfMatter:String = ""):Option[EnvObject] = {
    for (cObj <- container.getContainedObjects()) {
      if (cObj.name == substanceName) {
        if (stateOfMatter.length == 0) return Some(cObj)
        if ((cObj.propMaterial.isDefined) && (cObj.propMaterial.get.stateOfMatter == stateOfMatter)) return Some(cObj)
      }
      if ((cObj.propMaterial.isDefined) && (cObj.propMaterial.get.substanceName == substanceName)) {
        if (cObj.propMaterial.get.stateOfMatter == stateOfMatter) return Some(cObj)
      }
    }
    // If we reach here, no matches were found
    return None
  }


  // Mix the contents of a container
  def mixContainer(container:EnvObject):(Boolean, String) = {
    val contents = container.getContainedObjectsNotHidden()

    print("MIX: Contents: " + contents.map(_.toString()).mkString("\n"))

    // Case: Zero substances
    if (contents.size == 0) {
      return (false, "That container is empty, so there are no items to mix.")
    }

    // Case: One substance
    if (contents.size == 1) {
      val item = contents.toArray.last
      return (false, "There is only one thing (" + item.getDescriptName() + ")")
    }

    val water = this.getSubstance(container, "water", "liquid")
    val sodium = this.getSubstance(container, "sodium", "solid")
    val sodiumChloride = this.getSubstance(container, substanceName = "sodium chloride", "solid")
    val soap = this.getSubstance(container, "soap")

    // Case: Two substances
    if (contents.size == 2) {

      if ((water.isDefined) && (sodiumChloride.isDefined)) {
        // Salt Water
        water.get.delete()
        sodiumChloride.get.delete()

        val saltWater = new SaltWater()
        container.addObject(saltWater)

        return (true, "Sodium chloride and water mix to produce salt water.")
      }

    }


    return (false, "Mixing the contents of the " + container.getDescriptName() + " does not appear to produce anything new.")
  }

}
