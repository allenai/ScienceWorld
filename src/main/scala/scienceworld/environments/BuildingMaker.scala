package scienceworld.environments

import scienceworld.objects.containers.{BookShelf, GlassCup}
import scienceworld.objects.containers.furniture.{Bed, Chair, Closet, Couch, Desk, Table}
import scienceworld.objects.devices.{Bathtub, Sink, Toilet}
import scienceworld.objects.location.{Location, Room}
import scienceworld.objects.misc.Picture
import scienceworld.struct.EnvObject

import scala.util.Random

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

    // Glass cup?
    val cup = new GlassCup()
    room.addObject(cup)


    // Decoration
    val randomPicture = Picture.mkRandom()
    room.addObject(randomPicture)


    // Return
    room
  }


  def mkBedroom():Location = {
    val room = new Room("bedroom")

    val bed = new Bed()
    room.addObject(bed)

    val table = new Table()
    room.addObject(table)

    val closet = new Closet()
    room.addObject(closet)

    // Decoration
    val randomPicture = Picture.mkRandom()
    room.addObject(randomPicture)

    // Bookshelf
    if (Random.nextBoolean() == true) {
      room.addObject( BookShelf.mkRandom())
    }

    // Return
    room
  }


  /*
   * Living Room
   */
  def mkLivingRoom():Location = {
    val room = new Room("Living Room")

    // Couch
    val couch = new Couch()
    room.addObject(couch)

    // Chair
    val chair = new Chair()
    room.addObject(chair)

    // Desk/Table?
    val table = mkRandomTable()
    room.addObject(table)

    // Decoration
    val randomPicture = Picture.mkRandom()
    room.addObject(randomPicture)

    // Bookshelf
    if (Random.nextBoolean() == true) {
      room.addObject( BookShelf.mkRandom())
    }


    // Return
    room
  }


  def mkRandomTable():EnvObject = {
    val numOptions = 2
    val randIdx = Random.nextInt(numOptions)

    if (randIdx == 0) return new Table()
    if (randIdx == 1) return new Desk()

    // Default
    return new Table()
  }


}
