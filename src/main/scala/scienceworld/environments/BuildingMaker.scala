package scienceworld.environments

import scienceworld.objects.devices.{Bathtub, Sink, Toilet}
import scienceworld.objects.location.{Location, Room}
import scienceworld.objects.misc.Picture
import scienceworld.struct.EnvObject

class BuildingMaker {

}

object BuildingMaker {


  /*
   * Bathroom
   */
  def mkBathroom(includeBathtub:Boolean = true, sewer:EnvObject): Location = {
    val room = new Room("bathroom")

    // Sink
    val sink = new Sink(drainsTo = Some(sewer))
    room.addObject(sink)

    // Toilet
    val toilet = new Toilet(drainsTo = sewer)
    room.addObject(toilet)

    // Bathtub
    if (includeBathtub) {
      val bathtub = new Bathtub(drainsTo = Some(sewer))
      room.addObject(bathtub)
    }


    // Decoration
    val randomPicture = Picture.mkRandom()
    room.addObject(randomPicture)


    // Return
    room
  }

}
