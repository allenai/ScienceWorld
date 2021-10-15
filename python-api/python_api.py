# python-api.py
#
#   conda create --name virtualenv-scala python=3.8
#   conda activate virtualenv-scala
#   pip install py4j                                (for scala-python interface)
#   pip install -U pywebio                          (for web server)

from py4j.java_gateway import JavaGateway, GatewayParameters
import subprocess
import random
import timeit
import time
import json
import py4j

# Web interface
#from pywebio.input import *
#from pywebio.output import *

class VirtualEnv:

    #
    # Constructor
    #
    def __init__(self, scriptFilename):
        self.scriptFilename = scriptFilename

        # Launch the server
        self.launchServer()

        # Connect to the JVM
        self.gateway = JavaGateway(gateway_parameters=GatewayParameters(auto_field=True))

        # Load the script
        self.load(self.scriptFilename)

    #
    #   Destructor
    #
    def __del__(self):
        # Shutdown the server
        self.shutdown()



    #
    #   Methods
    #

    # Launches the PY4J server
    def launchServer(self):            
        cmd = "nohup java -cp virtualenv-scala-assembly-1.0.jar scienceworld.runtime.pythonapi.PythonInterface >/dev/null 2>&1 &"
        #"nohup usr/local/bin/otherscript.pl {0} >/dev/null 2>&1 &", shell=True
        subprocess.Popen(cmd, shell=True)
        time.sleep(1)

    # Ask the simulator to load an environment from a script
    def load(self, taskName):
        # TODO: Error handling
        self.scriptFilename = taskName

        print("Load: " + self.scriptFilename)
        self.gateway.load(self.scriptFilename)


    # Ask the simulator to reset an environment back to it's initial state
    def reset(self):
        self.gateway.reset()
        # Make first move
        observation, score, isCompleted = self.step("look around")
        # Also get the number of moves
        numMoves = self.getNumMoves()

        # Return a tuple that looks like the Jericho signiture for reset
        return observation, {'moves': numMoves, 'score': score}


    # Shutdown the scala server
    def shutdown(self):
        self.gateway.shutdown()


    # Get a list of valid tasks/environments
    def getTaskNames(self):
        return self.gateway.getTaskNames()


    # Get possible actions
    def getPossibleActions(self):
        return self.gateway.getPossibleActions()

    # Get possible objects
    def getPossibleObjects(self):
        return self.gateway.getPossibleObjects()

    # Get possible action/object combinations
    def getPossibleActionObjectCombinations(self):
        templatesJSON = self.gateway.getPossibleActionObjectCombinationsJSON()
        out = []
        for templateJSON in templatesJSON:            
            out.append( json.loads(templateJSON) )

        return out

    # Get the vocabulary of the model (at the current state)
    def getVocabulary(self):
        vocab = set()        

        # Action vocabulary
        for actionStr in self.getPossibleActions():
            for word in actionStr.split(" "):
                vocab.add(word)

        # Object vocabulary (keep as compound nouns?)                    
        vocabObjects = self.getPossibleObjects()
        vocab = vocab.union( set(vocabObjects) )
        
        return vocab


    def getNumMoves(self):
        return self.gateway.getNumMoves()

    def getTaskDescription(self):
        return self.gateway.getTaskDescription()


    # Step
    def step(self, inputStr:str):
        #observation, score, isCompleted = self.gateway.step(inputStr)
        observation = self.gateway.step(inputStr)
        score = self.gateway.getScore()
        isCompleted = self.gateway.getCompleted()

        return observation, score, isCompleted



#
#   Examples
#


def speedTest():
    exitCommands = ["quit", "exit"]

    # Initialize environment
    env = VirtualEnv("")
    taskName = env.getTaskNames()[0]        # Just get first task    
    env.load(taskName)
    initialObs, initialDict = env.reset()

    numEpochs = 1000

    start = timeit.default_timer()
    userInputStr = "look around"        # First action
    for i in range(0, numEpochs):
        # Send user input, get response
        observation, score, isCompleted = env.step(userInputStr)

    end = timeit.default_timer()
    deltaTime = end - start
    print("Runtime: " + str(deltaTime) + " seconds")
    print("Rate: " + str(numEpochs / deltaTime) + " epochs/second")

    print("Shutting down server...")    
    #env.shutdown()

    print("Completed.")

# Example user input console, to play through a game. 
def randomModel():
    exitCommands = ["quit", "exit"]

    # Initialize environment
    env = VirtualEnv("")
    taskName = env.getTaskNames()[0]        # Just get first task    
    env.load(taskName)
    initialObs, initialDict = env.reset()


    print("Possible actions: " + str(env.getPossibleActions()) )
    print("Possible objects: " + str(env.getPossibleObjects()) )
    #print("Possible action/object combinations: " + str(env.getPossibleActionObjectCombinations()))
    
    score = 0.0
    isCompleted = False
    curIter = 0
    maxIter = 1000

    userInputStr = "look around"        # First action
    while (userInputStr not in exitCommands) and (isCompleted == False) and (curIter < maxIter):
        print("----------------------------------------------------------------")
        print ("Iteration: " + str(curIter))

        # Send user input, get response
        observation, score, isCompleted = env.step(userInputStr)
        print("\n>>> " + observation)
        print("Score: " + str(score))
        print("isCompleted: " + str(isCompleted))

        if (isCompleted):
            break

        # Randomly select action

        possibleActionObjectCombinations = env.getPossibleActionObjectCombinations()
        randomTemplate = random.choice( possibleActionObjectCombinations )        
        print(randomTemplate)
        userInputStr = randomTemplate["action"]

        # Sanitize input
        userInputStr = userInputStr.lower().strip()
        print("Choosing random action: " + str(userInputStr))

        curIter += 1
        
    # Report progress of model
    if (curIter == maxIter):
        print("Maximum number of iterations reached (" + str(maxIter) + ")")
    print ("Final score: " + str(score))
    print ("isCompleted: " + str(isCompleted))

    print("Shutting down server...")    
    #env.shutdown()

    print("Completed.")


# Example user input console, to play through a game. 
def userConsole():
    exitCommands = ["quit", "exit"]

    # Initialize environment
    env = VirtualEnv("")
    taskName = env.getTaskNames()[0]        # Just get first task    
    env.load(taskName)
    initialObs, initialDict = env.reset()
    
    print("Possible actions: " + str(env.getPossibleActions()) )
    print("Possible objects: " + str(env.getPossibleObjects()) )
    print("Possible action/object combinations: " + str(env.getPossibleActionObjectCombinations()) )
    print("Vocabulary: " + str(env.getVocabulary()) )

    userInputStr = "look around"        # First action
    while (userInputStr not in exitCommands):
        # Send user input, get response
        observation, score, isCompleted = env.step(userInputStr)
        print("\n" + observation)
        print("Score: " + str(score))
        print("isCompleted: " + str(isCompleted))

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

    print("Virtual Text Environment API demo")

    # Run a user console
    userConsole()

    # Run speed test
    #speedTest()

    # Run a model that chooses random actions until successfully reaching the goal
    #randomModel()

    print("Exiting.")

if __name__ == "__main__":
    main()