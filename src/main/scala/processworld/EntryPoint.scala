package processworld

import processworld.CategoricalVariableStore._
import processworld.Objects.Agent
import processworld.Objects.Dishes.{Bowl, Plate}
import processworld.Objects.Dishwasher.Dishwasher
import processworld.Objects.Environments.Kitchen
import processworld.Objects.Foods.Apple
import processworld.Objects.Substances.{Soap, Water}
import processworld.Visualization.Visualize
import processworld.struct.{ActionInfo, ActionInfoMove, stdAction, stdObject, stdProp}


class EntryPoint {


}


object EntryPoint {
  val logger = Logger.Logger

  def main(args:Array[String]): Unit = {

    logger.add("Started... ")

    val cvStore = new CategoricalVariableStore()



    // Kitchen
    val envKitchen = new Kitchen()


    // Dishwasher
    val objDishwasher = new Dishwasher()
    envKitchen.addObjectToContainer(objDishwasher)

    // Tick
    envKitchen.tick()

    // Add bowl to dishrack
    // Find dishracks
    var dishwasherRacks = objDishwasher.getContainedObjectsWithName(stdObject.OBJ_DISHWASHER_RACK, allowPorousContainers = true)
    val objDish1 = new Bowl()
    if (dishwasherRacks.length > 0) {
      dishwasherRacks(0).addObjectToContainer(objDish1)
    }

    // Tick
    envKitchen.tick()

    // Add plate to dishrack
    // Find dishracks
    dishwasherRacks = objDishwasher.getContainedObjectsWithName(stdObject.OBJ_DISHWASHER_RACK, allowPorousContainers = true)
    val objDish2 = new Plate()
    if (dishwasherRacks.length > 0) {
      dishwasherRacks(0).addObjectToContainer(objDish2)
    }

    // Tick
    envKitchen.tick()

    println("----------------------------------------------------- ")
    println("")
    println("Kitchen: ")
    println(envKitchen.toStringRecursive(true))


    // Try to slide out a dishwasher rack without opening the dishwasher first
    dishwasherRacks(0).doAction(stdAction.DRAWER_SLIDEOUT, new ActionInfo())

    // open dishwasher
    objDishwasher.doAction(stdAction.CONTAINER_OPEN, new ActionInfo())

    // Slide out a dishwasher rack
    dishwasherRacks = objDishwasher.getContainedObjectsWithName(stdObject.OBJ_DISHWASHER_RACK, allowPorousContainers = true)
    dishwasherRacks(0).doAction(stdAction.DRAWER_SLIDEOUT, new ActionInfo())

    objDishwasher.doAction(stdAction.CONTAINER_CLOSE, new ActionInfo())

    // Open soap cup
    val objSoapCup = objDishwasher.getContainedObjectsWithName(stdObject.OBJ_DISHWASHER_SOAP_CUP, allowPorousContainers = true)(0)
    objSoapCup.doAction(stdAction.CONTAINER_OPEN, new ActionInfo())

    // Tick
    envKitchen.tick()


    // Create dirty plate
    val objDish3 = new Plate()
    envKitchen.addObjectToContainer(objDish3)
    val objApple = new Apple()
    envKitchen.addObjectToContainer(objApple)

    val agent = new Agent()
    envKitchen.addObjectToContainer(agent)

    // Move apple to dish
    objApple.doAction(stdAction.MOVE, new ActionInfoMove(agent = agent, newContainer = objDish3))

    // Tick
    envKitchen.tick()

    // Try to clean the dish (should fail, because the dish still has food on it)
    val objWater = new Water()
    envKitchen.addObjectToContainer(objWater)

    val objSoap = new Soap()
    envKitchen.addObjectToContainer(objSoap)

    println("Kitchen: ")
    println(envKitchen.toStringRecursive(false))

    // Tick
    envKitchen.tick()

    // Move the plate to the dishrack
    objDish3.doAction(stdAction.MOVE, new ActionInfoMove(agent = agent, newContainer = dishwasherRacks(0)))

    envKitchen.tick()


    // Move the food from the plate (to the kitchen)
    objApple.doAction(stdAction.MOVE, new ActionInfoMove(agent = agent, newContainer = envKitchen))

    println(logger.show())

    envKitchen.tick()

    // Try to move the apple to the soap cup in the dishwasher
    //val objSoapCup = objDishwasher.getContainedObjectsWithName(stdObject.OBJ_DISHWASHER_SOAP_CUP, allowPorousContainers = true)(0)
    objSoap.doAction(stdAction.MOVE, new ActionInfoMove(agent = agent, newContainer = objSoapCup))

    envKitchen.tick()

    objSoapCup.doAction(stdAction.CONTAINER_CLOSE, new ActionInfo())

    // Tick
    envKitchen.tick()


    println("----------------------------------------------------- ")
    println("")
    println("Kitchen: ")
    println(envKitchen.toStringRecursive(true))


    println("----------------------------------------------------- ")
    println("LOG: ")
    println(logger.show())


    // Sprayer test

    //val objsSprayer = objDishwasher.getContainedObjectsWithName(stdObject.OBJ_DISHWASHER_SPRAYER, allowPorousContainers = true)
    //objsSprayer(0).doAction(stdAction.SPRAY_WATER, actionInfo = new ActionInfo())

    // Turn on the dishwasher
    objDishwasher.doAction(stdAction.TURN_ON, new ActionInfo())


    // Tick
    for (i <- 0 until 20) {
      envKitchen.tick()
    }



    println("----------------------------------------------------- ")
    println("")
    println("Kitchen: ")
    println(envKitchen.toStringRecursive(true))


    println("----------------------------------------------------- ")
    println("LOG: ")
    println(logger.show())

    /*
    val dot = Visualize.visualizeObjectDOT(envKitchen)
    println("")
    println("DOT:")
    println(dot)
     */


    logger.exportDOTHTML("export/")
  }

}