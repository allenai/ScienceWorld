#
#   Examples
#
#   conda create --name virtualenv-scala python=3.8
#   conda activate virtualenv-scala
#   pip install py4j                                (for scala-python interface)
#   pip install -U pywebio                          (for web server)


from python_api import VirtualEnv
import random
import timeit
import time

def speedTest(jarPath:str):
    exitCommands = ["quit", "exit"]

    # Initialize environment    
    env = VirtualEnv("", jarPath, threadNum = 0)
    taskName = env.getTaskNames()[0]        # Just get first task    
    env.load(taskName)
    initialObs, initialDict = env.reset()

    numEpochs = 1000

    start = timeit.default_timer()
    userInputStr = "look around"        # First action
    for i in range(0, numEpochs):
        # Send user input, get response    
        observation, score, isCompleted, _ = env.step(userInputStr)
        
    end = timeit.default_timer()
    deltaTime = end - start
    print("Runtime: " + str(deltaTime) + " seconds")
    print("Rate: " + str(numEpochs / deltaTime) + " epochs/second")

    print("Shutting down server...")    
    #env.shutdown()

    print("Completed.")

# Example user input console, to play through a game. 
def randomModel(jarPath:str):
    exitCommands = ["quit", "exit"]

    # Initialize environment    
    env = VirtualEnv("", jarPath, threadNum = 0)

    taskName = env.getTaskNames()[0]        # Just get first task    
    env.load(taskName)
    
    initialObs, initialDict = env.reset()

    print("Possible actions: " + str(env.getPossibleActions()) )
    print("Possible objects: " + str(env.getPossibleObjects()) )
    templates, lut = env.getPossibleActionObjectCombinations()

    #print("Possible action/object combinations: " + str(templates))
    #print("Object IDX to Object Referent LUT: " + str(lut))
    
    score = 0.0
    isCompleted = False
    curIter = 0
    maxIter = 1000

    userInputStr = "look around"        # First action
    while (userInputStr not in exitCommands) and (isCompleted == False) and (curIter < maxIter):
        print("----------------------------------------------------------------")
        print ("Iteration: " + str(curIter))

        ## DEBUG
        if (curIter % 30 == 0 and curIter != 0):
            initialObs, initialDict = env.reset()
            
            print("RESETTING")
            print(initialObs)


        # Send user input, get response
        observation, score, isCompleted, _ = env.step(userInputStr)
        print("\n>>> " + observation)
        print("Score: " + str(score))
        print("isCompleted: " + str(isCompleted))

        if (isCompleted):
            break

        # Randomly select action        
        templates, lut = env.getPossibleActionObjectCombinations()
        print(list(lut.keys())[-1])
        #print("Possible action/object combinations: " + str(templates))
        #print("Object IDX to Object Referent LUT: " + str(lut))

        randomTemplate = random.choice( templates )        
        print(randomTemplate)
        userInputStr = randomTemplate["action"]

        # Sanitize input
        userInputStr = userInputStr.lower().strip()
        print("Choosing random action: " + str(userInputStr))

        curIter += 1

        #if (curIter > 30):
        #    time.sleep(1)

        
    # Report progress of model
    if (curIter == maxIter):
        print("Maximum number of iterations reached (" + str(maxIter) + ")")
    print ("Final score: " + str(score))
    print ("isCompleted: " + str(isCompleted))

    print("Shutting down server...")    
    #env.shutdown()

    print("Completed.")


# Example user input console, to play through a game. 
def userConsole(jarPath:str):
    exitCommands = ["quit", "exit"]

    # Initialize environment
    env = VirtualEnv("", jarPath, threadNum = 0)
    taskName = env.getTaskNames()[0]        # Just get first task    
    env.load(taskName)
    initialObs, initialDict = env.reset()
    
    print("Possible actions: " + str(env.getPossibleActions()) )
    print("Possible objects: " + str(env.getPossibleObjects()) )
    templates, lut = env.getPossibleActionObjectCombinations()
    print("Possible action/object combinations: " + str(templates))
    #print("Object IDX to Object Referent LUT: " + str(lut))
    print("Vocabulary: " + str(env.getVocabulary()) )
    print("Possible actions (with IDs): " + str(env.getPossibleActionsWithIDs()))
    print("Possible object types: " + str(env.getObjectTypes()))    
    print("Object IDX to Object Referent LUT: " + str(lut))
    print("\n")
    print("Possible object referents LUT: " + str(env.getPossibleObjectReferentLUT()))
    print("\n")
    print("Valid action-object combinations: " + str(env.getValidActionObjectCombinations()))
    print("\n")
    print("Object_ids to type_ids: " + str(env.getAllObjectTypesLUTJSON()))
    print("\n")
    print("All objects, their ids, types, and referents: " + str(env.getAllObjectIdsTypesReferentsLUTJSON() ))
    print("\n")
    print("Valid action-object combinations (with templates): " + str(env.getValidActionObjectCombinationsWithTemplates() ))
    print("\n")
    print("Task Description: " + str(env.getTaskDescription()) )    

    userInputStr = "look around"        # First action
    while (userInputStr not in exitCommands):
        # Send user input, get response
        observation, score, isCompleted, additionalInfo = env.step(userInputStr)
        print("\n" + observation)
        print("Score: " + str(score))
        print("isCompleted: " + str(isCompleted))
        print("AdditionalInfo: " + str(additionalInfo))

        # Get user input
        userInputStr = input('> ')
        # Sanitize input
        userInputStr = userInputStr.lower().strip()

    print("Shutting down server...")    
    #env.shutdown()

    print("Completed.")




#
#   Main
#
def main():    
    jarPath = "virtualenv-scala-assembly-1.0.jar"
    #jarPath = "/home/ruoyao/Documents/projects/virtualenv-scala2/python-api/virtualenv-scala-assembly-1.0.jar"

    print("Virtual Text Environment API demo")

    # Run a user console
    #userConsole(jarPath)

    # Run speed test
    #speedTest(jarPath)

    # Run a model that chooses random actions until successfully reaching the goal
    randomModel(jarPath)

    print("Exiting.")

if __name__ == "__main__":
    main()