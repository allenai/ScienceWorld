package scienceworld.properties


trait DeviceProperties {
  var isActivable:Boolean               = false
  var isActivated:Boolean               = false
  var isUsable:Boolean                  = false
  var isBroken:Boolean                  = false       // For devices that are broken (e.g. for environment ablations)
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


// For instantaneous use devices (like a thermometer)
class IsUsable extends DeviceProperties {
  isUsable                              = true
}