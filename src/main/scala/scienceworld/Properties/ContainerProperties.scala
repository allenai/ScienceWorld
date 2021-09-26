package scienceworld.Properties

trait ContainerProperties {
  var isContainer:Boolean         = true
  var isOpen:Boolean              = false

  // Add properties for storing different kinds of things?  (e.g. can store solids?  liquids?  gasses? etc)
}


class IsContainer extends ContainerProperties {
  isContainer                     = true
  isOpen                          = false
}

class IsNotContainer extends ContainerProperties {
  isContainer                     = false
  isOpen                          = false
}