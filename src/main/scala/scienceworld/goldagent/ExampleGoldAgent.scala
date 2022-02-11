package scienceworld.goldagent
import main.scala.scienceworld.runtime.SimplifierProcessor
import scienceworld.runtime.pythonapi.PythonInterface

import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._
import scala.util.Random


object ExampleGoldAgent {


  def main(args:Array[String]) = {
    val interface = new PythonInterface()

    val specificTasks = Array(11,12,13,14)           // Do specific tasks
    //val specificTasks = Array(13)           // Do specific tasks
    //val specificTasks = Array.empty[Int]      // Do all

    val simplificationStr = "easy"

    val errors = new ArrayBuffer[String]
    var numVariationsTested:Int = 0

    val errorHistories = new ArrayBuffer[RunHistory]


    // Create environment
    // Get a task name
    val taskNames = interface.getTaskNames().asScala.toList


    println ("TASK LIST: ")
    val taskList = interface.taskMaker.getTaskList()
    for (i <- 0 until taskList.length) {
      val taskName = taskList(i)
      val numVariations = interface.taskMaker.getMaxVariations(taskName)
      println( i.formatted("%5s") + ": \t" + taskName.formatted("%60s") + "  (" + numVariations + " variations)")
    }


    val taskScores = new ArrayBuffer[ Array[Double] ]()
    for (taskIdx <- 0 until taskNames.length) {
      breakable {
        val taskName = taskNames(taskIdx)
        // If we've specified specific tasks to run, skip this one
        if ((!specificTasks.isEmpty) && (!specificTasks.contains(taskIdx))) {
          taskScores.append(Array.empty[Double])
          break
        }


        // Simulation parameters
        var curEpisode: Int = 0

        var totalSteps: Long = 0

        var userInput = "look around"
        val episodeScores = new ArrayBuffer[Double]
        val maxTaskVariations = interface.getTaskMaxVariations(taskName)



        // For each variation
        for (variationIdx <- 0 until maxTaskVariations) {
          println("---------------------------")
          println("   Task " + taskIdx + "   " + taskName)
          println("   Variation: " + variationIdx + " / " + maxTaskVariations)
          println("---------------------------")

          // Load the task/variation
          interface.load(taskName, variationIdx, simplificationStr)

          // Get reference to AgentInterface
          val agentInterface = interface.agentInterface

          // Get the gold action sequence
          val goldActionSeq = interface.getGoldActionSequence().asScala.toArray

          // Create a history object to store this run
          val history = new RunHistory(taskName, taskIdx, variationIdx)

          var curScore: Double = 0.0
          for (actionIdx <- 0 until goldActionSeq.length) {


            // Get next gold action
            userInput = goldActionSeq(actionIdx)

            // Supply action to environment, get next environment observation
            println(">> " + userInput)
            val observation = agentInterface.get.step(userInput)
            curScore = observation._2
            println("Observation: ")
            println(observation)

            // Store in history
            history.addStep(userInput, observation)

            // Check for error state:
            if (agentInterface.get.isInErrorState()) {

              println("Action History:")
              for (i <- 0 to actionIdx) {
                println(i + ": \t" + goldActionSeq(i))
              }
              println("")
              println("ERROR STATE DETECTED!")
              println("ERROR MESSAGE: ")
              println(agentInterface.get.getErrorStateMessage())

              sys.exit(1)
            }

            totalSteps += 1
          }


          // If we reach here, we're done with the gold action sequence.  Test to make sure the score is perfect
          if (curScore < 1.0) {
            val errStr = "ERROR: Score not 100% after running gold action sequence (Task: " + taskName + ", VariationIdx: " + variationIdx + ", Score: " + curScore + ")"
            println(errStr)
            errors.append(errStr)
            println ("Gold sequence: " + goldActionSeq.mkString(", "))

            errorHistories.append(history)
          }
          episodeScores.append(curScore)
          numVariationsTested += 1
        }
        taskScores.append(episodeScores.toArray)


        // Show error histories
        println ("ERROR HISTORIES:")
        for (history <- errorHistories) {
          println(history.toString())
        }

        // Show summary statistics
        println("")
        println("---------------------------------")
        println("Scores:")
        println("Simplifications: " + SimplifierProcessor.getSimplificationsUsed())
        println("---------------------------------")
        println("Total number of variations tested: " + numVariationsTested )
        println("Total number of variations with errors in gold path: " + errors.length)
        println("---------------------------------")

        for (taskIdx <- 0 until taskScores.length) {
          val taskName = taskNames(taskIdx)
          println(taskIdx.formatted("%3s") + ": " + taskName.formatted("%60s") + "\t" + summaryStatistics(taskScores(taskIdx)))
        }

        println("---------------------------------")
      }
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


// Storage class for histories
class RunHistory(val taskName:String, val taskIdx:Int, val variationIdx:Int) {
  val historyActions = new ArrayBuffer[String]
  val historyObservations = new ArrayBuffer[(String, Double, Boolean)]

  def length:Int = this.historyActions.length

  def addStep(action:String, observation:(String, Double, Boolean)): Unit = {
    historyActions.append(action)
    historyObservations.append(observation)
  }

  override def toString():String = {
    val os = new StringBuilder

    os.append("\n------------------------------------------------------------------------\n\n")
    os.append("Task: " + taskName + "\n")
    os.append("TaskIdx: " + taskIdx + "\n")
    os.append("VariationIdx: " + variationIdx + "\n")
    os.append("\n")

    for (i <- 0 until this.length) {
      os.append(">>> " + historyActions(i) + "\n")
      os.append(historyObservations(i)._1 + "\n")
      os.append("\n")
    }

    os.append("Action history: " + historyActions.mkString(", ") + "\n")
    os.append("\n------------------------------------------------------------------------\n\n")

    os.toString()
  }

}