package scienceworld.runtime

import main.scala.scienceworld.runtime.SimplifierProcessor
import scienceworld.input.InputParser
import scienceworld.runtime.pythonapi.PythonInterface

import scala.util.control.Breaks.{break, breakable}
import collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

object StatelessRunner {
  /*
   * Main
   */

  def printUsage(): Unit = {
    println("Usage: ")
    println("Argument 1: Task index (valid range 0-29)")
    println("Argument 2: Variation index (range varies depending on task, typically at least 0-10 will be valid)")
    println("Argument 3: Action sequence, consisting of comma-delimited actions.  Actions can only be alphanumeric, and spaces must be replaced with + symbols. e.g. 'look+around,open+door,go+through+door,eat+apple'")
  }

  def parseArgs(args:Array[String]): (Int, Int, Array[String]) = {

    // Check for correct number of arguments
    if (args.length != 3) {
      printUsage()
      sys.exit(1)
    }

    // Parse task index
    var taskIdx:Int = 0
    try {
      taskIdx = args(0).toInt
    } catch {
      case e:Exception => {
        println("Error when parsing argument 1 (task index):\n " + e.toString)
        printUsage()
        sys.exit(1)
      }
    }

    // Parse variation index
    var varIdx:Int = 0
    try {
      varIdx = args(1).toInt
    } catch {
      case e:Exception => {
        println("Error when parsing argument 2 (variation index):\n " + e.toString)
        printUsage()
        sys.exit(1)
      }
    }

    // Parse action sequence
    var userInputSequence = Array.empty[String]
    try {
      val sanitizedStr = args(2).replaceAll("\\+", " ")
      userInputSequence = sanitizedStr.split(",")
    } catch {
      case e:Exception => {
        println("Error when parsing argument 3 (user input sequence):\n " + e.toString)
        printUsage()
        sys.exit(1)
      }
    }

    // Return
    return (taskIdx, varIdx, userInputSequence)
  }

  def main(args:Array[String]) = {
    println("Initializing... ")

    //val taskIdx:Int = 13
    //val varIdx:Int = 10
    val simplificationStr = "easy"

    // Parse task index, variation index, and user input strings from command line
    val (taskIdx, varIdx, userInputStrings) = parseArgs(args)

    // Create new agent interface
    val interface = new PythonInterface()

    val startTime = System.currentTimeMillis()

    println ("TASK LIST: ")
    val taskList = interface.getTaskNames().asScala
    for (i <- 0 until taskList.size) {
      val taskName = taskList(i)
      val numVariations = interface.getTaskMaxVariations(taskName)
      println( i.formatted("%5s") + ": \t" + taskName.formatted("%60s") + "  (" + numVariations + " variations)")
    }

    // Pick a task
    val taskName = interface.getTaskNames().asScala(taskIdx)

    // Load task
    interface.load(taskName, variationIdx = varIdx, simplificationStr)

    println ("Task: " + interface.getTaskDescription() )

    // Simplifications
    println ("Possible simplifications: " + SimplifierProcessor.getPossibleSimplifications())

    // DEBUG: Set the task/goals
    var curIter:Int = 0

    // User input
    //val userInputStrings = Array("look around", "eat orange", "open door", "0")

    breakable {
      var userInputString:String = "look around"

      for (userInputString <- userInputStrings) {
        curIter = interface.getNumMoves()
        println("")
        println("---------------------------------------")
        println(" Iteration " + curIter)
        println("---------------------------------------")
        println("")

        // Process step in environment
        //val (description, score, isCompleted) = agentInterface.step(userInputString)
        val description = interface.step(userInputString)
        val score = interface.getScore()
        val isCompleted = interface.isComplete

        println("")
        println("Description: ")
        println(description)
        println("Score: " + score.formatted("%3.3f"))
        println("IsCompleted: " + isCompleted)
      }
    }

    val deltaTime = System.currentTimeMillis() - startTime
    println("Total execution time: " + deltaTime + " msec for " + curIter + " iterations (" + (curIter / (deltaTime.toDouble/1000.0f)) + " iterations/sec)")

    println ("")
    println ("Exiting...")

    println("JSON History:")
    println(interface.getRunHistoryJSON())

  }

}

