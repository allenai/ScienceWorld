package scienceworld.tasks

import scienceworld.tasks.goals.GoalSequence
import scienceworld.tasks.goals.specificgoals.{GoalChangeStateOfMatter, GoalIsDifferentStateOfMatter, GoalIsNotStateOfMatter, GoalIsStateOfMatter}

class Task {

}

object Task {

  // Test goal sequence: Change the state of some matter into gas
  def mkTaskChangeOfStateGas():GoalSequence = {

    val description = "Your task is to boil a substance.  First, focus on a substance that is in solid or liquid form (e.g. 'focus <substanceName>').  Then, make changes to the environment that will cause it to boil.  When the substance changes to a gas state, the score will switch to 1.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalIsNotStateOfMatter("gas"),              // Be in any state but a gas
      new GoalChangeStateOfMatter("gas")          // Be in the gas state
    ), description)

    return goalSequence
  }


  // Test goal sequence: Change the state of some matter into gas
  def mkTaskChangeOfState():GoalSequence = {

    val description = "Your task is to change the state of matter of a substance.  First, focus on a substance.  Then, make changes that will cause it to change its state of matter.  To reset, type 'reset task'. "
    val goalSequence = new GoalSequence(Array(
      new GoalIsStateOfMatter(),              // Be in any state
      new GoalIsDifferentStateOfMatter()      // Be in any state but the first state
    ), description)

    return goalSequence
  }



}