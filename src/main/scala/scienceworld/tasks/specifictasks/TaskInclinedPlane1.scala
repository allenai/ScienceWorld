package scienceworld.tasks.specifictasks

import scienceworld.objects.agent.Agent
import scienceworld.objects.livingthing.LivingThing
import scienceworld.objects.livingthing.animals.{Beaver, BlueJay, BrownBear, Butterfly, Crocodile, Dove, Elephant, Frog, GiantTortoise, Moth, Parrot, Toad, Turtle, Wolf}
import scienceworld.objects.misc.InclinedPlane
import scienceworld.properties.{AluminumProp, BrassProp, BronzeProp, CaesiumProp, CeramicProp, ChocolateProp, CopperProp, CottonClothProp, GlassProp, GreenPaintProp, IronProp, LeadProp, PaperProp, PlasticProp, PlatinumProp, RubberProp, SandpaperProp, SoapyWaterProp, SteelProp, TinProp, TitaniumProp, WoodProp, ZincProp}
import scienceworld.struct.EnvObject
import scienceworld.tasks.{Task, TaskMaker1, TaskModifier, TaskObject, TaskValueStr}
import scienceworld.tasks.goals.{Goal, GoalSequence}
import scienceworld.tasks.goals.specificgoals.{GoalFind, GoalFindInclinedPlane, GoalFindLivingThingStage}

import scala.util.control.Breaks._
import scala.collection.mutable.ArrayBuffer
import TaskInclinedPlane1._

import scala.util.Random


class TaskInclinedPlane1(val mode:String = MODE_FRICTION_NAMED) extends TaskParametric {
  val taskName = "task-8-" + mode.replaceAll(" ", "-")

  val locations = Array("workshop")

  // Variation 1: Which seeds to grow
  val numDistractors = 5
  val inclinedPlanes1 = new ArrayBuffer[ Array[TaskModifier] ]()
  val inclinedPlanes2 = new ArrayBuffer[ Array[TaskModifier] ]()
  for (location <- locations) {

    // Make a set of inclined plane objects of various surfaces
    val inclinedPlaneObjects1 = TaskInclinedPlane1.mkInclinedPlaneSet(angleDeg = 45.0)
    for (inclinedPlane <- inclinedPlaneObjects1) {
      inclinedPlanes1.append( Array(new TaskObject(inclinedPlane.name, Some(inclinedPlane), roomToGenerateIn = location, Array.empty[String], generateNear = 0, forceAdd = true)) )
    }

    val inclinedPlaneObjects2 = TaskInclinedPlane1.mkInclinedPlaneSet(angleDeg = 45.0)
    for (inclinedPlane <- inclinedPlaneObjects2) {
      inclinedPlanes2.append( Array(new TaskObject(inclinedPlane.name, Some(inclinedPlane), roomToGenerateIn = location, Array.empty[String], generateNear = 0, forceAdd = true)) )
    }

  }

  // Combinations
  var combinations1 = for {
    i <- inclinedPlanes1
    j <- inclinedPlanes2
  } yield List(i, j)

  // Remove any combinations that have the same coefficient of friction
  // Also add extra information as keys (e.g. most friction/least friction)
  val combinations = TaskInclinedPlane1.filterDuplicatesAndAddMostLeast(combinations1)


  println("Number of combinations: " + combinations.length)

  def numCombinations():Int = this.combinations.size

  def getCombination(idx:Int):Array[TaskModifier] = {
    val out = new ArrayBuffer[TaskModifier]
    for (elem <- combinations(idx)) {
      out.insertAll(out.length, elem)
    }
    // Return
    out.toArray
  }

  // Setup a particular modifier combination on the universe
  private def setupCombination(modifiers:Array[TaskModifier], universe:EnvObject, agent:Agent):(Boolean, String) = {
    // Run each modifier's change on the universe
    for (mod <- modifiers) {
      println("Running modifier: " + mod.toString)
      val success = mod.runModifier(universe, agent)
      if (!success) {
        return (false, "ERROR: Error running one or more modifiers while setting up task environment.")
      }
    }
    // If we reach here, success
    return (true, "")
  }

  def setupCombination(combinationNum:Int, universe:EnvObject, agent:Agent): (Boolean, String) = {
    if (combinationNum >= this.numCombinations()) {
      return (false, "ERROR: The requested variation (" + combinationNum + ") exceeds the total number of variations (" + this.numCombinations() + ").")
    }
    return this.setupCombination( this.getCombination(combinationNum), universe, agent )
  }


  // Setup a set of subgoals for this task modifier combination.
  private def setupGoals(modifiers:Array[TaskModifier], combinationNum:Int): Task = {
    // Step 1: Find seed type
    val leastFriction = this.getTaskValueStr(modifiers, "leastFriction")
    if (leastFriction.isEmpty) throw new RuntimeException("ERROR: Failed to find name of inclined plane with least friction.")
    val mostFriction = this.getTaskValueStr(modifiers, "mostFriction")
    if (mostFriction.isEmpty) throw new RuntimeException("ERROR: Failed to find name of inclined plane with most friction.")

    val gSequence = new ArrayBuffer[Goal]
    var description:String = "<empty>"
    if (mode == MODE_FRICTION_NAMED) {

      gSequence.append(new GoalFindInclinedPlane(surfaceName = leastFriction.get, failIfWrong = true, _defocusOnSuccess = true))
      val planeNames = Random.shuffle( List(leastFriction.get, mostFriction.get) )

      description = "Your task is to determine which of the two inclined planes (" + planeNames.mkString(", ") + ") has the least friction. After completing your experiment, focus on the inclined plane with the least friction." // TODO: Better description?

    } else {
      throw new RuntimeException("ERROR: Unrecognized task mode: " + mode)
    }

    val taskLabel = taskName + "-variation" + combinationNum
    //val description = "Your task is to find a " + subTask + ". First, focus on the thing. Then, move it to the " + answerBoxName + " in the " + answerBoxLocation + "."
    val goalSequence = new GoalSequence(gSequence.toArray)

    val task = new Task(taskName, description, goalSequence)

    // Return
    return task
  }

  def setupGoals(combinationNum:Int): Task = {
    this.setupGoals( this.getCombination(combinationNum), combinationNum )
  }




}


object TaskInclinedPlane1 {
  val MODE_FRICTION_NAMED       = "inclined plane friction (named surfaces)"

  def registerTasks(taskMaker:TaskMaker1): Unit = {
    taskMaker.addTask( new TaskInclinedPlane1(mode = MODE_FRICTION_NAMED) )
  }


  /*
   * Helper functinos
   */

  // Make a set of inclined planes
  def mkInclinedPlaneSet(angleDeg:Double = 45.0):Array[EnvObject] = {
    val out = new ArrayBuffer[EnvObject]

    // Metal surfaces
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new AluminumProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new BrassProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new BronzeProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new CaesiumProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new CopperProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new IronProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new LeadProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new PlatinumProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new SteelProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new TinProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new TitaniumProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new ZincProp) )

    // Non-metal surfaces
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new GlassProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new PlasticProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new CeramicProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new WoodProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new PaperProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new SandpaperProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new CottonClothProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new RubberProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new ChocolateProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new SoapyWaterProp) )
    out.append( new InclinedPlane(angleDeg, surfaceMaterial = new GreenPaintProp) )

    // Return
    out.toArray
  }


  def filterDuplicatesAndAddMostLeast(in:Traversable[List[Array[TaskModifier]]], threshold:Double = 0.05): Array[List[List[TaskModifier]]] = {
    val out = new ArrayBuffer[ List[List[TaskModifier]] ]

    for (tmSet <- in) {
      val inclinedPlanes = tmSet.flatten

      breakable {
        var leastFriction:Double = 1.0
        var leastFrictionName:String = ""
        var mostFriction:Double = 0.0
        var mostFrictionName:String = ""

        for (i <- 0 until inclinedPlanes.length) {
          val tm1 = inclinedPlanes(i).asInstanceOf[TaskObject]
          val plane1 = tm1.exampleInstance.get.asInstanceOf[InclinedPlane]

          for (j <- 0 until inclinedPlanes.length) {
            val tm2 = inclinedPlanes(j).asInstanceOf[TaskObject]
            val plane2 = tm2.exampleInstance.get.asInstanceOf[InclinedPlane]

            if (i != j) {
              // Check to see if the friction coefficients of the two materials are too similar (i.e. within 'threshold').  If they are, don't keep them in the list.
              val delta = math.abs(plane1.surfaceMaterial.frictionCoefficient - plane2.surfaceMaterial.frictionCoefficient)
              if (delta <= threshold) break
            }
          }

          if (plane1.surfaceMaterial.frictionCoefficient < leastFriction) {
            leastFriction = plane1.surfaceMaterial.frictionCoefficient
            leastFrictionName = plane1.surfaceMaterial.substanceName
          }
          if (plane1.surfaceMaterial.frictionCoefficient > mostFriction) {
            mostFriction = plane1.surfaceMaterial.frictionCoefficient
            mostFrictionName = plane1.surfaceMaterial.substanceName
          }

        }

        // If we reach here, the list is okay (i.e. no duplicates/near duplicates).  Store it.
        val taskModifiers = inclinedPlanes ++ Array(new TaskValueStr(key = "leastFriction", leastFrictionName), new TaskValueStr(key = "mostFriction", mostFrictionName))
        out.append( List(taskModifiers.toList) )      // Wrapped in an extra List() to match how this appears in other Tasks
      }

    }

    // Return
    out.toArray
  }

}
