package processworld.Visualization

import processworld.struct.Object

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._
import sys.process._


object Visualize {
  var nodeCounter:Int = 0

  def visualizeObjectDOT(root:Object, varValuesSegment:Map[String, Map[String, String]]=Map.empty[String, Map[String, String]], createdObjects:Array[String]): String = {
    val os = new StringBuilder

    os.append("digraph G { \n")
    os.append("\tcompound = true; \n")

    nodeCounter = 0
    val groupedObjects = getObjectsGroupedContents(root)
    os.append( mkObjectDOT(root, varValuesSegment, createdObjects, groupedObjects, indentLevel = 1) )

    os.append("} \n")

    //println (" * visualizeObjectDOT: varValuesSegment: " + varValuesSegment.toString())

    // Return
    os.toString()
  }

  private def mkObjectDOT(obj:Object, varValuesSegment:Map[String, Map[String, String]]=Map.empty[String, Map[String, String]], createdObjects:Array[String] = Array.empty[String], groupedObjects:Array[Array[Object]] = Array.empty[Array[Object]], indentLevel:Int=0, quantityMarker:Int=1): String = {
    val os = new StringBuilder
    val indent = "\t"*indentLevel

    //println(" * mkObjectDOT(): Started... (" + obj.getNameString() + ")")

    os.append(indent + "subgraph cluster_" + obj.objectName.replaceAll(" ", "_") + "_" + obj.uniqueIdx + " { \n")

    // Label
    os.append(indent + "\tlabel=< " )
    os.append("<TABLE CELLPADDING=\"2\" CELLSPACING=\"0\" CELLBORDER=\"1\" SIDES=\"LRTB\">")

    // Object Name/ID
    var objColour = "dodgerblue"
    if (obj.objectName.toLowerCase.startsWith("agent")) objColour = "orchid"          // Colour agents purple
    //if (!varValuesSegment.contains(obj.getNameString())) objColour = "limegreen"      // Colour new objects green
    if (createdObjects.contains( obj.getNameString() ) ) objColour = "limegreen"
    //println("\tgroupedObjects.length: " + groupedObjects.length)
    breakable {                                                                       // Colour new stacked objects green, if one of the objects in the stack is new
      for (cObjs <- groupedObjects) {
        val cObj = cObjs(0)
        //println("cObj: " + cObj.getNameString() + " (cObjs: " + cObjs.map(_.getNameString()).mkString(",") + ") / " + obj.getNameString())
        if (cObj.getNameString() == obj.getNameString()) {
          //println("createdObjects: " + createdObjects.mkString(", "))
          //println("cObjs: " + cObjs.map(_.getNameString()).mkString(", "))
          for (createdObject <- createdObjects) {
            if (cObjs.map(_.getNameString()).contains(createdObject)) {
              objColour = "limegreen"
              break()
            }
          }
        }
      }
    }


    if (quantityMarker == 1) {
      os.append("<TR><TD COLSPAN=\"2\" BGCOLOR=\"" + objColour + "\"> <b>" + obj.objectName + " (" + obj.uniqueIdx + ") " + createdObjects.mkString(",") + "</b></TD></TR>")
    } else {
      os.append("<TR><TD COLSPAN=\"2\" BGCOLOR=\"" + objColour + "\"> <b>[" + quantityMarker + "x] " + obj.objectName + "</b></TD></TR>")
    }

    // Table of properties
    val allVarValues = obj.getAllVarValues()
    for (key <- allVarValues.keySet.toArray.sorted) {
      // colStr: Highlight if this variable value has changed since the last segment
      var colStr = " BGCOLOR=\"greenyellow\""
      if (varValuesSegment.contains(obj.getNameString())) {                           // Safety
        if (varValuesSegment(obj.getNameString()).contains(key)) {                    // Safety
          if (allVarValues(key) == varValuesSegment(obj.getNameString())(key)) {      // If the variable values are the same, do not highlight
            colStr = ""
          }
        }
      }

      os.append("<TR><TD SIDES=\"B\" " + colStr +">" + key + "</TD><TD SIDES=\"B\">" + allVarValues(key) + "</TD></TR>")
    }
    os.append("</TABLE>")

    os.append(" > \n")

    //os.append("\n")
    if (obj.contents.length > 0) {
      //for (cObj <- obj.contents.sortBy(_.getNameString()).reverse) {
      for (cObjs <- getObjectsGroupedContents(obj)) {
        val quantity = cObjs.length     // Number of objects in this group
        val cObj = cObjs(0)             // Take first object of the group to be representative

        val groupedObjects_ = getObjectsGroupedContents(obj)
        os.append(mkObjectDOT(cObj, varValuesSegment, createdObjects, groupedObjects_, indentLevel + 1, quantity))
      }
    } else {
      os.append(indent + "\tn" + nodeCounter + ";\n")
      nodeCounter += 1
    }
    //os.append("\n")

    os.append(indent + "} \n")

    // Return
    os.toString()
  }

  def runDOTPNG(filenameDOT:String, filenameOut:String): Boolean = {
    val command = "dot -Tpng " + filenameDOT + " -o " + filenameOut
    val exitStatus = command.!

    if (exitStatus == 0) return true      // Success
    // Default return
    false
  }

  def runDOTSVG(filenameDOT:String, filenameOut:String): Boolean = {
    val command = "dot -Tsvg " + filenameDOT + " -o " + filenameOut
    val exitStatus = command.!

    if (exitStatus == 0) return true      // Success
    // Default return
    false
  }

  // Group objects by having identical names/properties, so that we don't draw multiple copies of identical objects in the DOT visualization
  private def getObjectsGroupedContents(obj:Object): Array[Array[Object]] = {
    val groupedObjs = new ArrayBuffer[ArrayBuffer[Object]]

    // Step 1: Group objects by having the same name/properties
    for (cObj <- obj.contents) {
      breakable {
        for (i <- 0 until groupedObjs.length) {
          // Compare the objects -- make sure they're equal by name/properties, and don't contain any other objects
          if ((cObj.isEqualByProperties(groupedObjs(i)(0))) && (cObj.contents.length == 0) && (groupedObjs(i)(0).contents.length == 0)) {
            // Object is the same, add it to the group
            groupedObjs(i).append(cObj)
            break()
          }
        }
        // If we reach here, the object is unique
        val newGroup = new ArrayBuffer[Object]
        newGroup.append(cObj)
        groupedObjs.append(newGroup)
      }
    }

    // Step 2: Pack into a immutable structure
    val out = new ArrayBuffer[Array[Object]]
    for (group <- groupedObjs) {
      out.append(group.toArray)
    }

    // Return (sorted)
    out.toArray.sortBy(_.last.getNameString()).reverse
  }

}

