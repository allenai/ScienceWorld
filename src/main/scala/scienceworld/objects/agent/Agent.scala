package scienceworld.objects.agent

import scienceworld.objects.Orange
import scienceworld.objects.containers.Container
import scienceworld.properties.{IsOpenUnclosableContainer, MoveableProperties}
import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._



class Agent extends EnvObject {
  this.name = "agent"

  this.propMoveable = Some( new MoveableProperties(isMovable = false) )

  // Waiting (e.g. from wait command)
  var waitingCounter:Int = 0

  // Inventory
  val inventory = new InventoryContainer()
  this.addObject(inventory)

  /*
   * Inventory methods
   */
  def getInventoryContainer():EnvObject = this.inventory

  //## debug
  this.inventory.addObject( new Orange() )


  /*
   * Waiting methods
   */
  def setWait(numIterations:Int) = {
    waitingCounter = numIterations
  }

  def decrementWait(): Unit = {
    if (this.waitingCounter > 0) {
      this.waitingCounter -= 1
    }
  }

  def isWaiting():Boolean = {
    if (this.waitingCounter > 0) return true
    // Default return
    return false
  }



  /*
   * Standard methods
   */

  override def tick(): Boolean = {
    // Decrease waiting time (if agent is waiting)
    if (this.isWaiting()) {
      this.decrementWait()
    }


    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("agent", "self")
  }

  override def getDescription(mode:Int): String = {
    return ("the agent")
  }

}




class InventoryContainer extends Container {
  this.name = "inventory"

  this.propContainer = Some( new IsOpenUnclosableContainer )


  override def tick(): Boolean = {
    super.tick()
  }

  override def getReferents(): Set[String] = {
    Set("inventory", this.name)
  }

  override def getDescription(mode:Int): String = {
    super.getDescription(mode = MODE_DETAILED)
  }


}