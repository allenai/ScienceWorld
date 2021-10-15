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
import py4j

# Web interface
from pywebio.input import *
from pywebio.output import *
from pywebio.input import textarea, input
from pywebio.session import *
from pywebio import start_server

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

        return observation, score, isCompleted




#
#   Web server main
#
def app():
    exitCommands = ["quit", "exit"]

    set_env(title='Awesome PyWebIO!!', auto_scroll_bottom=True)    

    # Initialize environment
    scriptFilename = ""     ## TODO

    env = VirtualEnv(scriptFilename)

    put_markdown('## Science World (Text Simulation)')

    taskName = select("Select a task:", env.getTaskNames())

    # Load environment
    env.load(taskName)
    initialObs, initialDict = env.reset()
    
    #print("Possible actions: " + str(env.getPossibleActions()) )
    #print("Possible objects: " + str(env.getPossibleObjects()) )
    #print("Possible action/object combinations: " + str(env.getPossibleActionObjectCombinations()))

    put_table([
        ["Task", env.getTaskDescription()]
    ])

    userInputStr = "look around"        # First action
    while (userInputStr not in exitCommands):
        put_markdown("### Move " + str(env.getNumMoves()) )

        # Send user input, get response
        observation, score, isCompleted = env.step(userInputStr)        
        
        put_text(observation)
        put_table([
            ["Score:", str(score)],
            ["isCompleted:", str(isCompleted)]
        ])

        #print("\n" + observation)
        #print("Score: " + str(score))
        #print("isCompleted: " + str(isCompleted))

        # Get user input
        userInputStr = input('Enter your action (`help` for list of actions, `objects` for list of object referents) ')
        
        # Sanitize input
        userInputStr = userInputStr.lower().strip()

        time.sleep(1)

    print("Shutting down server...")    
    #env.shutdown()

    print("Completed.")


    #text = textarea("Please insert the text for your PDF file", 
    #                placeholder="Type anything you like", 
    #                required=True)
                    
    #save_location = input("What is the name of your PDF file?", required=True)
    #put_text("Congratulations! A PDF file is generated for you.")


if __name__ == '__main__':
    start_server(app, port=36535, debug=True)
