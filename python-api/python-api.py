# python-api.py
#
#   conda create --name virtualenv-scala python=3.8
#   conda activate virtualenv-scala
#   pip install py4j

from py4j.java_gateway import JavaGateway
import time
import subprocess

class VirtualEnv:

    #
    # Constructor
    #
    def __init__(self, scriptFilename):
        self.scriptFilename = scriptFilename

        # Launch the server
        self.launchServer()

        # Connect to the JVM
        self.gateway = JavaGateway()

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
        cmd = "nohup java -cp virtualenv-scala-assembly-1.0.jar language.runtime.pythonapi.PythonInterface >/dev/null 2>&1 &"
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

    # Step
    def step(self, inputStr:str):
        observation = self.gateway.step(inputStr)
        return observation



#
#   Examples
#

# Example of creating an environment, then taking one step
def example1(scriptFilename:str):    
    env = VirtualEnv(scriptFilename)
    env.reset()
    observation = env.step("look around")
    print(observation)

# Example user input console, to play through a game. 
def userConsole(scriptFilename:str):
    exitCommands = ["quit", "exit"]
    # Initialize environment
    env = VirtualEnv(scriptFilename)
    env.reset()

    userInputStr = "look around"        # First action
    while (userInputStr not in exitCommands):
        # Send user input, get response
        observation = env.step(userInputStr)
        print("\n" + observation)

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

    print("Exiting.")

if __name__ == "__main__":
    main()