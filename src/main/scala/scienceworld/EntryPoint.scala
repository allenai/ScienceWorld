package scienceworld

import scienceworld.struct.EnvObject

class EntryPoint {

}

object EntryPoint {

  def main(args:Array[String]) = {
    println("Initializing... ")

    val obj = new EnvObject()

    println(obj.toString())

  }

}
