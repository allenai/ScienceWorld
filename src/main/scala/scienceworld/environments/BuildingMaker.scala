package scienceworld.environments

import scienceworld.objects.{Apple, Banana, Orange, OrangeJuice, Potato, Water}
import scienceworld.objects.containers.{BookShelf, CeramicCup, GlassCup, MetalPot, Sewer, TinCup, WoodBowl, WoodCup}
import scienceworld.objects.containers.furniture.{Bed, Chair, Closet, Couch, Counter, Cupboard, Desk, Table}
import scienceworld.objects.devices.{Bathtub, Freezer, Fridge, Sink, Stove, Thermometer, Toilet}
import scienceworld.objects.location.{Location, Room, Universe}
import scienceworld.objects.misc.Picture
import scienceworld.objects.portal.Door
import scienceworld.struct.EnvObject

import scala.util.Random


object RoomMaker {

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


  /*
   * Kitchen
   */
  def mkKitchen(sewer:EnvObject):Location = {
    val room = new Room("Kitchen")

    // Objects

    // Major appliances/devices
    val sink = new Sink(drainsTo = Some(sewer))
    room.addObject(sink)

    val stove = new Stove()
    room.addObject(stove)

    val fridge = new Fridge()
    room.addObject(fridge)

    val freezer = new Freezer()
    room.addObject(freezer)



    // Containers
    val cupboard = new Cupboard()
    room.addObject(cupboard)

    cupboard.addObject( new MetalPot )
    cupboard.addObject( new TinCup() )
    cupboard.addObject( new CeramicCup() )

    val counter = new Counter()
    room.addObject(counter)

    val bowl = new WoodBowl()
    counter.addObject(bowl)

    // Foods
    val apple = new Apple()
    bowl.addObject(apple)

    bowl.addObject( new Banana() )
    bowl.addObject( new Orange() )
    bowl.addObject( new Potato() )


    // Another container
    val cup2 = new WoodCup()
    cup2.addObject( new OrangeJuice() )
    fridge.addObject(cup2)

    // A Thermometer
    val thermometer = new Thermometer()
    room.addObject(thermometer)


    // Random picture
    room.addObject( Picture.mkRandom() )

    // Table
    val table = new Table()
    room.addObject( table )

    // Cup on table
    table.addObject( new GlassCup() )

    // Chair
    val chair = new Chair()
    room.addObject(chair)


    // Return
    room
  }


  /*
   * Helper functions
   */
  def mkDoor(location1:Location, location2:Location, isOpen:Boolean = false) {
    val door = new Door(isOpen, location1, location2)
    location1.addPortal(door)
    location2.addPortal(door)
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




object BuildingMaker {

  def mkRandomHouse(universe:Universe, sewer:Sewer): Unit = {
    // Rooms
    val roomKitchen = RoomMaker.mkKitchen(sewer)
    universe.addObject(roomKitchen)

    val roomLivingRoom = RoomMaker.mkLivingRoom()
    universe.addObject(roomLivingRoom)

    val roomBedroom = RoomMaker.mkBedroom()
    universe.addObject(roomBedroom)

    // Bathroom
    val roomBathroom = RoomMaker.mkBathroom(includeBathtub = true, sewer)
    universe.addObject(roomBathroom)

    // Hallway
    val roomHallway = new Room("Hallway")
    roomHallway.addObject( Picture.mkRandom() )
    universe.addObject(roomHallway)


    // Doors
    RoomMaker.mkDoor(roomKitchen, roomHallway)
    RoomMaker.mkDoor(roomLivingRoom, roomHallway)
    RoomMaker.mkDoor(roomBedroom, roomHallway)
    RoomMaker.mkDoor(roomBathroom, roomKitchen)
  }



}
