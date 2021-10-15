package scienceworld.processes.lifestage

import scienceworld.struct.EnvObject

import scala.collection.mutable.ArrayBuffer


/*
 * Life Cycle
 */

class LifeCycle(lifeCycleName:String = "life cycle of the X organism") {
  val stages = new ArrayBuffer[LifeStage]()
  var curStage:Int = 0

  // Add a stage
  // if 'isDefault' is set to true, the added stage becomes the current stage
  def addStage(stage:LifeStage, isDefault:Boolean = false): Unit = {
    this.stages.append(stage)
    if (isDefault) {
      this.curStage = this.stages.length
    }
  }

  // Get the current stage
  def getCurStage():LifeStage = {
    this.stages(curStage)
  }

  // Get the current stage name (e.g. for descriptions)
  def getCurStageName():String = this.getCurStage().stageName


  // Change the current stage to a new stage with a given name
  def changeStage(stageName:String): Unit = {
    for (i <- 0 until stages.length) {
      if (stages(i).stageName == stageName) {
        println("* Living Thing is changing life cycle stage from (" + getCurStage().stageName + ") to (" + stageName + ")")      // Debug statement
        this.curStage = i
      }
    }
    // If we reach here, the stage wasn't found
    throw new RuntimeException("ERROR: Stage (" + stageName + ") not found in life cycle (" + this.lifeCycleName + ")")
  }


  /*
   * Tick
   */
  def tick(): Unit = {
    println ("lifeCycle: tick! " + this.getCurStage().stageName)

    this.getCurStage().tick()
  }

}

/*
 * Life stage (prototype)
 */
class LifeStage(val stageName: String, obj:EnvObject, lifecycle:LifeCycle) {
  // Is this life stage completed?
  private var _isCompleted: Boolean = false
  // How many ticks has this life stage been active so far (if not completed), or total (if completed)?
  private var durationTicks: Int = 0

  // Accessors
  def isCompleted(): Boolean = this._isCompleted
  def duration(): Int = this.durationTicks
  def incrementDuration() { this.durationTicks += 1 }

  // Tick
  def tick(): Unit = {
    // Check if needs are met
    // Increment life stage, if needed
  }


  /*
   * Helper functions
   */

  // Check that the container that this object is in has an object of a specific name.
  // e.g. check that the object is in the same container as some water
  def checkContainerHas(objName:String):(Boolean, Option[EnvObject]) = {
    // Step 1: Check that the object is in a container
    val container = obj.getContainer()
    if (container.isEmpty) return (false, None)

    // Step 2: Check to see if the container has an object with a given name
    // TODO: Should this be an object with a specific type?
    for (obj <- container.get.getContainedObjects()) {
      if (obj.name == objName) return (true, Some(obj))     // An object meeting the criteria has been found
    }

    // Default return
    // If we reach here, an object meeting the criteria wasn't found
    return (false, None)
  }

}


