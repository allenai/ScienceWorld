package processworld.Logger

import java.io.PrintWriter

import processworld.struct.Object
import processworld.Visualization.Visualize

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import sys.process._

/*
 * A logger that allows segments (for each global 'tick' in the simulation)
 */

object Logger {
  // Constructor
  val log = new ArrayBuffer[ArrayBuffer[String]]()
  val visualization = new ArrayBuffer[String]()
  val variableValues = new ArrayBuffer[mutable.Map[String, Map[String, String]]]

  newSegment()


  // Segments
  def newSegment(): Unit = {
    log.append( new ArrayBuffer[String] )
    visualization.append("")
    variableValues.append(mutable.Map[String, Map[String, String]]())
  }

  def numSegments(): Int = {
    return log.length
  }


  // Adding to log
  def add(logStr:String): Unit = {
    log.last.append(logStr)
  }

  def addVisualization(dotStr:String) = {
    visualization(visualization.length-1) = dotStr
  }

  def addVariables(root:Object): Unit = {
    // This object
    val key = root.getNameString()
    variableValues(variableValues.length-1)(key) = root.getAllVarValues()

    // Recurse through contents
    for (cObj <- root.contents) {
      addVariables(cObj)
    }

    //println("addVariables(" + (variableValues.length-1) + "): " + variableValues.last.toString())
  }

  // Get a list of objects that were in segmentIdx-1 but are no longer in segmentIdx
  def getDestroyedObjects(segmentIdx:Int): Array[String] = {
    val missingObjects = mutable.Set[String]()

    val variablesBefore = getVariables(segmentIdx-1)
    val variablesNow = getVariables(segmentIdx)

    for (objName <- variablesBefore.keySet) {
      if (!variablesNow.contains(objName)) {
        missingObjects.add(objName)
      }
    }

    // Return
    missingObjects.toArray.sorted
  }

  // Get a list of objects that are in segmentIdx but were not in segmentIdx-1
  def getCreatedObjects(segmentIdx:Int): Array[String] = {
    val createdObjects = mutable.Set[String]()

    val variablesBefore = getVariables(segmentIdx-1)
    val variablesNow = getVariables(segmentIdx)

    for (objName <- variablesNow.keySet) {
      if (!variablesBefore.contains(objName)) {
        createdObjects.add(objName)
      }
    }

    // Return
    createdObjects.toArray.sorted
  }

  // Showing log
  def show(lastN:Int = 0):String = {
    val logFlattened = log.flatten

    // Case: Show entire log
    if (lastN == 0) {
      return logFlattened.mkString("\n")
    }

    // Case: Show last N lines
    val max = logFlattened.length
    var min = math.min(logFlattened.length - lastN, 0)
    return logFlattened.slice(min, max).mkString("\n")
  }

  def getSegment(segmentIdx:Int): Array[String] = {
    return log(segmentIdx).toArray
  }

  def getVisualiation(segmentIdx:Int): String = {
    visualization(segmentIdx)
  }

  def getVariableValue(segmentIdx:Int, obj:Object, varName:String): Unit = {
    variableValues(segmentIdx)(obj.getNameString())(varName)
  }

  def getVariables(segmentIdx:Int):Map[String, Map[String, String]] = {
    if ((segmentIdx >= 0) && (segmentIdx < variableValues.length)) {
      return variableValues(segmentIdx).toMap
    }

    // Default return
    Map.empty[String, Map[String, String]]
  }

  // HTML/Dot export
  def exportDOTHTML(path:String): Unit = {
    val htmlOut = new StringBuilder
    val filenameTempDOT = path + "/temp.dot"

    println ("* exportDOTHTML(): Started... (path = " + path + ")")

    // Step 0: HTML Preamble
    htmlOut.append("<html><head><title>export</title></head>\n")
    htmlOut.append("<body>\n")

    // Step 1: Make path (if it doesn't already exist)
    val cmdMkdir = "mkdir " + path
    cmdMkdir.!

    // Step 2: Export DOT segments
    for (segIdx <- 0 until numSegments()) {
      htmlOut.append("<h2>Segment " + segIdx + "</h2>\n")

      val pw = new PrintWriter(filenameTempDOT)
      pw.println(getVisualiation(segIdx))
      pw.close()

      //val filenameOut = "vis-seg-" + segIdx + ".png"
      //Visualize.runDOTPNG(filenameTempDOT, path + "/" + filenameOut)
      val filenameOut = "vis-seg-" + segIdx + ".svg"
      Visualize.runDOTSVG(filenameTempDOT, path + "/" + filenameOut)

      htmlOut.append("<img src=\"" + filenameOut + "\"> <br>\n" )

      htmlOut.append("<b>Destroyed Objects:</b> <font color=\"red\">" + getDestroyedObjects(segIdx).mkString(", ") + "</font><br>\n")
      htmlOut.append("<b>Created Objects:</b> <font color=\"green\">" + getCreatedObjects(segIdx).mkString(", ") + "</font><br>\n")

      htmlOut.append("<ol>\n")
      for (line <- getSegment(segIdx)) {
        htmlOut.append("\t<li>" + line + "</li>\n")
      }
      htmlOut.append("</ol>\n")

      htmlOut.append("<br><br><br> <hr> <br> \n")
    }

    // Step 3: Export HTML
    htmlOut.append("</body>\n")
    htmlOut.append("</html>\n")

    val pw = new PrintWriter(path + "/index.html")
    pw.println(htmlOut)
    pw.close()


  }

}
