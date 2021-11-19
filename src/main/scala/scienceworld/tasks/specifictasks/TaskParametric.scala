package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskModifier, TaskValueStr}

trait TaskParametric {
  val taskName:String

  def numCombinations():Int
  def getCombination(idx:Int):Array[TaskModifier]

  //def setupCombination(modifiers:Array[TaskModifier], universe:EnvObject, agent:Agent)
  def setupCombination(combinationNum:Int, universe:EnvObject, agent:Agent): (Boolean, String)

  def setupGoals(combinationNum:Int): Task

  /*
   * Helpers
   */

  // Search the TaskModifiers for a TaskValueStr (key/value pair), that might include additional info (such as the answer) to supply to the goal conditions.
  def getTaskValueStr(modifiers:Array[TaskModifier], key:String):Option[String] = {
    // TODO
    for (mod <- modifiers) {
      mod match {
        case m:TaskValueStr => {
          if (m.key == key) return Some(m.value)
        }
        case _ => { }
      }
    }
    // If we reach here, the key wasn't found
    return None
  }

}
