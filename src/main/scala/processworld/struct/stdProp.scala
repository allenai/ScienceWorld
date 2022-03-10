package processworld.struct


object stdProp {
  // This one isn't actually stored (since a proper reference is stored in the Object's member variables), but used for visualization
  val CURRENT_CONTAINER     = "current_container"

  val STATE_OF_MATTER       = "state_of_matter"
  val TEMPERATURE_C         = "temperature_c"
  val MASS_KG               = "mass_kg"
  val VOLUME_LITER          = "volume_liter"
  val CONTAINER_CAPACITY_LITERS = "container_capacity_liters"
  val CONTAINER_VOLUME_FILLED_LITERS = "container_volume_filled_liters"

  val CONTAINER_POROUS      = "container_porous"

  val LOCATION_CURRENT      = "location_current"

  val CLEANLINESS           = "cleanliness"


  val CONTAINER_OPEN        = "container_open"
  val DRAWER_OUT            = "drawer_out"

  val ACTIVATED             = "activated"

  // Dishwasher
  val ACTIVATION_STAGE      = "activation_stage"

}
