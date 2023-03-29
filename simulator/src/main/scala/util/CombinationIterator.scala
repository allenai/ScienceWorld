package main.scala.util

import scala.util.control.Breaks._

/**
  * Iterator for all combinations/permutations of a set of maxValue.size numbers, where each number has some range ( 0 to maxValue(i)-1 )
  * Created by peter on 10/14/17.
  */
class CombinationIterator(val maxValues:Array[Int]) {
  private val numValues = maxValues.length
  private val out = Array.fill[Int](numValues)(0)
  private var isComplete:Boolean = false
  var count:Long = 0
  val size:Long = calculateSize()

  // Returns true if there are additional patterns to iterate through
  def hasNext():Boolean = {
    if (count < size) return true
    // Return
    false
  }

  // Retrive the next pattern
  def next():Array[Int] = {
    if (count != 0) increment()
    count += 1
    out
  }

  // Increment the pattern
  private def increment(): Unit = {
    if (count >= size) return

    breakable {
      for (i <- (numValues-1) to 0 by -1) {
        if ( out(i) < (maxValues(i)-1) ) {
          out(i) += 1
          break()
        } else {
          out(i) = 0
        }
      }
      // If we reach here, then there are no more patterns to iterate through
      //isComplete = true
    }
  }


  private def calculateSize():Long = {
    var sum:Long = 1
    for (i <- 0 until numValues) {
      sum *= maxValues(i)
    }
    // Return
    sum
  }

}


object CombinationIterator {

  // Example of use
  def main(args:Array[String]): Unit = {
    val maxValues = Array(5, 2, 3)
    val iter = new CombinationIterator( maxValues )

    println ("size: " + iter.size)

    while (iter.hasNext()) {
      println ( iter.next().toList )
    }

  }

}
