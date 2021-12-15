package examples

import scienceworld.runtime.pythonapi.PythonInterface

import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.util.Random

class ExampleRandomAgent {

}

object ExampleRandomAgent {


  def main(args:Array[String]) = {
    val interface = new PythonInterface()

    // Create environment
    // Get a task name
    val taskNames = interface.getTaskNames().asScala.toList
    val taskName = taskNames(0)

    // Choose a variation
    val maxVariations = interface.getTaskMaxVariations(taskName)
    val variationIdx = Random.nextInt(maxVariations)

    // Load the task/variation
    interface.load(taskName, variationIdx)

    // Get reference to AgentInterface
    val agentInterface = interface.agentInterface

    // Simulation parameters
    val maxIter:Int = 1000
    var curIter:Int = 0

    var userInput = "look around"
    while(curIter < maxIter) {
      println("---------------------------")
      println("   Iteration " + curIter + " / " + maxIter)
      println("---------------------------")
      val observation = agentInterface.get.step(userInput)
      println (">> " + userInput)
      println ("Observation: ")
      println (observation)

      val (templates, uuidToRefLUT) = agentInterface.get.getPossibleActionObjectCombinations()
      //## CRASH TEST
      val test1 = agentInterface.get.getValidActionObjectCombinations()

      // Randomly pick a next action
      val randIdx = Random.nextInt(templates.length)
      val randTemplate = templates(randIdx)
      //println (">>>>>>>>>>>> " + randTemplate.actionString )
      userInput = randTemplate.actionString

      // Check for error state:
      if (agentInterface.get.isInErrorState()) {
        println ("ERROR STATE DETECTED!")
        println ("ERROR MESSAGE: ")
        println (agentInterface.get.getErrorStateMessage())
      }

      curIter += 1
    }


    println ("Completed...")

  }


}
