package scienceworld

import scienceworld.Objects.devices.Sink
import scienceworld.struct.EnvObject

class EntryPoint {

}

object EntryPoint {

  def main(args:Array[String]) = {
    println("Initializing... ")

    val obj = new EnvObject()

    println(obj.toString())


    val sink = new Sink()

    println(sink.getDescription())

  }

}
