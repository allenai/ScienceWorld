package scienceworld.tasks.goals

import scienceworld.struct.EnvObject

import scala.collection.mutable

// MaxSize represents the maximum number of objects that can be simultaneously monitored
class ObjMonitor(val maxSize:Int = 1) {
  val monitoredObjects = mutable.Set[EnvObject]()

  /*
   * Accessors
   */
  def clearMonitoredObjects() = { this.monitoredObjects.clear() }

  def getMonitoredObjects():Set[EnvObject] = this.monitoredObjects.toSet

  def addMonitor(obj:EnvObject): Boolean = {
    if (this.monitoredObjects.size >= maxSize) return false
    this.monitoredObjects.add(obj)
    return true
  }

  def removeMonitor(obj:EnvObject): Unit = {
    this.monitoredObjects.remove(obj)
  }


  /*
   * String methods
   */
  override def toString():String = {
    val os = new StringBuilder

    val objs = monitoredObjects.toArray
    os.append("Monitored Objects:\n")
    for (i <- 0 until objs.length) {
      os.append("\t" + i + ":\t" + objs(i).toStringMinimal())
    }

    // Return
    os.toString
  }
}
