package scienceworld.Properties


trait DeviceProperties {
  var isActivable:Boolean               = false
  var isActivated:Boolean               = false
}


class IsActivableDeviceOff extends DeviceProperties {
  isActivable                           = true
  isActivated                           = false
}

class IsActivableDeviceOn extends DeviceProperties {
  isActivable                           = true
  isActivated                           = true
}

class IsNotActivableDeviceOff extends DeviceProperties {
  isActivable                           = false
  isActivated                           = false
}

class IsNotActivableDeviceOn extends DeviceProperties {
  isActivable                           = false
  isActivated                           = true
}
