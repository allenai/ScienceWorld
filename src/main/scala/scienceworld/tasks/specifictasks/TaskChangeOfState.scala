package scienceworld.tasks.specifictasks

import scienceworld.objects.{AppleJuice, Caesium, Chocolate, Gallium, Lead, Marshmallow, Mercury, OrangeJuice, Soap, Tin}
import scienceworld.properties.LeadProp
import scienceworld.struct.EnvObject


class TaskChangeOfState(val seed:Int) {

  val substancePossibilities = Array(
    new TaskObject("water", None, "", Array.empty[String], 0),                                      // Example of water (found in the environment)

    new TaskObject("orange juice", Some(new OrangeJuice), "kitchen", Array("fridge"), 0),           // Example of something needing to be generated
    new TaskObject("apple juice", Some(new AppleJuice), "kitchen", Array("fridge"), 0),
    new TaskObject("chocolate", Some(new Chocolate), "kitchen", Array("fridge"), 0),
    new TaskObject("marshmallow", Some(new Marshmallow), roomToGenerateIn = "kitchen", Array("cupboard", "table", "desk"), generateNear = 0),

    new TaskObject("soap", Some(new Soap), roomToGenerateIn = "kitchen", Array("cupboard", "table", "desk"), generateNear = 0),
    new TaskObject("rubber", Some(new Soap), roomToGenerateIn = "workshop", Array("table", "desk"), generateNear = 0),

    new TaskObject("lead", Some(new Lead()), "workshop", Array("table", "desk"), 0),                // Metals
    new TaskObject("tin", Some(new Tin()), "workshop", Array("table", "desk"), 0),
    new TaskObject("mercury", Some(new Mercury()), "workshop", Array("table", "desk"), 0),
    new TaskObject("gallium", Some(new Gallium()), "workshop", Array("table", "desk"), 0),
    new TaskObject("caesium", Some(new Caesium()), "workshop", Array("table", "desk"), 0),
  )


  /*
  new Lead()
new Tin()
new Mercury()
new Gallium()
new Caesium()
   */
}



// An example of a specific object required for a task:
// (a) it's name,
// (b) if it needs to be generated (and isn't just found in the environment naturally), an example instance of it
// (c) if generation is required, what room to generate it in (set to "" for n/a)
// (d) if generation if required, what container name(s) are valid to place it into (e.g. "desk", "table").  If blank, it will be placed in the root container (e.g. the 'floor')
// (e) generateNear: If non-zero, it will generate the object within 'generateNear' steps of the original location.  e.g. if the generation location is 'kitchen', and generateNear is 2, then the object could be generated in (e.g.) the hallway or bathroom, but not a far-off location.
class TaskObject(val name:String, val exampleInstance:Option[EnvObject], val roomToGenerateIn:String, val possibleContainerNames:Array[String], val generateNear:Int=0) {
  // Does this task object need to be generated in the environment?
  val needsToBeGenerated:Boolean = exampleInstance.isEmpty



}
