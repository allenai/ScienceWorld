import os
import json
import logging
from collections import OrderedDict

from py4j.java_gateway import JavaGateway, GatewayParameters, launch_gateway, CallbackServerParameters

from scienceworld.constants import BASEPATH, DEBUG_MODE, ID2TASK, JAR_PATH, NAME2ID
from scienceworld.utils import infer_task, snake_case_deprecation_warning

logger = logging.getLogger(__name__)


class ScienceWorldEnv:

    def __init__(self, taskName=None, serverPath=None, envStepLimit=100):
        serverPath = serverPath or JAR_PATH  # Use the builtin jar.

        # Launch the server and connect to the JVM.
        # Launch Java side with dynamic port and get back the port on which the
        # server was bound to.
        if DEBUG_MODE:
            import sys, time
            port = launch_gateway(
                classpath=serverPath, die_on_exit=True, cwd=BASEPATH,
                javaopts=['-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005,quiet=y'],
                redirect_stdout=sys.stdout, redirect_stderr=sys.stderr)
            logger.debug("Attach debugger within the next 10 seconds")
            time.sleep(10)  # Give time for user to attach debugger
        else:
            port = launch_gateway(classpath=serverPath, die_on_exit=True, cwd=BASEPATH)

        # Connect python side to Java side with Java dynamic port and start python
        # callback server with a dynamic port
        self._gateway = JavaGateway(
            gateway_parameters=GatewayParameters(auto_field=True, port=port),
            callback_server_parameters=CallbackServerParameters(port=0, daemonize=True))

        # Retrieve the port on which the python callback server was bound to.
        python_port = self._gateway.get_callback_server().get_listening_port()

        # Tell the Java side to connect to the python callback server with the new
        # python port. Note that we use the java_gateway_server attribute that
        # retrieves the GatewayServer instance.
        self._gateway.java_gateway_server.resetCallbackClient(
            self._gateway.java_gateway_server.getCallbackClient().getAddress(),
            python_port)

        self.server = self._gateway.jvm.scienceworld.runtime.pythonapi.PythonInterface()
        logger.info("ScienceWorld server running on port %d", port)

        # Keep track of the last step score, to calculate reward from score
        self.lastStepScore = 0

        # Load the script
        self.taskName = taskName
        if self.taskName:
            self.load(taskName, 0, "")

        # Set the environment step limit
        self.envStepLimit = envStepLimit

        # Clear the run histories
        self.clearRunHistories()

        # By default, set that the gold path was not generated unless the user asked for it
        self.goldPathGenerated = False

    # Ask the simulator to load an environment from a script
    def load(self, taskName, variationIdx=0, simplificationStr="", generateGoldPath=False):
        """ Load a given task and its variation. """

        # Check loading arguments.
        # Validate task name.
        taskName = infer_task(taskName)
        if taskName not in self.getTaskNames():
            msg = "Unknown taskName: '{}'. ".format(taskName)
            msg += "Supported tasks are: {}".format(self.getTaskNames())
            raise ValueError(msg)

        self.taskName = taskName

        # Validate simplification string.
        possible_simplifications = ["easy"] + self.getPossibleSimplifications()
        for simplification in simplificationStr.split(","):
            if simplification and simplification not in possible_simplifications:
                msg = "Unknown simplification: '{}'. ".format(simplification)
                msg += "Supported simplifications are: {}".format(possible_simplifications)
                raise ValueError(msg)

        is_electrical_task = "power-component" in taskName or "conductivity" in taskName
        if is_electrical_task and "noElectricalAction" in simplificationStr:
            msg = "Invalid simplification. Task '{}' requires electrical actions but '--no-electrical' was provided."
            raise ValueError(msg.format(taskName))

        self.simplificationStr = simplificationStr
        self.variationIdx = variationIdx

        logger.info(f"Loading: {self.taskName} (variation: {self.variationIdx}) (simplifications: {self.simplificationStr})")
        self.server.load(self.taskName, self.variationIdx, self.simplificationStr, generateGoldPath)

        # Reset last step score (used to calculate reward from current-previous score)
        self.lastStepScore = 0

        # Keep track of whether the gold path was generated, to generate verbose error messages
        self.goldPathGenerated = generateGoldPath

    # Ask the simulator to reset an environment back to it's initial state
    def reset(self):
        self.server.reset()

        # Reset last step score (used to calculate reward from current-previous score)
        self.lastStepScore = 0

        # Make first move
        observation, score, isCompleted, info = self.step("look around")

        # Return a tuple that looks like the Jericho signature for reset
        return observation, info

    # Simplifications
    def get_simplifications_used(self):
        return self.server.getSimplificationsUsed()

    def get_possible_simplifications(self):
        return self.server.getPossibleSimplifications().split(", ")



    @property
    def tasks(self):
        """ Get the supported tasks in ScienceWorld. """
        return OrderedDict(ID2TASK)

    @property
    def task_names(self):
        """ Get the name for the supported tasks in ScienceWorld. """
        return list(ID2TASK.values())

    def get_task_names(self):
        """ Get the name for the supported tasks in ScienceWorld. """
        return list(self.server.getTaskNames())

    # Get the maximum number of variations for this task
    def get_max_variations(self, taskName):
        return self.server.getTaskMaxVariations(infer_task(taskName))

    # Get possible actions
    def get_possible_actions(self):
        return list(self.server.getPossibleActions())

    # Get possible actions (and also include the template IDs for those actions)
    def get_possible_actions_with_IDs(self):
        jsonStr = self.server.getPossibleActionsWithIDs()
        data = json.loads(jsonStr)
        return data

    # Get possible objects
    def get_possible_objects(self):
        return list(self.server.getPossibleObjects())

    # Get a list of object_ids to unique referents
    def get_possible_object_referent_LUT(self):
        jsonStr = self.server.getPossibleObjectReferentLUTJSON()
        data = json.loads(jsonStr)
        return data

    # As above, but dictionary is referenced by object type ID
    def get_possible_object_referent_types_LUT(self):
        jsonStr = self.server.getPossibleObjectReferentTypesLUTJSON()
        data = json.loads(jsonStr)
        return data

    # Get a list of *valid* agent-object combinations
    def get_valid_action_object_combinations(self):
        return list(self.server.getValidActionObjectCombinations())

    def get_valid_action_object_combinations_with_templates(self):
        jsonStr = self.server.getValidActionObjectCombinationsJSON()
        data = json.loads(jsonStr)
        return data['validActions']

    # Get a LUT of object_id to type_id
    def get_all_object_types_LUTJSON(self):
        jsonStr = self.server.getAllObjectTypesLUTJSON()
        data = json.loads(jsonStr)
        return data

    # Get a LUT of {object_id: {type_id, referent:[]} } tuples
    def get_all_object_ids_types_referents_LUTJSON(self):
        jsonStr = self.server.getAllObjectIdsTypesReferentsLUTJSON()
        data = json.loads(jsonStr)
        return data

    # Get possible action/object combinations
    def get_possible_action_object_combinations(self):
        combinedJSON = self.server.getPossibleActionObjectCombinationsJSON()
        data = json.loads(combinedJSON)
        templates = data['templates']
        lookUpTable = data['lookUpTable']

        return (templates, lookUpTable)

    # Get a list of object types and their IDs
    def get_object_types(self):
        jsonStr = self.server.getObjectTypesLUTJSON()
        data = json.loads(jsonStr)
        return data

    # Get the vocabulary of the model (at the current state)
    def get_vocabulary(self):
        vocab = set()

        # Action vocabulary
        for actionStr in self.get_possible_actions():
            for word in actionStr.split(" "):
                vocab.add(word)

        # Object vocabulary (keep as compound nouns?)
        vocabObjects = self.get_possible_objects()
        vocab = vocab.union( set(vocabObjects) )

        return vocab


    def get_num_moves(self):
        return self.server.getNumMoves()

    def get_task_description(self):
        return self.server.getTaskDescription()

    #
    # History
    #
    def get_run_history(self):
        historyStr = self.server.getRunHistoryJSON()
        jsonOut = json.loads(historyStr)
        return jsonOut


    # History saving (provides an API to do this, so it's consistent across agents)
    def store_run_history(self, episodeIdxKey, notes):
        packed = {
            'episodeIdx': episodeIdxKey,
            'notes': notes,
            'history': self.get_run_history()
        }

        self.runHistories[episodeIdxKey] = packed

    def save_run_histories(self, filenameOutPrefix):
        # Save history

        # Create verbose filename
        filenameOut = filenameOutPrefix
        keys = sorted(self.runHistories.keys())
        if (len(keys) > 0):
            keyFirst = keys[0]
            keyLast = keys[-1]
            filenameOut += "-" + str(keyFirst) + "-" + str(keyLast)

        filenameOut += ".json"

        logger.info("* Saving run history (" + str(filenameOut) + ")...")

        with open(filenameOut, 'w') as outfile:
            json.dump(self.runHistories, outfile, sort_keys=True, indent=4)

    def get_run_history_size(self):
        return len(self.runHistories)

    def clear_run_histories(self):
        self.runHistories = {}

    # A one-stop function to handle saving.
    def save_run_histories_buffer_if_full(self, filenameOutPrefix, maxPerFile=1000, forceSave=False):
        if ((self.get_run_history_size() >= maxPerFile) or (forceSave == True)):
            self.save_run_histories(filenameOutPrefix)
            self.clear_run_histories()


    #
    # Train/development/test sets
    #
    def get_variations_train(self):
        return list(self.server.getVariationsTrain())

    def get_variations_dev(self):
        return list(self.server.getVariationsDev())

    def get_variations_test(self):
        return list(self.server.getVariationsTest())

    def get_random_variation_train(self):
        return self.server.getRandomVariationTrain()

    def get_random_variation_dev(self):
        return self.server.getRandomVariationDev()

    def get_random_variation_test(self):
        return self.server.getRandomVariationTest()

    # Gold action sequence
    def get_gold_action_sequence(self):
        if (self.goldPathGenerated == True):
            return list(self.server.getGoldActionSequence())
        else:
            return ["ERROR: Gold path was not generated.  Set `generateGoldPath` flag to true when calling load()."]


    # Step
    def step(self, inputStr:str):
        observation = self.server.step(inputStr)
        score = int(round(100 * self.server.getScore()))        # Convert from 0-1 to 0-100
        isCompleted = self.server.getCompleted()
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

        # Mirror of Jericho API
        infos = {
            'moves': numMoves,
            'score': score,
            'reward': reward,
            'look': self.look(),
            'inv': self.inventory(),
            'taskDesc': self.taskdescription(),
            'valid': self.getValidActionObjectCombinations(),
            'variationIdx': self.variationIdx,
            'taskName': self.taskName,
            'simplificationStr': self.simplificationStr,
        }

        return observation, reward, isCompleted, infos


    # Special actions that are "free" (consume zero time)
    def look(self):
        observation = self.server.freeActionLook()
        return observation

    def inventory(self):
        observation = self.server.freeActionInventory()
        return observation

    def taskdescription(self):
        observation = self.server.freeActionTaskDesc()
        return observation

    # Goal progress
    def get_goal_progress_str(self):
        goalStr = self.server.getGoalProgressStr()
        return goalStr


    ####################### Camel Case Methods ################################
    # All of the wrapper methods for camel case, to avoid breaking projects.

    # Simplifications
    def getSimplificationsUsed(self):
        snake_case_deprecation_warning()

        return self.get_simplifications_used()

    def getPossibleSimplifications(self):
        snake_case_deprecation_warning()

        return self.get_possible_simplifications()

    def getTaskNames(self):
        """ Get the name for the supported tasks in ScienceWorld. """
        snake_case_deprecation_warning()

        return self.get_task_names()

    # Get the maximum number of variations for this task
    def getMaxVariations(self, taskName):
        snake_case_deprecation_warning()

        return self.get_max_variations(taskName)

    # Get possible actions
    def getPossibleActions(self):
        snake_case_deprecation_warning()

        return self.get_possible_actions()

    # Get possible actions (and also include the template IDs for those actions)
    def getPossibleActionsWithIDs(self):
        snake_case_deprecation_warning()

        return self.get_possible_actions_with_IDs()

    # Get possible objects
    def getPossibleObjects(self):
        snake_case_deprecation_warning()

        return self.get_possible_objects()

    # Get a list of object_ids to unique referents
    def getPossibleObjectReferentLUT(self):
        snake_case_deprecation_warning()

        return self.get_possible_object_referent_LUT()

    # As above, but dictionary is referenced by object type ID
    def getPossibleObjectReferentTypesLUT(self):
        snake_case_deprecation_warning()

        return self.get_possible_object_referent_types_LUT()

    # Get a list of *valid* agent-object combinations
    def getValidActionObjectCombinations(self):
        snake_case_deprecation_warning()

        return self.get_valid_action_object_combinations()

    def getValidActionObjectCombinationsWithTemplates(self):
        snake_case_deprecation_warning()

        return self.get_valid_action_object_combinations_with_templates()

    # Get a LUT of object_id to type_id
    def getAllObjectTypesLUTJSON(self):
        snake_case_deprecation_warning()

        return self.get_all_object_types_LUTJSON()

    # Get a LUT of {object_id: {type_id, referent:[]} } tuples
    def getAllObjectIdsTypesReferentsLUTJSON(self):
        snake_case_deprecation_warning()

        return self.get_all_object_ids_types_referents_LUTJSON()

    # Get possible action/object combinations
    def getPossibleActionObjectCombinations(self):
        snake_case_deprecation_warning()

        return self.get_possible_action_object_combinations()

    # Get a list of object types and their IDs
    def getObjectTypes(self):
        snake_case_deprecation_warning()

        return self.get_object_types()

    # Get the vocabulary of the model (at the current state)
    def getVocabulary(self):
        snake_case_deprecation_warning()

        return self.get_vocabulary()


    def getNumMoves(self):
        snake_case_deprecation_warning()

        return self.get_num_moves()

    def getTaskDescription(self):
        snake_case_deprecation_warning()

        return self.get_task_description()

    def getRunHistory(self):
        snake_case_deprecation_warning()

        return self.get_run_history()

    def storeRunHistory(self, episodeIdxKey, notes):
        snake_case_deprecation_warning()

        self.store_run_history(episodeIdxKey, notes)

    def saveRunHistories(self, filenameOutPrefix):
        snake_case_deprecation_warning()

        self.save_run_histories(filenameOutPrefix)

    def getRunHistorySize(self):
        snake_case_deprecation_warning()

        return self.get_run_historySize()

    def clearRunHistories(self):
        snake_case_deprecation_warning()

        self.clear_run_histories()

    # A one-stop function to handle saving.
    def saveRunHistoriesBufferIfFull(self, filenameOutPrefix, maxPerFile=1000, forceSave=False):
        snake_case_deprecation_warning()

        self.save_run_histories_buffer_if_full(filenameOutPrefix, maxPerFile, forceSave)

    def getVariationsTrain(self):
        snake_case_deprecation_warning()

        return self.get_variations_train()

    def getVariationsDev(self):
        snake_case_deprecation_warning()

        return self.get_variations_dev()

    def getVariationsTest(self):
        snake_case_deprecation_warning()

        return self.get_variations_test()

    def getRandomVariationTrain(self):
        snake_case_deprecation_warning()

        return self.get_random_variation_train()

    def getRandomVariationDev(self):
        snake_case_deprecation_warning()

        return self.get_random_variation_dev()

    def getRandomVariationTest(self):
        snake_case_deprecation_warning()

        return self.get_random_variation_test()

    def getGoldActionSequence(self):
        snake_case_deprecation_warning()

        return self.get_gold_action_sequence()

    def getGoalProgressStr(self):
        snake_case_deprecation_warning()

        return self.get_goal_progress_str()




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

        logger.info("* Saving run history ( " + str(filenameOut) + ")...")

        with open(filenameOut, 'w') as outfile:
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
