package processworld.Counters

/*
 * Static object that generates a unique object ID for each object that's created.
 */


object TickCounter {
  private var tickIdx:Int = 0

  def getCurrentIdx():Int = {
    tickIdx
  }

  def getNextIdx():Int = {
    tickIdx += 1
    return tickIdx
  }

}

