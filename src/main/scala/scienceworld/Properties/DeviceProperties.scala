package scienceworld.Properties


trait DeviceProperties {
  var isActivable:Boolean               = false
  var isActivated:Boolean               = false
}


class IsActivableDevice extends DeviceProperties {
  isActivable                           = true
  isActivated                           = false
}

class IsNotActivableDevice extends DeviceProperties {
  isActivable                           = false
  isActivated                           = false
}
