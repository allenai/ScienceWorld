package scienceworld.tasks

import scienceworld.tasks.goals.GoalSequence
import scienceworld.tasks.goals.specificgoals.{GoalChangeStateOfMatter, GoalIsNotStateOfMatter}

class Task {

}

object Task {

  // Test goal sequence: Change the state of some matter into gas
  def mkTaskChangeOfState():GoalSequence = {

    val goalSequence = new GoalSequence(Array(
      new GoalIsNotStateOfMatter("gas"),              // Be in any state but a gas
      new GoalChangeStateOfMatter("gas")          // Be in the gas state
    ))

    return goalSequence
  }

}