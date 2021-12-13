package scienceworld.tasks.goals.specificgoals

import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn}


class GoalContainerByTemperature(tempThreshold:Double, containerNameAbove:String = "", containerNameBelow:String = "") extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):GoalReturn = {
    println ("## --")
    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    println ("## 01")

    // Get the object's current temperature
    if (obj.propMaterial.isEmpty) return GoalReturn.mkSubgoalUnsuccessful()
    val objTemp = obj.propMaterial.get.temperatureC

    println ("## 0")

    // Get the object's current container
    val curContainer = obj.getContainer()
    if (curContainer.isEmpty) return GoalReturn.mkSubgoalUnsuccessful()

    println ("obj: " + obj.name)
    println ("objTemp: " + objTemp)
    println ("container: " + curContainer.get.name)
    println ("containerAbove: " + containerNameAbove)
    println ("containerBelow: " + containerNameBelow)

    // If the container is not one of the two answer containers, then exit
    if ((curContainer.get.name != containerNameAbove) && (curContainer.get.name != containerNameBelow)) return GoalReturn.mkSubgoalUnsuccessful()

    println ("## 1")

    // Success conditions
    if ((curContainer.get.name == containerNameAbove) && (objTemp >= tempThreshold)) {
      println ("## 2")
      // Above temp container
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()

    }
    if ((curContainer.get.name == containerNameBelow) && (objTemp <= tempThreshold)) {
      println ("## 3")
      // Below temp container
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    }

    println ("## 4")

    // If we reach here, then the object is in one of the two answer containers, but not the correct one.
    return GoalReturn.mkTaskFailure()
  }

}