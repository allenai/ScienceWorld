package scienceworld.properties

trait MaterialProperties {
  var substanceName:String            = "none"
  var nameInStateOfMatter:Map[String, String] = Map("solid" -> "none", "liquid" -> "none", "gas" -> "none")
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


class AirProp extends MaterialProperties {
  substanceName                   = "air"
  nameInStateOfMatter             = Map("solid" -> "solid air", "liquid" -> "liquid air", "gas" -> "air")
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
class MetalProp extends MaterialProperties {
  substanceName                   = "metal"
  nameInStateOfMatter             = Map("solid" -> "metal", "liquid" -> "liquid metal", "gas" -> "gaseous metal")
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

class AluminumProp extends MetalProp {
  substanceName                   = "aluminum"
  nameInStateOfMatter             = Map("solid" -> "aluminum", "liquid" -> "liquid aluminum", "gas" -> "gaseous aluminum")
  color                           = "grey"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 2470.0f
  meltingPoint                    = 660.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

class BrassProp extends MetalProp {
  substanceName                   = "brass"
  nameInStateOfMatter             = Map("solid" -> "brass", "liquid" -> "liquid brass", "gas" -> "gaseous brass")
  color                           = "brown"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 1100.0f
  meltingPoint                    = 900.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

class BronzeProp extends MetalProp {
  substanceName                   = "bronze"
  nameInStateOfMatter             = Map("solid" -> "bronze", "liquid" -> "liquid bronze", "gas" -> "gaseous bronze")
  color                           = "brown"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 2300.0f
  meltingPoint                    = 910.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

class CopperProp extends MetalProp {
  substanceName                   = "copper"
  nameInStateOfMatter             = Map("solid" -> "copper", "liquid" -> "liquid copper", "gas" -> "gaseous copper")
  color                           = "brown"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 2500.0f
  meltingPoint                    = 1083.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

class GoldProp extends MetalProp {
  substanceName                   = "gold"
  nameInStateOfMatter             = Map("solid" -> "gold", "liquid" -> "liquid gold", "gas" -> "gaseous gold")
  color                           = "brown"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 2700.0f
  meltingPoint                    = 1063.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

class IronProp extends MetalProp {
  substanceName                   = "iron"
  nameInStateOfMatter             = Map("solid" -> "iron", "liquid" -> "liquid iron", "gas" -> "gaseous iron")
  color                           = "grey"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 2862.0f
  meltingPoint                    = 1538.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

class LeadProp extends MetalProp {
  substanceName                   = "lead"
  nameInStateOfMatter             = Map("solid" -> "lead", "liquid" -> "liquid lead", "gas" -> "gaseous lead")
  color                           = "grey"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 1740.0f
  meltingPoint                    = 327.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

class PlatinumProp extends MetalProp {
  substanceName                   = "platinum"
  nameInStateOfMatter             = Map("solid" -> "platinum", "liquid" -> "liquid platinum", "gas" -> "gaseous platinum")
  color                           = "grey"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 4400.0f
  meltingPoint                    = 1770.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

class SiliconProp extends MetalProp {
  substanceName                   = "silicon"
  nameInStateOfMatter             = Map("solid" -> "silicon", "liquid" -> "liquid silicon", "gas" -> "gaseous silicon")
  color                           = "grey"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 2600.0f
  meltingPoint                    = 1420.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

class SteelProp extends MetalProp {
  substanceName                   = "steel"
  nameInStateOfMatter             = Map("solid" -> "steel", "liquid" -> "liquid steel", "gas" -> "gaseous steel")
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

class TinProp extends MetalProp {
  substanceName                   = "tin"
  nameInStateOfMatter             = Map("solid" -> "tin", "liquid" -> "liquid tin", "gas" -> "gasseous tin")
  color                           = "grey"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 2602.0f
  meltingPoint                    = 232.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

class TitaniumProp extends MetalProp {
  substanceName                   = "titanium"
  nameInStateOfMatter             = Map("solid" -> "titanium", "liquid" -> "liquid titanium", "gas" -> "gaseous titanium")
  color                           = "grey"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 3200.0f
  meltingPoint                    = 1670.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

class ZincProp extends MetalProp {
  substanceName                   = "zinc"
  nameInStateOfMatter             = Map("solid" -> "zinc", "liquid" -> "liquid zinc", "gas" -> "gaseous zinc")
  color                           = "brown"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 906.0f
  meltingPoint                    = 419.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = true

}

/*
 * Glass
 */
class GlassProp extends MaterialProperties {
  substanceName                   = "glass"
  nameInStateOfMatter             = Map("solid" -> "glass", "liquid" -> "liquid glass", "gas" -> "gaseous glass")
  color                           = "clear"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 2200.0f
  meltingPoint                    = 1400.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = false

}


/*
 * Plastic
 */
class PlasticProp extends MaterialProperties {
  substanceName                   = "plastic"
  nameInStateOfMatter             = Map("solid" -> "plastic", "liquid" -> "liquid plastic", "gas" -> "gaseous plastic")
  color                           = "white"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 1.00
  stateOfMatter                   = "solid"
  boilingPoint                    = 300.0f
  meltingPoint                    = 200.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = false

}

/*
 * Ceramic
 */
class CeramicProp extends MaterialProperties {
  substanceName                   = "ceramic"
  nameInStateOfMatter             = Map("solid" -> "ceramic", "liquid" -> "liquid ceramic", "gas" -> "gaseous ceramic")
  color                           = "white"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.50
  stateOfMatter                   = "solid"
  boilingPoint                    = 10001.0f
  meltingPoint                    = 2000.0f
  combustionPoint                 = 10000.0f
  // Electrical
  electricallyConductive          = false

}



/*
 * Wood/Paper
 */
class WoodProp extends MaterialProperties {
  substanceName                   = "wood"
  nameInStateOfMatter             = Map()
  color                           = "brown"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.50
  stateOfMatter                   = "solid"
  boilingPoint                    = 10001.0f
  meltingPoint                    = 10000.0f
  combustionPoint                 = 350.0f
  // Electrical
  electricallyConductive          = false

}


class PaperProp extends MaterialProperties {
  substanceName                   = "paper"
  nameInStateOfMatter             = Map()
  color                           = "white"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.50
  stateOfMatter                   = "solid"
  boilingPoint                    = 10001.0f
  meltingPoint                    = 10000.0f
  combustionPoint                 = 210.0f
  // Electrical
  electricallyConductive          = false

}


/*
 * Cloth
 */
class CottonClothProp extends MaterialProperties {
  substanceName                   = "cloth"
  nameInStateOfMatter             = Map()
  color                           = "white"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.10
  stateOfMatter                   = "solid"
  boilingPoint                    = 10001.0f
  meltingPoint                    = 10000.0f
  combustionPoint                 = 300.0f
  // Electrical
  electricallyConductive          = false

}


/*
 * Liquids
 */
class WaterProp extends MaterialProperties {
  substanceName                   = "water"
  nameInStateOfMatter             = Map("solid" -> "ice", "liquid" -> "water", "gas" -> "steam")
  color                           = "clear"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.5f
  stateOfMatter                   = "liquid"
  boilingPoint                    = 100.0f
  meltingPoint                    = 0.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = false

}


class OrangeJuiceProp extends MaterialProperties {
  substanceName                   = "orange juice"
  nameInStateOfMatter             = Map("solid" -> "solid orange juice", "liquid" -> "orange juice", "gas" -> "gaseous orange juice")
  color                           = "orange"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.5f
  stateOfMatter                   = "liquid"
  boilingPoint                    = 100.0f
  meltingPoint                    = 0.0f
  combustionPoint                 = 100000.0f
  // Electrical
  electricallyConductive          = false
}


/*
 * Plants/Soil
 */
class SoilProp extends MaterialProperties {
  substanceName                   = "soil"
  nameInStateOfMatter             = Map()
  color                           = "brown"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.50
  stateOfMatter                   = "solid"
  boilingPoint                    = 10001.0f
  meltingPoint                    = 10000.0f
  combustionPoint                 = 10000.0f
  // Electrical
  electricallyConductive          = false

}

class PlantMatterProp extends MaterialProperties {
  substanceName                   = "plant matter"
  nameInStateOfMatter             = Map()
  color                           = "green"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.50
  stateOfMatter                   = "solid"
  boilingPoint                    = 10001.0f
  meltingPoint                    = 10000.0f
  combustionPoint                 = 200.0f
  // Electrical
  electricallyConductive          = false

}

class FlowerMatterProp extends MaterialProperties {
  substanceName                   = "flower matter"
  nameInStateOfMatter             = Map()
  color                           = "purple"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.50
  stateOfMatter                   = "solid"
  boilingPoint                    = 10001.0f
  meltingPoint                    = 10000.0f
  combustionPoint                 = 200.0f
  // Electrical
  electricallyConductive          = false

}

class PollenMatterProp extends MaterialProperties {
  substanceName                   = "pollen matter"
  nameInStateOfMatter             = Map()
  color                           = "yellow"
  // Thermal
  temperatureC                    = 10.0f
  thermalConductivity             = 0.50
  stateOfMatter                   = "solid"
  boilingPoint                    = 10001.0f
  meltingPoint                    = 10000.0f
  combustionPoint                 = 200.0f
  // Electrical
  electricallyConductive          = false

}

