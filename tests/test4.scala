
// Open-a-closed-door then move-through-an-open-door test

//import "test1-import.env"
//import "test2.txt"

@defines
//#defineprop closed CLOSEDNAMESUB

@classes 

/*
 *  Base Object
 */ 
class Object extends Any {
    constructor() { }	
	
    prop referents = "object"

    prop isMovable = true

	prop description = {
		return this.name + ", a kind of " + this.type
	}
}

/*
 *  Agent Model
 */
class Agent extends Object {
    constructor() { }

    prop referents = "agent"
	prop inheritTest = { return "inherit!" }	
}


/*
 *  Locations
 */ 
class Location extends Object {
    constructor() { }

    // Default properties
    prop temperature = 10
    prop thermalConductivy  =   0.0001            // Very large thermal insulator
    prop isMovable = false

    override prop referents = { return "Location" }

}

class Room extends Location {
    constructor() { 
        var air = new Air()
        moveObj(air to this)
    }    

    prop name = ""
    override prop referents = { 
        return Array("room", this.name)
    }

	prop exampleProp = { 		
		return 3456
	} 

	*override prop description = {                                      // The * makes it calculate at runtime
		var os = "This room is called the " + this.name + ". "
		os = os + "In it you see: \n"
		for (containedObj <- this) {			
			os = os + "\t" + containedObj.description + "\n"
		}		        
		return os        
	}
	
}

class Outside extends Location {
    constructor() { }

    override prop referents = { return "Outside" }
}


/*
 *  Room Parts (movement)
 */
class Door extends Object {
    constructor(connectsTo:Room) { 
        printlnLog("Door being initialized that connects to " + str(connectsTo))        
        this.connectsTo = connectsTo
    }

	// Default property values
    prop isMovable = false
	prop isOpen = false
    prop connectsTo = ""

    prop referents = { 
        var connectsTo = this.connectsTo
        //var os = "door to " + connectsTo.name               // Also, other way around (e.g. living room door)        
        var refs = Array("door", "door to " + connectsTo.name, connectsTo.name + " door")
        return refs
    }

    override prop description = {
        var connectsTo = this.connectsTo
        //println(connectsTo)
        var os = "A door to " + connectsTo.name
        return os
    }
}


/*
 *  Appliances
 */
class Device extends Object {
    constructor() { } 

    prop name = "device"    

    prop canBeActivated = true
    prop isActivated = false

    override prop referents = {
        return Array("device", this.name)
    }

    override prop description = {
        return "a device"
    }

}

class Sink extends Device {
    constructor() { } 

    prop name = "sink"    

    prop madeOf = new Steel()

    override prop referents = {
        return Array("sink", this.name)
    }

    override prop description = {
        var onOff = "off"
        if (this.isActivated == true) { onOff = "on" }
        return "a sink, which is turned " + onOff
    }

}

// Heat sources
class HeatSource extends Device {
    constructor() { } 

    prop name = "heat source"        

    override prop referents = {
        return Array("heat source", this.name)
    }

    override prop description = {
        var onOff = "off"
        if (this.isActivated == true) { onOff = "on" }
        return "a " + this.name + ", which is turned " + onOff
    }
}

class Stove extends HeatSource {
    constructor() { } 

    prop name = "stove"        

    prop temperature = 10
    prop thermalConductivy  =   0.0001            // Very large thermal insulator

    prop maxTemp = 260
    prop heatingTemp = {
        if (this.isActivated == true) { return this.maxTemp }
        return 0
    }

    override prop referents = {
        return Array("stove", this.name)
    }

    override prop description = {
        var onOff = "off"
        if (this.isActivated == true) { onOff = "on" }
        var os = "a " + this.name + ", which is turned " + onOff + ". "
        if (len(this.containedObjects) > 0) {
            os = os + "On the stove is: "     
            var containedObjs = this.containedObjects       
            for (i <- 0 until len(containedObjs)) { 
                var obj = containedObjs[i]               
                os = os + obj.name                
                if (i < len(containedObjs)-1) { os = os + ", "}
            }
            os = os + (".")
        } else {
            os = os + "Nothing is on the stove."
        }
        return os
    }    
}


/*
 *  Containers
 */ 
class Container extends Object {
    constructor() { }

    prop name = "container"    

    override prop referents = {
        return Array("container", this.name)
    }

    override prop description = {
        return "a container"
    }

}


class MetalPot extends Container {
    constructor(location:Location) { 
        this.temperature = location.temperature
    }

    prop name = "metal pot"
    prop madeOf = new Steel()

    override prop referents = {
        return Array("pot", "metal pot", this.name)
    }

    override prop description = {
        return "a metal pot"
    }

}

class GlassCup extends Container { 
    constructor(location:Location) { 
        this.temperature = location.temperature
    }

    prop name = "glass cup"
    prop madeOf = new Glass()

    override prop referents = {
        return Array("cup", "glass cup", this.name)
    }

    override prop description = {
        return "a glass cup"
    }

}

/*
 *  Substances
 */
class Substance extends Object {
    constructor() {}    
    
    prop name               =   "substance"
    prop color              =   "white"
    prop temperature        =   0
    prop thermalConductivy  =   1.0
    prop stateOfMatter      =   "solid"
    prop boilingPoint       =   100
    prop meltingPoint       =   0
    prop combustionPoint    =   200

    override prop referents = {
        return Array("substance", this.name)
    }

    override prop description = {
        return "a substance"
    }   
}

/*
 *  Substances (air)
 */

class Air extends Object {
    constructor() {}    

    prop name               =   "air"
    prop color              =   "clear"
    prop temperature        =   10
    prop thermalConductivy  =   1.0
    prop stateOfMatter      =   "gas"
    prop boilingPoint       =   -194
    prop meltingPoint       =   -215
    prop combustionPoint    =   100000    

    override prop referents = {
        return Array("air")
    }

    override prop description = {
        return "a colorless, odorless gas"
    }

}

/*
 *  Substances (metals)
 */ 

class Metal extends Substance {
    constructor() {}        

    prop name = "metal"
    prop color              =   "grey"
    prop temperature        =   10
    prop thermalConductivy  =   1.0
    prop stateOfMatter      =   "solid"
    prop boilingPoint       =   2000
    prop meltingPoint       =   1000
    prop combustionPoint    =   100000    

    override prop referents = {
        return Array("metal", this.name)
    }

    override prop description = {
        return "a metal"
    }
}

class Steel extends Metal {
    constructor() {}        

    prop name = "steel"
    prop color              =   "grey"
    prop temperature        =   10
    prop thermalConductivy  =   1.0
    prop stateOfMatter      =   "solid"
    prop boilingPoint       =   2900
    prop meltingPoint       =   1400
    prop combustionPoint    =   100000    

    override prop referents = {
        return Array("steel", this.name)
    }

    override prop description = {
        return "steel"
    }
}

/*
 *  Substances (glass)
 */ 
class Glass extends Substance {
    constructor() {}        

    prop name = "glass"
    prop color              =   "clear"
    prop temperature        =   10
    prop thermalConductivy  =   1.0
    prop stateOfMatter      =   "solid"
    prop boilingPoint       =   2200
    prop meltingPoint       =   1400
    prop combustionPoint    =   100000    

    override prop referents = {
        return Array("glass", this.name)
    }

    override prop description = {
        return "glass"
    }
}

/*
 *  Substances (plastic)
 */
class Plastic extends Substance {
    constructor() { }

    prop name = "plastic"
    prop color              =   "white"
    prop temperature        =   10
    prop thermalConductivy  =   1.0
    prop stateOfMatter      =   "solid"
    prop boilingPoint       =   300             // Made up
    prop meltingPoint       =   200
    prop combustionPoint    =   100000    

    override prop referents = {
        return Array("plastic", this.name)
    }

    override prop description = {
        return "plastic"
    }
}

/*
 *  Substances (liquids)
 */
class Water extends Substance {
    constructor() { }

    prop name               =   "water"
    prop color              =   "clear"
    prop temperature        =   10
    prop thermalConductivy  =   0.5
    prop stateOfMatter      =   "liquid"
    prop boilingPoint       =   100             // Made up
    prop meltingPoint       =   0
    prop combustionPoint    =   100000    

    override prop referents = {
        return Array("water", this.name)
    }

    override prop description = {
        return "water"
    }
}


/*
 *  Substances (foods)
 */ 

class Food extends Object {
    constructor() {}    
    prop isEdible = true    

    prop referents = "food"
}

class Apple extends Food {
    constructor() {}    
    prop color = "red"
    prop name = "apple"

    prop referents = {
        return Array("apple", this.color + " apple")        
    }

    override prop description = {
        return "a " + this.color + " apple"
    }
}




@predicates 

// Locations/Contents
pred at(thing:Any, location:Any) = {
    get {
		//return 123			// Test for throwing a return type error (expected: boolean, actual: number)
        //println("Checking if thing in location...")
        if (thing in location) {
            //println("---Thing is in location...")
            return true
        }
        return false        
    }
    set {
        println ("Moving " + thing.name + " to " + location.name)
        moveObj(thing to location)
    }
}

// Currently mirrors "at", since there's no difference between in/at right now. 
pred isIn(thing:Any, container:Any) = {
    get {
        if (thing in container) {
            return true
        } 
        return false
    }
    set { 
        println ("Moving " + thing.name + " to " + container.name)
        moveObj(thing to container)
    }
}

// Moving (with move-ability checks)
pred move(thing:Any, destination:Any) = {
    get { return false }
    set {
        if (thing.isMovable == true) {
            println ("Moving " + thing.name + " to " + destination.name)
            moveObj(thing to destination)
        } else {
            println ("That can't be moved.")
        }
    }
}

// Doors
pred moveThroughDoor(agent:Agent, door:Door) = {
    get {
        return false
    }
    set {
        var connectsTo = door.connectsTo
        println ("Moving through door, to " + connectsTo.name)
        moveObj(agent to door.connectsTo)
    }
}

pred isClosed(door:Door) = {     
    get { 
        if (door.isOpen == false) { return true }           // TODO: Add negation marker :) (e.g. !door.isOpen)
        return false
    }
    set {
        door.isOpen = false
        println("Door set to closed.")
    }
}

pred isOpen(door:Door) = {
    get { 
        return door.isOpen
    }
    set {
        if (door.isOpen == false) {
            door.isOpen = true
            println("The door is now open.")
        } else {
            door.isOpen = true
            println("The door was already open.")
        }
    }
}

// Eating
pred doEat(agent:Agent, food:Food) = {
    get { return false }
    set {        
        println ("The " + food.name + " was delicious.")
        agent.hungry = false
        deleteObject(food)
        // TODO: Remove object (food) from environment
    }
}

// Looking around
pred lookAround(agent:Agent, location:Room) = {
    get { return false }
    set {
        println ("Look Around:")
        println (location.description)
    }
}

// Devices
pred isActivable(device:Device) = {
    get { return device.canBeActivated }
    set { device.canBeActivated = true }
}

pred isActivated(device:Device) = {
    get { return device.isActivated }
    set { 
        if (device.isActivated == false) {
            println("The " + device.name + " is now turned on.")
        } else {
            println("The " + device.name + " was already turned on.")
        }
        device.isActivated = true 
    }
}

pred isDeactivated(device:Device) = {
    get { return !device.isActivated }
    set { 
        if (device.isActivated == true) {
            println("The " + device.name + " is now turned off.")
        } else {
            println("The " + device.name + " was already turned off.")
        }
        device.isActivated = false 
    }
}

// Heating (stove)
pred heatTransferInHeatSource(obj:Any, heater:HeatSource) = {
    get { return false }
    set {
        var MAX_TEMP_DELTA_PER_TICK     =   10
        //println("heater: ")
        //println(heater)
        if (heater.isActivated == true) {
            var objTemp = obj.temperature
            var heaterTemp = heater.heatingTemp
            if (objTemp < heaterTemp) {
                var delta = heaterTemp - objTemp
                //var inc = min(delta, MAX_TEMP_DELTA_PER_TICK)     // linear
                var inc = delta * 0.10                              // percentage-based
                obj.temperature = obj.temperature + inc
                printlnLog ("Heat transfer: Object (" + obj.name + ") temperature Now " + str(obj.temperature))
            }
        }
    }
}

// This is essentially conductive heat transfer 
// Precondition -- assumes objects are touching
pred heatTransferTouchingObjects(obj1:Any, obj2:Any) = {
    get { return false }
    set {
        var NODATA = -999
        var DEFAULT_THERMAL_CONDUCTIVITY = 1.0
        var MAX_TEMP_DELTA_PER_TICK = 10.0          // Max temp change per tick

        // Get object temperatures
        var obj1Temp = NODATA
        var obj2Temp = NODATA
        if (hasProperty(obj1, "temperature") == true) { obj1Temp = obj1.temperature }
        if (hasProperty(obj2, "temperature") == true) { obj2Temp = obj2.temperature }

        if ((obj1Temp != NODATA) && (obj2Temp != NODATA)) {
            printlnLog ("Heat transfer between (" + obj1.name + ") and (" + obj2.name + ")")
            printlnLog  ("TEST: obj1Temp: " + str(obj1Temp) + "   obj2Temp: " + str(obj2Temp))
            if (obj1Temp == obj2Temp) {         // Exit early if the temperatures are the same
                printlnLog ("\tSame temp")
                return true
            }
            
            var obj1ThermalConductivity = DEFAULT_THERMAL_CONDUCTIVITY
            var obj2ThermalConductivity = DEFAULT_THERMAL_CONDUCTIVITY
            if (hasProperty(obj1, "thermalConductivy") == true) { obj1ThermalConductivity = obj1.thermalConductivy }
            if (hasProperty(obj2, "thermalConductivy") == true) { obj2ThermalConductivity = obj2.thermalConductivy }

            var minThermalConductivity = min(obj1ThermalConductivity, obj2ThermalConductivity)
            printlnLog  ("minThermalConductivity: " + str(minThermalConductivity))

            // Temperature change for object 1 
            var delta1 = (obj2Temp - obj1Temp) * minThermalConductivity
            /*
            // Linear
            var inc1 = 0            
            if (delta1 > 0) {
                inc1 = min(delta1, MAX_TEMP_DELTA_PER_TICK)                
            } else {
                inc1 = max(delta1, MAX_TEMP_DELTA_PER_TICK*-1)                
            } 
            */       
            var inc1 = delta1 * 0.10        // Percentage based     
            obj1.temperature = obj1.temperature + inc1

            // Temperature change for object 2
            var delta2 = (obj1Temp - obj2Temp) * minThermalConductivity
            /*
            // Linear
            var inc2 = 0
            inc2 = min(delta2, MAX_TEMP_DELTA_PER_TICK)                            
            if (delta2 > 0) {
                inc2 = min(delta2, MAX_TEMP_DELTA_PER_TICK)                
            } else {
                inc2 = max(delta2, MAX_TEMP_DELTA_PER_TICK*-1)                
            } 
            */       
            var inc2 = delta2 * 0.10        // Percentage based    
            obj2.temperature = obj2.temperature + inc2
            
            printlnLog ("Heat transfer (conductive - 1): Object (" + obj1.name + ") temperature from " + str(obj1Temp) + " to " + str(obj1.temperature) + "   (delta: " + str(delta1) + ", inc: " + str(inc1) + ")")
            printlnLog ("Heat transfer (conductive - 2): Object (" + obj2.name + ") temperature from " + str(obj2Temp) + " to " + str(obj2.temperature) + "   (delta: " + str(delta2) + ", inc: " + str(inc2) + ")")
        }
    }

}


pred ChangeOfState(obj:Any) = {
    get {         
        var NODATA = -999

        // First, check to see if an object has required properties
        // If no temperature, then fail
        if (hasProperty(obj, "temperature") == false) { return false }
        var temperature = obj.temperature
        
        // Melting point
        var meltingPoint = NODATA
        if (hasProperty(obj, "meltingPoint") == true) {
            meltingPoint = obj.meltingPoint
        } else {
            if (hasProperty(obj, "madeOf") == true) {
                var madeOf = obj.madeOf
                if (hasProperty(madeOf, "meltingPoint") == true) {
                    meltingPoint = madeOf.meltingPoint
                }
            }
        }

        // Boiling point
        var boilingPoint = NODATA
        if (hasProperty(obj, "boilingPoint") == true) {
            boilingPoint = obj.boilingPoint
        } else {
            if (hasProperty(obj, "madeOf") == true) {
                var madeOf = obj.madeOf
                if (hasProperty(madeOf, "boilingPoint") == true) {
                    boilingPoint = madeOf.boilingPoint
                }
            }
        }

        // State of matter
        var stateOfMatter = ""
        if (hasProperty(obj, "stateOfMatter") == true) {
            stateOfMatter = obj.stateOfMatter
        }
        
        if ((meltingPoint == NODATA) && (boilingPoint == NODATA)) { return false }

        // Infer what the state of matter should be based on temperature
        var shouldBeState = ""
        if (meltingPoint != NODATA) {
            // Is a solid?
            if (temperature <= meltingPoint) {
                shouldBeState = "solid"
            }
            if (temperature > meltingPoint) {
                if (boilingPoint != NODATA) {
                    if (temperature < boilingPoint) {
                        // meltingPoint < temp < boilingPoint, so the state is a liquid
                        shouldBeState = "liquid"
                    } else {
                        // temperature is greater than the melting point, so gas
                        shouldBeState = "gas"
                    }
                } else {
                    // No boiling point known, so make it a liquid (since we don't have an upper limit to make it a gas)
                    shouldBeState = "liquid"
                }
            }
        } else {
            if (boilingPoint != NODATA) {
                if (temperature < boilingPoint) {
                    // Assume liquid, since temperature is less than boiling point, but no melting point is known
                    shouldBeState = "liquid"
                } else {
                    // temperature is greater than the melting point, so gas
                    shouldBeState = "gas"
                }
            }
        }

        // Then, check to see if the current state of matter is different from what we think it should be
        //println("SOM: curState: " + stateOfMatter + "  shouldBeState: " + shouldBeState + "  "  + str(boilingPoint) + "    " + str(temperature) + "   " + str(obj))
        if (shouldBeState != stateOfMatter) {
            // Return true -- a state of matter change should happen
            return true
        } else {
            // State of matter is the same as inferred state
            return false
        }
    }

    set {
        var NODATA = -999

        // First, check to see if an object has required properties
        // If no temperature, then fail
        if (hasProperty(obj, "temperature") == false) { return false }
        var temperature = obj.temperature
        
        // Melting point
        var meltingPoint = NODATA
        if (hasProperty(obj, "meltingPoint") == true) {
            meltingPoint = obj.meltingPoint
        } else {
            if (hasProperty(obj, "madeOf") == true) {
                var madeOf = obj.madeOf
                if (hasProperty(madeOf, "meltingPoint") == true) {
                    meltingPoint = madeOf.meltingPoint
                }
            }
        }

        // Boiling point
        var boilingPoint = NODATA
        if (hasProperty(obj, "boilingPoint") == true) {
            boilingPoint = obj.boilingPoint
        } else {
            if (hasProperty(obj, "madeOf") == true) {
                var madeOf = obj.madeOf
                if (hasProperty(madeOf, "boilingPoint") == true) {
                    boilingPoint = madeOf.boilingPoint
                }
            }
        }

        // State of matter
        var stateOfMatter = ""
        if (hasProperty(obj, "stateOfMatter") == true) {
            stateOfMatter = obj.stateOfMatter
        }
        
        if ((meltingPoint == NODATA) && (boilingPoint == NODATA)) { return false }

        // Infer what the state of matter should be based on temperature
        var shouldBeState = ""
        if (meltingPoint != NODATA) {
            // Is a solid?
            if (temperature <= meltingPoint) {
                shouldBeState = "solid"
            }
            if (temperature > meltingPoint) {
                if (boilingPoint != NODATA) {
                    if (temperature < boilingPoint) {
                        // meltingPoint < temp < boilingPoint, so the state is a liquid
                        shouldBeState = "liquid"
                    } else {
                        // temperature is greater than the melting point, so gas
                        shouldBeState = "gas"
                    }
                } else {
                    // No boiling point known, so make it a liquid (since we don't have an upper limit to make it a gas)
                    shouldBeState = "liquid"
                }
            }
        } else {
            if (boilingPoint != NODATA) {
                if (temperature < boilingPoint) {
                    // Assume liquid, since temperature is less than boiling point, but no melting point is known
                    shouldBeState = "liquid"
                } else {
                    // temperature is greater than the melting point, so gas
                    shouldBeState = "gas"
                }
            }
        }

        // Then, check to see if the current state of matter is different from what we think it should be
        //println("SOM: curState: " + stateOfMatter + "  shouldBeState: " + shouldBeState + "  "  + str(boilingPoint) + "    " + str(temperature) + "   " + str(obj))
        if (shouldBeState != stateOfMatter) {
            // Return true -- a state of matter change should happen  
            var container = obj.container                                                          
            println ("The " + obj.name + " in/on the " + container.name + " turns into a " + shouldBeState + ".")            
            obj.stateOfMatter = shouldBeState            
        }
    }
}


@actions
action actionOpenDoor(agent:Agent, door:Door) {
    trigger: "open" + door
}
action actionCloseDoor(agent:Agent, door:Door) {
    trigger: "close" + door
}
action actionMoveThroughDoor(agent:Agent, door:Door) {
    trigger: "go through|walk through|move through" + door
    trigger: "go to|walk to|move to" + door
}
action actionEat(agent:Agent, food:Food) {
    trigger: "eat|consume" + food
}
action actionLookAround(agent:Agent) {
    trigger: "look|look around"
}
action actionActivate(agent:Agent, device:Device) {
    trigger: "activate|turn on" + device
}
action actionDeactivate(agent:Agent, device:Device) {
    trigger: "deactivate|turn off" + device
}

action actionMove(agent:Agent, obj:Any, moveTo:Any) { 
    trigger: "move" + obj + "to" + moveTo
}


@rules

// TODO: Implement strong type checking for rules, checking arguments (check all predicate arguments are populated, check no predicate arguments are missing, etc) 

//rule open_closed_doors(d:Door, a:Agent, r:Room) :: at(thing=a, location=r) * at(thing=d, location=r) * isClosed(door=d) -o isOpen(door=d)
rule open_doors(d:Door, a:Agent, r:Room) :: at(thing=a, location=r) * at(thing=d, location=r) * actionOpenDoor(agent=a, door=d) -o isOpen(door=d)
rule close_doors(d:Door, a:Agent, r:Room) :: at(thing=a, location=r) * at(thing=d, location=r) * actionCloseDoor(agent=a, door=d) -o isClosed(door=d)
//rule move_through_door(d:Door, a:Agent, r:Room) :: at(thing=a, location=r) * at(thing=d, location=r) * isOpen(door=d) -o moveThroughDoor(agent=a, door=d)
rule move_through_door(d:Door, a:Agent, r:Room) :: at(thing=a, location=r) * at(thing=d, location=r) * isOpen(door=d) * actionMoveThroughDoor(agent=a, door=d) -o moveThroughDoor(agent=a, door=d)
//rule errortest(d:Door, a:Agent, r:Room) :: at(thing=a) * isOpen(door=d, window=w) -o moveThroughDoor(agent=a, light=l)
rule look_around(a:Agent, r:Room) :: at(thing=a, location=r) * actionLookAround(agent=a) -o lookAround(agent=a, location=r)

// Moving objects
rule move_objects(a:Agent, o:Object, r:Location, d:Object) :: at(thing=a, location=r) * at(thing=o, location=r) * at(thing=d, location=r) * actionMove(agent=a, obj=o, moveTo=d) -o move(thing=o, destination=d)

// Eating
rule eat_test(f:Food, a:Agent) :: actionEat(agent=a, food=f) -o doEat(agent=a, food=f)

// Activate devices
rule activate_device(a:Agent, d:Device) :: at(thing=a, location=r) * at(thing=d, location=r) * isActivable(device=d) * isDeactivated(device=d) * actionActivate(agent=a, device=d) -o isActivated(device=d)
rule deactivate_device(a:Agent, d:Device) :: at(thing=a, location=r) * at(thing=d, location=r) * isActivable(device=d) * isActivated(device=d) * actionDeactivate(agent=a, device=d) -o isDeactivated(device=d)

// Heating
rule heat_stove(o:Object, h:HeatSource) :: isIn(thing=o, container=h) * isActivated(device=h) -o heatTransferInHeatSource(obj=o, heater=h)
//rule heat_stove_shouldfail(o:Object, h:HeatSource) :: isIn(thing=o, container=h) * isActivated(device=d) -o heatTransfer(obj=o, heater=h)

// Heating (conductive transfer, between objects in the same container)
rule heat_conductive(o1:Object, o2:Object, c:Object) :: at(thing=o1, location=c) * at(thing=o2, location=c) -o heatTransferTouchingObjects(obj1=o1, obj2=o2)
// Heating (between container-object)
rule heat_conductive(o1:Object, o2:Object, c:Object) :: at(thing=o1, location=o2) -o heatTransferTouchingObjects(obj1=o1, obj2=o2)

// Change of state
rule ChangeOfState(o:Object) :: ChangeOfState(obj=o) -o ChangeOfState(obj=o)




@init

println("Initializing...")

var roomKitchen = new Room()
roomKitchen.name = "Kitchen"
addObjToWorld(roomKitchen)

var room2 = new Room()
room2.name = "Living Room"
addObjToWorld(room2)

var room3 = new Room()
room3.name = "Hallway"
addObjToWorld(room3)

//var a1 = new Agent()                              
//var doorR1R3 = new Door(connectsTo = a1)              // Should throw a type error

var doorR1R3 = new Door(connectsTo = room3)
moveObj(doorR1R3 to roomKitchen)

var doorR3R1 = new Door(connectsTo = roomKitchen)
moveObj(doorR3R1 to room3)

var doorR3R2 = new Door(connectsTo = room2)
moveObj(doorR3R2 to room3)

var doorR2R3 = new Door(connectsTo = room3)
moveObj(doorR2R3 to room2)

var apple = new Apple()
moveObj(apple to room3)


var metalPot = new MetalPot(location=roomKitchen)
moveObj(metalPot to roomKitchen)

var sink = new Sink()
moveObj(sink to roomKitchen)

var stove = new Stove()
moveObj(stove to roomKitchen)


var agent = new Agent()
agent.name = "The Agent"
moveObj(agent to roomKitchen)

agent.inheritTest = "failure test"		// Should throw a warning that this is overwriting an auto-populated property


//## Test
var water = new Water()
moveObj(metalPot to stove)
moveObj(water to metalPot)
stove.isActivated = true

//## Another test
/*
var cup = new GlassCup(location=roomKitchen)
var water2 = new Water()
water2.temperature = 500
moveObj(water2 to cup)
var steel = new Steel()
steel.temperature = 10
moveObj(steel to cup)

moveObj(cup to roomKitchen)
*/


// Language tests
//requestAction actionOpenDoor(agent=agent, door=doorR1R3)  // This is an example of queueing an action
//requestAction openDoor(agent=room1, door=doorR1R3)		// This should create a type error
/*
println("##Built-in")
for (i <- 0 until 10) {
	var a = "123.456"
	println( i )
	println( a )
	println( i + round(a) )
	println( i + floor(a) )
	println( "num: " + str(i) )
}
for (obj <- roomKitchen) {
	println(obj)
}

println ( min(10, 20, 30) )
println ( max(10, 20, 30) )
println ( pow(10, 2) )
println ( sqrt(9) )
println ( "abc" + str(sin(90)) )		// Nested

var testArray = Array(1, 2, 3)
println(testArray)
println(testArray[1])
testArray[1] = "hi"
println(testArray)
println(testArray[0])
println(testArray[1])
println(testArray[2])
//println(testArray[3])       // Should throw error
//testArray[3] = "hi"         // Should throw error

testArray.append("appendTest")
println(testArray)

testArray.remove(0)
println(testArray)

testArray.remove(2)
println(testArray)

println(len(testArray))


testArray.append("test")
testArray.append("abc")
testArray.append(456)
for (i <- 0 until len(testArray)) {
    println(str(i) + " : " + str(testArray[i]))    
}

println(contains(testArray, "test"))
println(contains(testArray, 456))
println(contains(testArray, 123))

println("Iterator test:")
for (elem <- testArray) {
    println(elem)    
}

println("Iterator test2:")
for (elem <- roomKitchen) {
    println(elem)    
}


for (i <- 0 until len(testArray)) {
    testArray.remove(0)
    println(testArray)
    println("isEmpty: " + str(empty(testArray)))    
}

println(type(roomKitchen))


var test = true
println(test)
println(!test)

//exit(1)
*/

// Set main agent
setAgent(agent)
//exit(1)
