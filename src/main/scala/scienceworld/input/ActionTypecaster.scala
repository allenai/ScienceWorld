package scienceworld.input

import scienceworld.actions.{Action, ActionActivate, ActionCloseDoor, ActionDeactivate, ActionEat, ActionLookAround, ActionMoveObject, ActionMoveThroughDoor, ActionOpenDoor}

object ActionTypecaster {

  def typecastAction(actionIn:InputMatch):Action = {
    val action = actionIn.actionRequestDef.get
    val assignments = actionIn.varLUT.toMap

    action.name match {
      case ActionOpenDoor.ACTION_NAME => new ActionOpenDoor(action, assignments)
      case ActionCloseDoor.ACTION_NAME => new ActionCloseDoor(action, assignments)
      case ActionMoveThroughDoor.ACTION_NAME => new ActionMoveThroughDoor(action, assignments)
      case ActionLookAround.ACTION_NAME => new ActionLookAround(action, assignments)
      case ActionActivate.ACTION_NAME => new ActionActivate(action, assignments)
      case ActionDeactivate.ACTION_NAME => new ActionDeactivate(action, assignments)
      case ActionEat.ACTION_NAME => new ActionEat(action, assignments)
      case ActionMoveObject.ACTION_NAME => new ActionMoveObject(action, assignments)

      case _ => throw new RuntimeException("ERROR: Unknown action name: " + action.name)
    }

  }

}
