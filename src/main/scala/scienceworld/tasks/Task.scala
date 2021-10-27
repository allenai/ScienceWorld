package scienceworld.tasks

import scienceworld.struct.EnvObject
import scienceworld.tasks.goals.GoalSequence
import scienceworld.tasks.goals.specificgoals.{GoalChangeStateOfMatter, GoalIsDifferentStateOfMatter, GoalIsNotStateOfMatter, GoalIsStateOfMatter}

class Task(val taskName:String, val description:String, val goalSequence:GoalSequence, val taskObjects:Array[EnvObject] = Array.empty[EnvObject]) {

}



object Task {

}