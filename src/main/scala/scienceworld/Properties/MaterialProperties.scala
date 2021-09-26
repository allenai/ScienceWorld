package scienceworld.Properties

trait MaterialProperties {
  var substanceName:String            = "none"
  var color:String                    = "clear"
  // Thermal (all temperatures in C)
  var temperatureC:Double             = 0.0f
  var thermalConductivity:Double      = 1.0f
  var stateOfMatter:String            = "solid"
  var boilingPoint:Double             = 100.0f
  var meltingPoint:Double             = 0.0f
  var combustionPoint:Double          = 200.0f
  // Electrical
  var electricallyConductive:Boolean  = false
}


class Air extends MaterialProperties {
  substanceName                   = "air"
  color                           = "clear"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.01
  stateOfMatter                   = "gas"
  boilingPoint                    = -194.0f
  meltingPoint                    = -215.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = false
}


/*
 * Metals
 */
class Metal extends MaterialProperties {
  substanceName                   = "metal"
  color                           = "grey"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 2000.0f
  meltingPoint                    = 1000.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true
}

class Steel extends Metal {
  substanceName                   = "steel"
  color                           = "grey"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 2900.0f
  meltingPoint                    = 1400.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}


/*
 * Glass
 */
class Glass extends MaterialProperties {
  substanceName                   = "glass"
  color                           = "clear"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 2200.0f
  meltingPoint                    = 1400.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}


/*
 * Plastic
 */
class Plastic extends MaterialProperties {
  substanceName                   = "plastic"
  color                           = "white"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 300.0f
  meltingPoint                    = 200.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}


/*
 * Liquids
 */
class Water extends MaterialProperties {
  substanceName                   = "water"
  color                           = "clear"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.5f
  stateOfMatter                   = "solid"
  boilingPoint                    = 100.0f
  meltingPoint                    = 0.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}
