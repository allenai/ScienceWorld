package scienceworld.properties

import scienceworld.struct.EnvObject

class PortalProperties(var isOpen:Boolean,
                       var isOpenable:Boolean,
                       var connectsFrom:EnvObject,
                       var connectsTo:EnvObject,
                       val isLockable:Boolean,
                       val isLocked:Boolean) {

}
