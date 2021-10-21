package scienceworld.objects.agent

import scienceworld.struct.EnvObject



class Agent extends EnvObject {
  this.name = "agent"

  // Waiting (e.g. from wait command)
  var waitingCounter:Int = 0

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
