package scienceworld.input

import scienceworld.actions.{Action, ActionActivate, ActionCloseDoor, ActionDeactivate, ActionEat, ActionFlush, ActionFocus, ActionLookAround, ActionLookAt, ActionLookIn, ActionMoveObject, ActionMoveThroughDoor, ActionOpenDoor, ActionPourObject, ActionRead, ActionResetTask, ActionUseDevice}
import scienceworld.tasks.goals.{GoalSequence, ObjMonitor}

object ActionTypecaster {

  def typecastAction(actionIn:InputMatch, objMonitor:ObjMonitor, goalSequence:GoalSequence):Action = {
    val action = actionIn.actionRequestDef.get
    val assignments = actionIn.varLUT.toMap

    action.name match {
      case ActionFocus.ACTION_NAME => new ActionFocus(action, assignments, objMonitor)
      case ActionResetTask.ACTION_NAME => new ActionResetTask(action, assignments, objMonitor, goalSequence)

      case ActionOpenDoor.ACTION_NAME => new ActionOpenDoor(action, assignments)
      case ActionCloseDoor.ACTION_NAME => new ActionCloseDoor(action, assignments)
      case ActionMoveThroughDoor.ACTION_NAME => new ActionMoveThroughDoor(action, assignments)
      case ActionLookAround.ACTION_NAME => new ActionLookAround(action, assignments)
      case ActionLookAt.ACTION_NAME => new ActionLookAt(action, assignments)
      case ActionLookIn.ACTION_NAME => new ActionLookIn(action, assignments)
      case ActionActivate.ACTION_NAME => new ActionActivate(action, assignments)
      case ActionDeactivate.ACTION_NAME => new ActionDeactivate(action, assignments)
      case ActionEat.ACTION_NAME => new ActionEat(action, assignments)
      case ActionMoveObject.ACTION_NAME => new ActionMoveObject(action, assignments)
      case ActionPourObject.ACTION_NAME => new ActionPourObject(action, assignments)
      case ActionUseDevice.ACTION_NAME => new ActionUseDevice(action, assignments)
      case ActionRead.ACTION_NAME => new ActionRead(action, assignments)
      case ActionFlush.ACTION_NAME => new ActionFlush(action, assignments)

      case _ => throw new RuntimeException("ERROR: Unknown action name: " + action.name)
    }

  }

}
