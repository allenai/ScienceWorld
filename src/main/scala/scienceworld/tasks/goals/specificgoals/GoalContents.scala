package scienceworld.tasks.goals.specificgoals

import scienceworld.objects.livingthing.LivingThing
import scienceworld.objects.livingthing.animals.Animal
import scienceworld.objects.livingthing.plant.Plant
import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.{Goal, GoalReturn}

/*
 * Focus on specific classes of objects
 */

class GoalFocusOnLivingThing() extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):GoalReturn = {
    if (obj.isInstanceOf[LivingThing]) {
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    }

    // If we've focused on something different, the task has failed
    return GoalReturn.mkTaskFailure()
  }

}

class GoalFocusOnNonlivingThing() extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):GoalReturn = {
    if (!obj.isInstanceOf[LivingThing]) {
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    }

    // If we've focused on something different, the task has failed
    return GoalReturn.mkTaskFailure()
  }

}



class GoalFocusOnAnimal() extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):GoalReturn = {
    if (obj.isInstanceOf[Animal]) {
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    }

    // If we've focused on something different, the task has failed
    return GoalReturn.mkTaskFailure()
  }

}


class GoalFocusOnPlant() extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):GoalReturn = {
    if (obj.isInstanceOf[Plant]) {
      this.satisfiedWithObject = Some(obj)
      return GoalReturn.mkSubgoalSuccess()
    }

    // If we've focused on something different, the task has failed
    return GoalReturn.mkTaskFailure()
  }

}





/*
 * Check to see if the focus object has been placed in a specific container
 */

// Object must be in the container
// If the focus object is in one of the containers listed in 'failureContainers', then it causes task failure.
class GoalObjectInDirectContainer(containerName:String = "", failureContainers:List[EnvObject] = List.empty[EnvObject]) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):GoalReturn = {
    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Check if the object is in one of the incorrect (failure) containers that would cause task failure
    if (obj.getContainer().isDefined) {
      for (failContainer <- failureContainers) {
        if (obj.getContainer().get.name == failContainer.name) return GoalReturn.mkTaskFailure()
      }
    }

    // Check that the object's container name is set to the desired container
    if (obj.getContainer().isEmpty) return GoalReturn.mkSubgoalUnsuccessful()
    if (obj.getContainer().get.name.toLowerCase != containerName.toLowerCase) return GoalReturn.mkSubgoalUnsuccessful()


    // If we reach here, the condition is satisfied
    this.satisfiedWithObject = Some(obj)
    return GoalReturn.mkSubgoalSuccess()
  }

}


// Object could be in the container, or a container within that container
// If the focus object is in one of the containers listed in 'failureContainers', then it causes task failure.
class GoalObjectInContainer(containerName:String = "", failureContainers:List[EnvObject] = List.empty[EnvObject]) extends Goal {

  override def isGoalConditionSatisfied(obj:EnvObject, lastGoal:Option[Goal]):GoalReturn = {
    // Check that the focus object of this step is the same as the focus object of the previous step
    if (lastGoal.isDefined) {
      if (lastGoal.get.satisfiedWithObject.get != obj) return GoalReturn.mkSubgoalUnsuccessful()
    }

    // Check if the object is in one of the incorrect (failure) containers that would cause task failure
    if (obj.getContainer().isDefined) {
      for (failContainer <- failureContainers) {
        // First, start at the direct container
        var objContainer:Option[EnvObject] = obj.getContainer()
        // Also check recursive containers by recusing down to object tree root
        while (objContainer.isDefined) {
          if (objContainer.get.name == failContainer.name) {
            return GoalReturn.mkTaskFailure()
          }
          objContainer = objContainer.get.getContainer()
        }
      }
    }

    // Check that the object's container name is set to the desired container
    var container:Option[EnvObject] = obj.getContainer()
    while (container.isDefined) {
      // Check to see if this container is the query container
      if (container.get.name.toLowerCase == containerName.toLowerCase()) {
        this.satisfiedWithObject = Some(obj)
        return GoalReturn.mkSubgoalSuccess()
      }

      // If not, recurse
      container = container.get.getContainer()
    }

    // If we reach here, the condition was not satisfied
    return GoalReturn.mkSubgoalUnsuccessful()
  }

}
