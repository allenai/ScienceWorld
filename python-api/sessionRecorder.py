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
import json
from os.path import exists


# Example user input console, to play through a game. 
def userConsole(jarPath:str):
    exitCommands = ["quit", "exit"]

    history = []

    # Initialize environment
    env = VirtualEnv("", jarPath, threadNum = 0)
    #taskName = env.getTaskNames()[0]        # Just get first task    

    # Select task
    taskNames = env.getTaskNames()
    print("Select task: ")
    for i in range(0, len(taskNames)):
        print(str(i) + ": \t" + str(taskNames[i]))
    print("Enter task number:")
    taskNumber = input('> ')

    # Verify input
    try:
        taskIdx = int(taskNumber)
    except ValueError:
        print("ERROR: Unknown task number")
        exit(1)

    if (taskIdx < 0) or (taskIdx >= len(taskNames)):
        print("ERROR: Task number is out of range")
        exit(1)


    # Load task
    taskName = env.getTaskNames()[taskIdx]
    env.load(taskName)
    initialObs, initialDict = env.reset()
    
    #print("Possible actions: " + str(env.getPossibleActions()) )
    #print("Possible objects: " + str(env.getPossibleObjects()) )
    templates, lut = env.getPossibleActionObjectCombinations()
    #print("Possible action/object combinations: " + str(templates))
    #print("Object IDX to Object Referent LUT: " + str(lut))
    #print("Vocabulary: " + str(env.getVocabulary()) )
    #print("Possible actions (with IDs): " + str(env.getPossibleActionsWithIDs()))
    #print("Possible object types: " + str(env.getObjectTypes()))    
    #print("Object IDX to Object Referent LUT: " + str(lut))
    #print("\n")
    taskDescription = env.getTaskDescription()
    print("Task Description: " + str(taskDescription) )    
    

    userInputStr = "look around"        # First action
    curIter = 0
    while (userInputStr not in exitCommands):        
        print ("------------------")
        print ("  Iteration " + str(curIter))
        print ("------------------")

        # Send user input, get response
        observation, score, isCompleted, additionalInfo = env.step(userInputStr)
        print("\n" + observation)
        print("Score: " + str(score))
        print("isCompleted: " + str(isCompleted))
        print("AdditionalInfo: " + str(additionalInfo))

        # Store user input/observation in history
        historyElem = {
            'observation': observation,
            'input': userInputStr,
            'score': score,
            'iterations': curIter
        }
        history.append(historyElem)

        # Get user input
        userInputStr = input('> ')
        # Sanitize input
        userInputStr = userInputStr.lower().strip()

        curIter += 1

    # Shut down server
    print("Shutting down server...")    
    env.shutdown()

    # Save history
    print("Save file")
    if (len(history) > 2):        
        
        # Find an unused save filename
        fileIdx = 0        
        fileExists = True
        saveFilename = ""
        savePath = "saves/"
        while (fileExists and fileIdx < 100):            
            saveFilename = savePath + "save-" + str(taskName) + "-record" + str(fileIdx) + ".json"
            if (exists(saveFilename) == False):
                fileExists = False
            fileIdx += 1

        # If we reach here, check to see if the filename is valid
        if (fileExists == True):
            println ("Maximum number of saves reached.")
        else:
            # Save file
            print("Writing " + saveFilename)

            # Pack
            packed = {
                'taskName': taskName,
                'taskDesc': taskDescription,
                'history': history
            }

            # Save file
            with open(saveFilename, 'w') as f:
                json.dump(packed, f, indent=4)
            
            

    print("Completed.")




#
#   Main
#
def main():    
    jarPath = "virtualenv-scala-assembly-1.0.jar"
    #jarPath = "/home/ruoyao/Documents/projects/virtualenv-scala2/python-api/virtualenv-scala-assembly-1.0.jar"

    print("Virtual Text Environment API demo")

    # Run a user console
    userConsole(jarPath)

    print("Exiting.")

if __name__ == "__main__":
    main()