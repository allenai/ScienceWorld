package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.struct.EnvObject
import scienceworld.tasks.Task

trait TaskParametric {
  val taskName:String

  def numCombinations():Int
  def getCombination(idx:Int):Array[TaskModifier]

  //def setupCombination(modifiers:Array[TaskModifier], universe:EnvObject, agent:Agent)
  def setupCombination(combinationNum:Int, universe:EnvObject, agent:Agent): (Boolean, String)

  def setupGoals(combinationNum:Int): Task

}
