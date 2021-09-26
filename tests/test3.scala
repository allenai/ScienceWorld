
// Open-a-closed-door then move-through-an-open-door test

//import "test1-import.env"
//import "test2.txt"

@defines
//#defineprop closed CLOSEDNAMESUB

@classes 


class Object extends Any {
    constructor() { }

	prop abcTestProp = "TestProp123"		// Default property value	
	
    prop referents = "object"

	prop description = {
		return this.name + ", a kind of " + this.type
	}
}

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

class Door extends Object {
    constructor(connectsTo:Room) { 
        println("Door being initialized that connects to " + str(connectsTo))        
        this.connectsTo = connectsTo
    }

	// Default property values
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

class Room extends Object {
    constructor() { }

    prop name = ""
    prop referents = { 
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
class Agent extends Object {
    constructor() { }

    prop referents = "agent"
	prop inheritTest = { return "inherit!" }	
}


@predicates 

/*
def atOld(thing:Agent, location:Room) = {
    get {
        println("Checking if thing in location...")
        if (thing in location) {
            println("---Thing is in location...")
            return true
        }
        return false        
    }
    set {
        moveObj(thing to location)
    }
}
*/

def at(thing:Any, location:Any) = {
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
        moveObj(thing to location)
    }
}

def moveThroughDoor(agent:Agent, door:Door) = {
    get {
        return false
    }
    set {
        var connectsTo = door.connectsTo
        println ("Moving through door, to " + connectsTo.name)
        moveObj(agent to door.connectsTo)
    }
}

def isClosed(door:Door) = {     
    get { 
        if (door.isOpen == false) { return true }           // TODO: Add negation marker :) (e.g. !door.isOpen)
        return false
    }
    set {
        door.isOpen = false
        println("Door set to closed.")
    }
}

def isOpen(door:Door) = {
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

def doEat(agent:Agent, food:Food) = {
    get { return false }
    set {        
        println ("The " + food.name + " was delicious.")
        agent.hungry = false
        deleteObject(food)
        // TODO: Remove object (food) from environment
    }
}

def lookAround(agent:Agent, location:Room) = {
    get { return false }
    set {
        println ("Look Around:")
        println (location.description)
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

@rules

// TODO: Implement strong type checking for rules, checking arguments (check all predicate arguments are populated, check no predicate arguments are missing, etc) 

//rule open_closed_doors(d:Door, a:Agent, r:Room) :: at(thing=a, location=r) * at(thing=d, location=r) * isClosed(door=d) -o isOpen(door=d)
rule open_doors(d:Door, a:Agent, r:Room) :: at(thing=a, location=r) * at(thing=d, location=r) * actionOpenDoor(agent=a, door=d) -o isOpen(door=d)
rule close_doors(d:Door, a:Agent, r:Room) :: at(thing=a, location=r) * at(thing=d, location=r) * actionCloseDoor(agent=a, door=d) -o isClosed(door=d)
//rule move_through_door(d:Door, a:Agent, r:Room) :: at(thing=a, location=r) * at(thing=d, location=r) * isOpen(door=d) -o moveThroughDoor(agent=a, door=d)
rule move_through_door(d:Door, a:Agent, r:Room) :: at(thing=a, location=r) * at(thing=d, location=r) * isOpen(door=d) * actionMoveThroughDoor(agent=a, door=d) -o moveThroughDoor(agent=a, door=d)
//rule errortest(d:Door, a:Agent, r:Room) :: at(thing=a) * isOpen(door=d, window=w) -o moveThroughDoor(agent=a, light=l)
rule look_around(a:Agent, r:Room) :: at(thing=a, location=r) * actionLookAround(agent=a) -o lookAround(agent=a, location=r)


rule eat_test(f:Food, a:Agent) :: actionEat(agent=a, food=f) -o doEat(agent=a, food=f)


@init

println("test")

var room1 = new Room()
room1.name = "Office"
addObjToWorld(room1)

var room2 = new Room()
room2.name = "Living Room"
addObjToWorld(room2)

var room3 = new Room()
room3.name = "Hallway"
addObjToWorld(room3)

//var a1 = new Agent()                              
//var doorR1R3 = new Door(connectsTo = a1)              // Should throw a type error

var doorR1R3 = new Door(connectsTo = room3)
moveObj(doorR1R3 to room1)

var doorR3R1 = new Door(connectsTo = room1)
moveObj(doorR3R1 to room3)

var doorR3R2 = new Door(connectsTo = room2)
moveObj(doorR3R2 to room3)

var doorR2R3 = new Door(connectsTo = room3)
moveObj(doorR2R3 to room2)

var apple = new Apple()
moveObj(apple to room3)


var agent = new Agent()
agent.name = "The Agent"
moveObj(agent to room1)

agent.inheritTest = "failure test"		// Should throw a warning that this is overwriting an auto-populated property

//requestAction actionOpenDoor(agent=agent, door=doorR1R3)  // This is an example of queueing an action
//requestAction openDoor(agent=room1, door=doorR1R3)		// This should create a type error

println("##Built-in")
for (i <- 0 until 10) {
	var a = "123.456"
	println( i )
	println( a )
	println( i + round(a) )
	println( i + floor(a) )
	println( "num: " + str(i) )
}
for (obj <- room1) {
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
for (elem <- room1) {
    println(elem)    
}


for (i <- 0 until len(testArray)) {
    testArray.remove(0)
    println(testArray)
    println("isEmpty: " + str(empty(testArray)))    
}

println(type(room1))


//exit(1)


// Set main agent
setAgent(agent)
//exit(1)
