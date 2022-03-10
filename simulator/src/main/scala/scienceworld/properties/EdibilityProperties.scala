package scienceworld.properties

trait EdibilityProperties {
  var isEdible:Boolean            = true
  var isPoisonous:Boolean         = false
}


class Edible extends EdibilityProperties {
  isEdible                        = true
  isPoisonous                     = false
}

class Poisonous extends EdibilityProperties {
  isEdible                        = true
  isPoisonous                     = false
}

class Inedible extends EdibilityProperties {
  isEdible                        = false
  isPoisonous                     = false
}