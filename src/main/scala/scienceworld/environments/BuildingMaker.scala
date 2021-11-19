package scienceworld.environments

import scienceworld.objects.livingthing.animals.Bee
import scienceworld.objects.{Apple, Banana, Orange, OrangeJuice, Potato, Soap, SodiumChloride, Water, Wood}
import scienceworld.objects.containers.{BookShelf, CeramicCup, FlowerPot, GlassCup, GlassJar, Jug, MetalPot, Sewer, TinCup, WoodBowl, WoodCup}
import scienceworld.objects.containers.furniture.{Bed, Chair, Closet, Couch, Counter, Cupboard, Desk, SteelTable, WoodTable}
import scienceworld.objects.devices.{Axe, Bathtub, BlastFurnace, Freezer, Fridge, Lighter, Oven, Shovel, Sink, Stove, Thermometer, Toilet}
import scienceworld.objects.electricalcomponent.{Battery, LightBulb, SolarPanel, Switch, Wire}
import scienceworld.objects.environmentoutside.{FirePit, Fountain}
import scienceworld.objects.livingthing.plant.{AppleTree, OrangeTree, PeachTree, Plant, Soil}
import scienceworld.objects.location.{Location, Outside, Room, Universe}
import scienceworld.objects.misc.{ForkMetal, ForkPlastic, Picture}
import scienceworld.objects.portal.Door
import scienceworld.objects.taskitems.{AnswerBox, UnknownSubstanceElectricalConductivity}
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

    val table = new WoodTable()
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
    val room = new Room("living room")

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
    val room = new Room("kitchen")

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

    val oven = new Oven()
    room.addObject(oven)


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
    val table = new WoodTable()
    room.addObject( table )

    // Cup on table
    table.addObject( new GlassCup() )

    // Chair
    val chair = new Chair()
    room.addObject(chair)

    // Lighter
    val lighter = new Lighter()
    room.addObject(lighter)

    // Salt
    val jar = new GlassJar()
    jar.addObject(new SodiumChloride())
    room.addObject(jar)

    // Soap
    room.addObject(new Soap())


    // Return
    room
  }


  /*
   * Workshop
   */
  def mkWorkshop(): Room = {
    // House
    val room = new Room("workshop")

    // Table
    val table = new WoodTable()
    room.addObject( table )

    // Electical
    val lightbulb1 = new LightBulb()
    lightbulb1.name = "light bulb 1"
    table.addObject(lightbulb1)

    val lightbulb2 = new LightBulb()
    lightbulb2.name = "light bulb 2"
    table.addObject(lightbulb2)

    val lightbulb3 = new LightBulb()
    lightbulb3.name = "light bulb 3"
    table.addObject(lightbulb3)


    val wire1 = new Wire()
    wire1.name = "wire 1"
    table.addObject(wire1)

    val wire2 = new Wire()
    wire2.name = "wire 2"
    table.addObject(wire2)


    val switch = new Switch()
    table.addObject(switch)

    val battery = new Battery()
    table.addObject(battery)

    // Add a randomly generated substance that may or may not be electrically conductive
    /*
    val unknownSubstance = UnknownSubstanceElectricalConductivity.mkRandomSubstanceElectricalConductive()
    table.addObject(unknownSubstance)
     */

    // Add one metalic and one non-metalic object
    room.addObject(new ForkMetal())
    room.addObject(new ForkPlastic())

    // Answer box
    room.addObject( new AnswerBox("green") )
    room.addObject( new AnswerBox("red") )




    // Return
    room
  }

  /*
   * Greenhouse
   */
  def mkGreenhouse(sewer:EnvObject): Room = {
    // House
    val room = new Room("green house")


    // Water jug
    val waterJug = new Jug()
    room.addObject(waterJug)


    // Shovel
    val shovel = new Shovel()
    room.addObject(shovel)

/*
    // debug (plant)
    val plant1 = new OrangeTree()
    //plant1.name = "plant 1"
    val soil1 = new Soil()
    val flowerpot1 = new FlowerPot()
    flowerpot1.name = "flower pot 1"

    flowerpot1.addObject(soil1)
    flowerpot1.addObject(plant1)
    room.addObject(flowerpot1)


    // debug (plant)
    val plant2 = new AppleTree()
    //plant2.name = "plant 2"
    val soil2 = new Soil()
    val flowerpot2 = new FlowerPot()
    flowerpot2.name = "flower pot 2"

    flowerpot2.addObject(soil2)
    flowerpot2.addObject(plant2)
    room.addObject(flowerpot2)

    // debug (plant)
    val plant3 = new PeachTree()
    //plant3.name = "plant 3"
    val soil3 = new Soil()
    val flowerpot3 = new FlowerPot()
    flowerpot3.name = "flower pot 3"

    flowerpot3.addObject(soil3)
    flowerpot3.addObject(plant3)
    room.addObject(flowerpot3)


    val plant4 = new PeachTree()
    //plant3.name = "plant 3"
    val soil4 = new Soil()
    val flowerpot4 = new FlowerPot()
    flowerpot4.name = "flower pot 4"

    flowerpot4.addObject(soil4)
    flowerpot4.addObject(plant4)
    room.addObject(flowerpot4)


    val plant5 = new OrangeTree()
    //plant3.name = "plant 3"
    val soil5 = new Soil()
    val flowerpot5 = new FlowerPot()
    flowerpot5.name = "flower pot 5"

    flowerpot5.addObject(soil5)
    flowerpot5.addObject(plant5)
    room.addObject(flowerpot5)
*/


    val numBees = 5
    for (i <- 0 until numBees) {
      val bee = new Bee()
      bee.name = "bee " + i
      room.addObject(bee)
    }





    // Also add a sink, to fill up the water jug
    val sink = new Sink(drainsTo=Some(sewer) )
    room.addObject(sink)

    //## DEBUG
    // Answer box
    room.addObject( new AnswerBox("blue") )


    // Return
    room
  }


  /*
   * Outside
   */
  def mkOutside():Outside = {
    val outside = new Outside()

    outside.addObject( new FirePit() )
    outside.addObject( new Wood() )
    outside.addObject( new Axe() )
    outside.addObject( new Fountain() )

    return outside
  }


  /*
   * Foundry
   */
  def mkFoundry(sewer:Sewer):Room = {
    val foundry = new Room("foundry")

    foundry.addObject(new BlastFurnace())
    foundry.addObject(new Sink( drainsTo=Some(sewer)  ))
    foundry.addObject(new SteelTable() )

    // Return
    foundry
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
    val numOptions = 3
    val randIdx = Random.nextInt(numOptions)

    if (randIdx == 0) return new WoodTable()
    if (randIdx == 1) return new SteelTable()
    if (randIdx == 2) return new Desk()

    // Default
    return new WoodTable()
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

    // Workshop
    val roomWorkshop = RoomMaker.mkWorkshop()
    universe.addObject(roomWorkshop)

    // Green House
    val roomGreenhouse = RoomMaker.mkGreenhouse(sewer)
    universe.addObject(roomGreenhouse)

    // Hallway
    val roomHallway = new Room("hallway")
    roomHallway.addObject( Picture.mkRandom() )
    universe.addObject(roomHallway)


    // Outside
    val outside = RoomMaker.mkOutside()
    universe.addObject(outside)

    // Foundry
    val foundry = RoomMaker.mkFoundry(sewer)
    universe.addObject(foundry)


    // Doors
    RoomMaker.mkDoor(roomKitchen, roomHallway)
    RoomMaker.mkDoor(roomLivingRoom, roomHallway)
    RoomMaker.mkDoor(roomBedroom, roomHallway)
    RoomMaker.mkDoor(roomBathroom, roomKitchen)
    RoomMaker.mkDoor(roomWorkshop, roomHallway)
    RoomMaker.mkDoor(roomGreenhouse, roomHallway)

    RoomMaker.mkDoor(roomKitchen, outside)
    RoomMaker.mkDoor(roomGreenhouse, outside)

    RoomMaker.mkDoor(foundry, outside)

  }



}
