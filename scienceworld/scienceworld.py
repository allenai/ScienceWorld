# scienceworld.py
#
#   conda create --name scienceworld python=3.8
#   conda activate scienceworld
#   pip install py4j                                (for scala-python interface)
#   pip install -U pywebio                          (for web server)

from py4j.java_gateway import JavaGateway, GatewayParameters
import subprocess

import os
import time
import json

BASEPATH = os.path.dirname(os.path.abspath(__file__))
JAR_PATH = os.path.join(BASEPATH, 'scienceworld-1.0.0.jar')


class ScienceWorldEnv:

    #
    # Constructor
    #
    def __init__(self, taskName, serverPath=None, envStepLimit=100, threadNum=0, launchServer=True):
        self.taskName = taskName
        serverPath = serverPath or JAR_PATH  # Use the builtin jar.

        # Define the port number
        self.portNum = 25335 + threadNum

        # Launch the server
        if (launchServer == True):
            self.launchServer(serverPath)

        # Connect to the JVM
        self.gateway = JavaGateway(gateway_parameters=GatewayParameters(auto_field=True, port=self.portNum))

        # Keep track of the last step score, to calculate reward from score
        self.lastStepScore = 0

        # Load the script
        self.load(self.taskName, 0, "")

        # Set the environment step limit
        self.envStepLimit = envStepLimit

        # Clear the run histories
        self.clearRunHistories()

        # By default, set that the gold path was not generated unless the user asked for it
        self.goldPathGenerated = False

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
        print("Launching ScienceWorld Server (Port " + str(self.portNum) + ") -- this may take a moment.")
        cmd = "nohup java -cp " + serverPath + " scienceworld.runtime.pythonapi.PythonInterface " + str(self.portNum) + " >/dev/null 2>&1 &"

        subprocess.Popen(cmd, cwd=BASEPATH, shell=True)
        # The sleep command here is to give time for the server process to spawn.
        # If you are spawning many threads simultaneously, you may need to increase this time.
        time.sleep(5)

    # Ask the simulator to load an environment from a script
    def load(self, taskName, variationIdx, simplificationStr, generateGoldPath=False):
        self.scriptFilename = taskName

        print("Load: " + self.scriptFilename + " (variation: " + str(variationIdx) + ")" + " (simplifications: " + simplificationStr + ")")

        is_electrical_task = "power-component" in taskName or "conductivity" in taskName
        if is_electrical_task and "noElectricalAction" in simplificationStr:
            msg = "Invalid simplification. Task '{}' requires electrical actions but '--no-electrical' was provided."
            raise ValueError(msg.format(taskName))

        self.gateway.load(self.scriptFilename, variationIdx, simplificationStr, generateGoldPath)

        # Reset last step score (used to calculate reward from current-previous score)
        self.lastStepScore = 0

        # Keep track of whether the gold path was generated, to generate verbose error messages
        self.goldPathGenerated = generateGoldPath


    # Ask the simulator to reset an environment back to it's initial state
    def reset(self):
        self.gateway.reset()

        # Reset last step score (used to calculate reward from current-previous score)
        self.lastStepScore = 0

        # Make first move
        observation, score, isCompleted, info = self.step("look around")

        # Return a tuple that looks like the Jericho signiture for reset
        return observation, info

    # Ask the simulator to reset an environment back to it's initial state
    def resetWithVariation(self, variationIdx, simplificationStr):
        self.load(self.scriptFilename, variationIdx, simplificationStr)

        # Reset last step score (used to calculate reward from current-previous score)
        self.lastStepScore = 0

        # Make first move
        observation, score, isCompleted, info = self.step("look around")

        # Return a tuple that looks like the Jericho signiture for reset
        return observation, info


    # Shutdown the scala server
    def shutdown(self):
        self.gateway.shutdown()


    # Simplifications
    def getSimplificationsUsed(self):
        return self.gateway.getSimplificationsUsed()

    def getPossibleSimplifications(self):
        return self.gateway.getPossibleSimplifications()


    # Get a list of valid tasks/environments
    def getTaskNames(self):
        return self.gateway.getTaskNames()

    # Get the maximum number of variations for this task
    def getMaxVariations(self, taskName):
        return self.gateway.getTaskMaxVariations(taskName)

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

    # Get a list of object_ids to unique referents
    def getPossibleObjectReferentLUT(self):
        jsonStr = self.gateway.getPossibleObjectReferentLUTJSON()
        data = json.loads(jsonStr)
        return data

    # As above, but dictionary is referenced by object type ID
    def getPossibleObjectReferentTypesLUT(self):
        jsonStr = self.gateway.getPossibleObjectReferentTypesLUTJSON()
        data = json.loads(jsonStr)
        return data

    # Get a list of *valid* agent-object combinations
    def getValidActionObjectCombinations(self):
        return self.gateway.getValidActionObjectCombinations()

    def getValidActionObjectCombinationsWithTemplates(self):
        jsonStr = self.gateway.getValidActionObjectCombinationsJSON()
        data = json.loads(jsonStr)
        return data['validActions']

    # Get a LUT of object_id to type_id
    def getAllObjectTypesLUTJSON(self):
        jsonStr = self.gateway.getAllObjectTypesLUTJSON()
        data = json.loads(jsonStr)
        return data

    # Get a LUT of {object_id: {type_id, referent:[]} } tuples
    def getAllObjectIdsTypesReferentsLUTJSON(self):
        jsonStr = self.gateway.getAllObjectIdsTypesReferentsLUTJSON()
        data = json.loads(jsonStr)
        return data

    # Get possible action/object combinations
    def getPossibleActionObjectCombinations(self):
        combinedJSON = self.gateway.getPossibleActionObjectCombinationsJSON()
        data = json.loads(combinedJSON)
        templates = data['templates']
        lookUpTable = data['lookUpTable']

        return (templates, lookUpTable)

    # Get a list of object types and their IDs
    def getObjectTypes(self):
        jsonStr = self.gateway.getObjectTypesLUTJSON()
        data = json.loads(jsonStr)
        return data

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

    #
    # History
    #
    def getRunHistory(self):
        historyStr = self.gateway.getRunHistoryJSON()
        #print("historyStr: " + str(historyStr))
        jsonOut = json.loads(historyStr)
        return jsonOut


    # History saving (provides an API to do this, so it's consistent across agents)
    def storeRunHistory(self, episodeIdxKey, notes):
        packed = {
            'episodeIdx': episodeIdxKey,
            'notes': notes,
            'history': self.getRunHistory()
        }

        self.runHistories[episodeIdxKey] = packed

    def saveRunHistories(self, filenameOutPrefix):
        # Save history

        # Create verbose filename
        filenameOut = filenameOutPrefix
        keys = sorted(self.runHistories.keys())
        if (len(keys) > 0):
            keyFirst = keys[0]
            keyLast = keys[-1]
            filenameOut += "-" + str(keyFirst) + "-" + str(keyLast)

        filenameOut += ".json"

        print("* Saving run history (" + str(filenameOut) + ")...")

        with open(filenameOut, 'w') as outfile:
            #print(type(self.runHistories))
            json.dump(self.runHistories, outfile, sort_keys=True, indent=4)

    def getRunHistorySize(self):
        return len(self.runHistories)

    def clearRunHistories(self):
        self.runHistories = {}

    # A one-stop function to handle saving.
    def saveRunHistoriesBufferIfFull(self, filenameOutPrefix, maxPerFile=1000, forceSave=False):
        if ((self.getRunHistorySize() >= maxPerFile) or (forceSave == True)):
            self.saveRunHistories(filenameOutPrefix)
            self.clearRunHistories()


    #
    # Train/development/test sets
    #
    def getVariationsTrain(self):
        return self.gateway.getVariationsTrain()

    def getVariationsDev(self):
        return self.gateway.getVariationsDev()

    def getVariationsTest(self):
        return self.gateway.getVariationsTest()

    def getRandomVariationTrain(self):
        return self.gateway.getRandomVariationTrain()

    def getRandomVariationDev(self):
        return self.gateway.getRandomVariationDev()

    def getRandomVariationTest(self):
        return self.gateway.getRandomVariationTest()

    # Gold action sequence
    def getGoldActionSequence(self):
        if (self.goldPathGenerated == True):
            return self.gateway.getGoldActionSequence()
        else:
            return ["ERROR: Gold path was not generated.  Set `generateGoldPath` flag to true when calling load()."]

    # Step
    def step(self, inputStr:str):
        observation = self.gateway.step(inputStr)
        score = int(round(100 * self.gateway.getScore()))        # Convert from 0-1 to 0-100
        isCompleted = self.gateway.getCompleted()
        numMoves = self.getNumMoves()

        # Calculate reward
        reward = score - self.lastStepScore         # Calculate reward (delta score) for this step
        self.lastStepScore = score                  # Store current score for reward calculation on the next step


        # If the number of moves exceeds the environment step limit, then set isCompleted to be true
        if (numMoves > self.envStepLimit):
            isCompleted = True

        # New: Handle this in the API rather than the agent -- if the score is less than zero, then set the isCompleted flag to true. 
        if (score < 0):
            isCompleted = True

        #print("> " + str(inputStr))
        #print("score: " + str(score))
        #print("moves: " + str(numMoves))

        # Mirror of Jericho API
        infos = {'moves': numMoves,
                 'score': score,
                 'reward': reward,
                 'look': self.look(),
                 'inv': self.inventory(),
                 'taskDesc': self.taskdescription(),
                 'valid': self.getValidActionObjectCombinations() }

        return observation, reward, isCompleted, infos


    # Special actions that are "free" (consume zero time)
    def look(self):
        observation = self.gateway.freeActionLook()
        return observation

    def inventory(self):
        observation = self.gateway.freeActionInventory()
        return observation

    def taskdescription(self):
        observation = self.gateway.freeActionTaskDesc()
        return observation

    # Goal progress
    def getGoalProgressStr(self):
        goalStr = self.gateway.getGoalProgressStr()
        return goalStr


class BufferedHistorySaver:

    #
    # Constructor
    #
    def __init__(self, filenameOutPrefix):
        self.filenameOutPrefix = filenameOutPrefix

        # Clear the run histories
        self.clearRunHistories()

    #
    # Methods
    #

    # History saving (provides an API to do this, so it's consistent across agents)
    def storeRunHistory(self, runHistory, episodeIdxKey, notes):
        packed = {
            'episodeIdx': episodeIdxKey,
            'notes': notes,
            'history': runHistory
        }

        self.runHistories[episodeIdxKey] = packed

    def saveRunHistories(self):
        # Save history

        # Create verbose filename
        filenameOut = self.filenameOutPrefix
        keys = sorted(self.runHistories.keys())
        if (len(keys) > 0):
            keyFirst = keys[0]
            keyLast = keys[-1]
            filenameOut += "-" + str(keyFirst) + "-" + str(keyLast)

        filenameOut += ".json"

        print("* Saving run history ( " + str(filenameOut) + ")...")

        with open(filenameOut, 'w') as outfile:
            #print(type(self.runHistories))
            json.dump(self.runHistories, outfile, sort_keys=True, indent=4)

    def getRunHistorySize(self):
        return len(self.runHistories)

    def clearRunHistories(self):
        self.runHistories = {}

    # A one-stop function to handle saving.
    def saveRunHistoriesBufferIfFull(self, maxPerFile=1000, forceSave=False):
        if ((self.getRunHistorySize() >= maxPerFile) or (forceSave == True)):
            self.saveRunHistories()
            self.clearRunHistories()
