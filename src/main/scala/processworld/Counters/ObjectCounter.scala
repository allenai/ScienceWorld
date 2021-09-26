package processworld.Counters

/*
 * Static object that generates a unique object ID for each object that's created.
 */


object ObjectCounter {
  var objIdx:Long = 0

  def getNextIdx():Long = {
    objIdx += 1
    return objIdx
  }

}
