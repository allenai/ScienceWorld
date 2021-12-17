package examples

import scienceworld.runtime.pythonapi.PythonInterface

import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.collection.mutable.ArrayBuffer
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

    // Simulation parameters
    val maxEpisodes:Int = 1000
    val maxIterPerEpisode:Int = 50
    var curEpisode:Int = 0

    var totalSteps:Long = 0

    var userInput = "look around"
    while (curEpisode < maxEpisodes) {

      // Choose a variation
      val maxVariations = interface.getTaskMaxVariations(taskName)
      val variationIdx = Random.nextInt(maxVariations)

      // Load the task/variation
      interface.load(taskName, variationIdx)

      // Get reference to AgentInterface
      val agentInterface = interface.agentInterface

      var curIter:Int = 0
      var actionHistory = new ArrayBuffer[String]()
      while (curIter < maxIterPerEpisode) {
        println("---------------------------")
        println("   Episode " + curEpisode + "  Iteration " + curIter + " / " + maxIterPerEpisode)
        println("   Total Steps: " + totalSteps)
        println("---------------------------")
        val observation = agentInterface.get.step(userInput)
        println(">> " + userInput)
        println("Observation: ")
        println(observation)

        actionHistory.append(userInput)


        // Mode 1: use getPossibleActions
        /*
        val (templates, uuidToRefLUT) = agentInterface.get.getPossibleActionObjectCombinations()
        val randIdx = Random.nextInt(templates.length)
        val randTemplate = templates(randIdx)
        //println (">>>>>>>>>>>> " + randTemplate.actionString )
        userInput = randTemplate.actionString
         */

        // Mode 2: Use getValidActions
        val validActions = agentInterface.get.getValidActionObjectCombinations()
        val randIdx = Random.nextInt(validActions.length)
        val randAction = validActions(randIdx)
        //println (">>>>>>>>>>>> " + randTemplate.actionString )
        userInput = randAction

        // Randomly pick a next action

        // Check for error state:
        if (agentInterface.get.isInErrorState()) {

          println("Action History:")
          for (i <- 0 until actionHistory.length) {
            println(i + ": \t" + actionHistory(i))
          }
          println("")
          println("ERROR STATE DETECTED!")
          println("ERROR MESSAGE: ")
          println(agentInterface.get.getErrorStateMessage())


          sys.exit(1)
        }

        curIter += 1
        totalSteps += 1
      }
      curEpisode += 1
    }


    println ("Completed...")

  }


}
