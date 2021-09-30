# python-api.py
#
#   conda create --name virtualenv-scala python=3.8
#   conda activate virtualenv-scala
#   pip install py4j

from py4j.java_gateway import JavaGateway, GatewayParameters
import time
import timeit
import subprocess
import py4j

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
        self.load()

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
    def load(self):
        # TODO: Error handling
        print("Load: " + self.scriptFilename)
        self.gateway.load(self.scriptFilename)


    # Ask the simulator to reset an environment back to it's initial state
    def reset(self):
        self.gateway.reset()

    # Shutdown the scala server
    def shutdown(self):
        self.gateway.shutdown()



    # Get possible actions
    def getPossibleActions(self):
        return self.gateway.getPossibleActions()

    # Get possible objects
    def getPossibleObjects(self):
        return self.gateway.getPossibleObjects()

    # Get possible action/object combinations
    def getPossibleActionObjectCombinations(self):
        return self.gateway.getPossibleActionObjectCombinations()



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

# Example of creating an environment, then taking one step
def example1(scriptFilename:str):    
    env = VirtualEnv(scriptFilename)
    env.reset()
    observation, score, isCompleted = env.step("look around")
    print(observation)

def speedTest(scriptFilename:str):
    exitCommands = ["quit", "exit"]
    # Initialize environment
    env = VirtualEnv(scriptFilename)
    env.reset()

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
def userConsole(scriptFilename:str):
    exitCommands = ["quit", "exit"]
    # Initialize environment
    env = VirtualEnv(scriptFilename)
    env.reset()
    
    print("Possible actions: " + str(env.getPossibleActions()) )
    print("Possible objects: " + str(env.getPossibleObjects()) )
    print("Possible action/object combinations: " + str(env.getPossibleActionObjectCombinations()))
    

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
    scriptFilename = "../tests/test4.scala"

    print("Virtual Text Environment API demo")

    # Run a user console
    userConsole(scriptFilename)

    # Run speed test
    #speedTest(scriptFilename)

    print("Exiting.")

if __name__ == "__main__":
    main()