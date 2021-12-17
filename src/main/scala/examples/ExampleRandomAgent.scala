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

    val maxEpisodes:Int = 100
    val maxIterPerEpisode:Int = 100

    // Create environment
    // Get a task name
    val taskNames = interface.getTaskNames().asScala.toList

    val taskScores = new ArrayBuffer[ Array[Double] ]()
    for (taskIdx <- 0 until taskNames.length) {
      val taskName = taskNames(taskIdx)

      // Simulation parameters
      var curEpisode: Int = 0

      var totalSteps: Long = 0

      var userInput = "look around"
      val episodeScores = new ArrayBuffer[Double]
      while (curEpisode < maxEpisodes) {

        // Choose a variation
        val maxVariations = interface.getTaskMaxVariations(taskName)
        val variationIdx = Random.nextInt(maxVariations)

        // Load the task/variation
        interface.load(taskName, variationIdx)

        // Get reference to AgentInterface
        val agentInterface = interface.agentInterface

        var curIter: Int = 0
        var actionHistory = new ArrayBuffer[String]()

        var curScore:Double = 0.0
        while (curIter < maxIterPerEpisode) {
          println("---------------------------")
          println("   Task " + taskIdx + "   " + taskName)
          println("   Episode " + curEpisode + "  Iteration " + curIter + " / " + maxIterPerEpisode)
          println("   Total Steps: " + totalSteps)
          println("---------------------------")
          
          println(">> " + userInput)
          val observation = agentInterface.get.step(userInput)
          curScore = observation._2
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
        episodeScores.append(curScore)
      }
      taskScores.append(episodeScores.toArray)

      println("")
      println("---------------------------------")
      println("Scores:")
      println("---------------------------------")
      println("maxEpisodes: " + maxEpisodes)
      println("maxIterPerEpisode: " + maxIterPerEpisode)
      println("equivalent steps per task: " + (maxEpisodes * maxIterPerEpisode) )
      println("---------------------------------")

      for (taskIdx <- 0 until taskScores.length) {
        val taskName = taskNames(taskIdx)
        println(taskIdx.formatted("%3s") + ": " + taskName.formatted("%60s") + "\t" + summaryStatistics(taskScores(taskIdx)))
      }

      println("---------------------------------")
    }



    println ("Completed...")

  }


  def summaryStatistics(in:Array[Double]):String = {
    val os = new StringBuilder
    if (in.length == 0) return "no samples"

    var min:Double = in(0)
    var max:Double = in(0)
    var avg:Double = 0.0

    for (elem <- in) {
      if (elem < min) min = elem
      if (elem > max) max = elem

      // NOTE: Not including negative scores in the average
      if (elem >= 0.0) {
        avg += elem
      }

    }

    avg = avg / in.length.toDouble

    os.append("min: " + min.formatted("%3.2f").formatted("%5s") + "      ")
    os.append("max: " + max.formatted("%3.2f").formatted("%5s") + "      ")
    os.append("avg: " + avg.formatted("%3.2f").formatted("%5s") + "      ")

    return os.toString()
  }

}
