# python-api.py
#
#   conda create --name virtualenv-scala python=3.8
#   conda activate virtualenv-scala
#   pip install py4j                                (for scala-python interface)
#   pip install -U pywebio                          (for web server)

from py4j.java_gateway import JavaGateway, GatewayParameters
import subprocess

import time
import json

# Web interface
#from pywebio.input import *
#from pywebio.output import *

class VirtualEnv:

    #
    # Constructor
    #
    def __init__(self, scriptFilename, serverPath):
        self.scriptFilename = scriptFilename

        # Launch the server
        self.launchServer(serverPath)

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
    def launchServer(self, serverPath):
        # /home/ruoyao/Documents/projects/virtualenv-scala2/python-api/virtualenv-scala-assembly-1.0.jar            
        cmd = f"nohup java -cp {serverPath} scienceworld.runtime.pythonapi.PythonInterface >/dev/null 2>&1 &"
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
        observation, score, isCompleted, info = self.step("look around")

        # Return a tuple that looks like the Jericho signiture for reset
        return observation, info


    # Shutdown the scala server
    def shutdown(self):
        self.gateway.shutdown()


    # Get a list of valid tasks/environments
    def getTaskNames(self):
        return self.gateway.getTaskNames()


    # Get possible actions
    def getPossibleActions(self):
        return self.gateway.getPossibleActions()

    # Get possible actions (and also include the template IDs for those actions)
    def getPossibleActionsWithIDs(self):
        jsonStr = self.gateway.getPossibleActionsWithIDs()
        data = json.loads(jsonStr)
        return data

    # Get possible objects
    def getPossibleObjects(self):
        return self.gateway.getPossibleObjects()

    # Get possible action/object combinations
    def getPossibleActionObjectCombinations(self):        
        combinedJSON = self.gateway.getPossibleActionObjectCombinationsJSON()
        data = json.loads(combinedJSON)
        templates = data['templates']
        lookUpTable = data['lookUpTable']

        return (templates, lookUpTable)

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
        numMoves = self.getNumMoves()

        return observation, score, isCompleted, {'moves': numMoves, 'score': score}



