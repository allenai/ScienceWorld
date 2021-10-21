package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.livingthing.LivingThing
import scienceworld.objects.livingthing.animals.Animal
import scienceworld.objects.livingthing.plant.Plant
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.Goal

/*
 * Focus on specific classes of objects
 */

class GoalFocusOnLivingThing() extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    if (obj.isInstanceOf[LivingThing]) {
      this.satisfiedWithObject = Some(obj)
      return true
    }

    return false
  }

}

class GoalFocusOnNonlivingThing() extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    if (!obj.isInstanceOf[LivingThing]) {
      this.satisfiedWithObject = Some(obj)
      return true
    }

    return false
  }

}



class GoalFocusOnAnimal() extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    if (obj.isInstanceOf[Animal]) {
      this.satisfiedWithObject = Some(obj)
      return true
    }

    return false
  }

}


class GoalFocusOnPlant() extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    if (obj.isInstanceOf[Plant]) {
      this.satisfiedWithObject = Some(obj)
      return true
    }

    return false
  }

}





/*
 * Check to see if the focus object has been placed in a specific container
 */

// Object must be in the container
class GoalObjectInDirectContainer(containerName:String = "") extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return false
    }

    // Check that the object's container name is set to the desired container
    if (obj.getContainer().isEmpty) return false
    if (obj.getContainer().get.name.toLowerCase != containerName.toLowerCase) return false


    // If we reach here, the condition is satisfied
    this.satisfiedWithObject = Some(obj)
    return true
  }

}


// Object could be in the container, or a container within that container
class GoalObjectInContainer(containerName:String = "") extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):Boolean = {
    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return false
    }

    // Check that the object's container name is set to the desired container
    var container:Option[EnvObject] = obj.getContainer()
    while (container.isDefined) {
      // Check to see if this container is the query container
      if (container.get.name.toLowerCase == containerName.toLowerCase()) {
        this.satisfiedWithObject = Some(obj)
        return true
      }

      // If not, recurse
      container = container.get.getContainer()
    }

    // If we reach here, the condition was not satisfied
    return false
  }

}
