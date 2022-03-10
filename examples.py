#
#   Examples
#
#   conda create --name scienceworld python=3.8
#   conda activate scienceworld
#   pip install py4j                                (for scala-python interface)
#   pip install -U pywebio                          (for web server)


from scienceworld_python_api import ScienceWorldEnv
import argparse
import random
import timeit
import time

#
#   Example random agent -- randomly picks an action at each step.
#
def randomModel(args):
    exitCommands = ["quit", "exit"]    

    taskIdx = args['task_num']
    simplificationStr = args['simplification_str']
    numEpisodes = args['num_episodes']      

    # Keep track of the agent's final scores
    finalScores = []

    # Initialize environment
    env = ScienceWorldEnv("", args['jar_path'], envStepLimit = args['env_step_limit'] , threadNum = 0)
    taskNames = env.getTaskNames()
    taskName = taskNames[taskIdx]        # Just get first task    
    maxVariations = env.getMaxVariations(taskName)
    print("Task Names: " + str(taskNames))

    print("Starting Task " + str(taskIdx) + ": " + taskName)
    time.sleep(2)

    # Start running episodes
    for episodeIdx in range(0, numEpisodes):        
        # Pick a random task variation
        randVariationIdx = random.randrange(0, maxVariations)
        env.load(taskName, randVariationIdx, simplificationStr)

        # Reset the environment
        initialObs, initialDict = env.reset()
        
        # Example accessors
        print("Possible actions: " + str(env.getPossibleActions()) )
        print("Possible objects: " + str(env.getPossibleObjects()) )
        templates, lut = env.getPossibleActionObjectCombinations()
        print("Possible action/object combinations: " + str(templates))
        print("Object IDX to Object Referent LUT: " + str(lut))
        print("Task Name: " + taskName)
        print("Task Variation: " + str(randVariationIdx) + " / " + str(maxVariations))
        print("Task Description: " + str(env.getTaskDescription()) )        
        print("look: " + str(env.look()) )
        print("inventory: " + str(env.inventory()) )
        print("taskdescription: " + str(env.taskdescription()) )
        

        score = 0.0
        isCompleted = False
        curIter = 0
        maxIter = 10

        # Run one episode until we reach a stopping condition (including exceeding the maximum steps)
        userInputStr = "look around"        # First action
        while (userInputStr not in exitCommands) and (isCompleted == False):
            print("----------------------------------------------------------------")
            print ("Step: " + str(curIter))

            # Send user input, get response
            observation, score, isCompleted, info = env.step(userInputStr)
            print("\n>>> " + observation)
            print("Score: " + str(score))
            print("isCompleted: " + str(isCompleted))

            # The environment will makke isCompleted `True` when a stop condition has happened, or the maximum number of steps is reached.
            if (isCompleted):
                break

            # Randomly select action        
            templates, lut = env.getPossibleActionObjectCombinations()
            print(list(lut.keys())[-1])
            #print("Possible action/object combinations: " + str(templates))
            #print("Object IDX to Object Referent LUT: " + str(lut))
            randomTemplate = random.choice( templates )        
            print("Next random action: " + str(randomTemplate))
            userInputStr = randomTemplate["action"]

            # Sanitize input
            userInputStr = userInputStr.lower().strip()
            print("Choosing random action: " + str(userInputStr))

            curIter += 1


        # Episode finished -- Record the final score
        finalScores.append(score)
            
        # Report progress of model        
        print ("Final score: " + str(score))
        print ("isCompleted: " + str(isCompleted))        

        # Save history -- and when we reach maxPerFile, export them to file
        filenameOutPrefix = args['output_path_prefix'] + str(taskIdx)        
        env.storeRunHistory(episodeIdx, notes = {'text':'my notes here'} )
        env.saveRunHistoriesBufferIfFull(filenameOutPrefix, maxPerFile=args['max_episode_per_file'])

    # Episodes are finished -- manually save any last histories still in the buffer
    env.saveRunHistoriesBufferIfFull(filenameOutPrefix, maxPerFile=args['max_episode_per_file'], forceSave=True)

    # Show final episode scores to user: 
    avg = sum([x for x in finalScores if x >=0]) / len(finalScores)     # Clip negative scores to 0 for average calculation
    print ("")
    print ("---------------------------------------------------------------------")
    print (" Summary (Random Agent)")
    print (" Task " + str(taskIdx) + ": " + taskName)
    print ("---------------------------------------------------------------------")
    print (" Epsiode scores: " + str(finalScores))    
    print (" Average episode score: " + str(avg))
    print ("---------------------------------------------------------------------")
    print ("")

    print("Shutting down server...")    
    env.shutdown()

    print("Completed.")


# Example user input console, to play through a game. 
def userConsole(jarPath:str):
    exitCommands = ["quit", "exit"]

    simplificationStr = ""

    # Initialize environment
    env = ScienceWorldEnv("", jarPath, envStepLimit = 100, threadNum = 0)
    taskNames = env.getTaskNames()
    taskName = taskNames[0]        # Just get first task    
    maxVariations = env.getMaxVariations(taskName)
    randVariationIdx = random.randrange(0, maxVariations)           # Pick a random variation
    env.load(taskName, randVariationIdx, simplificationStr)

    initialObs, initialDict = env.reset()
    
    print("Task Names: " + str(taskNames))

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

    typeLUT = env.getPossibleObjectReferentTypesLUT()
    print("typeLUT: " + str(typeLUT))

    print("Task Name: " + taskName)
    print("Task Variation: " + str(randVariationIdx) + " / " + str(maxVariations))
    print("Task Description: " + str(env.getTaskDescription()) )    

    print("")
    print("Variations (train): " + str(env.getVariationsTrain() ))
    print("type: " + str(type(env.getVariationsTrain())) )
    print("")

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


    # Display run history 
    runHistory = env.getRunHistory()    
    print("Run History:")
    print(runHistory)
    for item in runHistory:
        print("* One step: \n" + str(item))
        print("")

    print("Shutting down server...")    
    env.shutdown()



    print("Completed.")


#
#   Parse command line arguments
#
def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--jar_path", type=str, default="scienceworld-1.0.jar")
    parser.add_argument("--task_num", type=int, default=13)
    parser.add_argument("--env_step_limit", type=int, default=100)    
    parser.add_argument("--num_episodes", type=int, default=5)    
    parser.add_argument("--simplification_str", default="easy")
    parser.add_argument("--max_episode_per_file", type=int, default=1000)
    parser.add_argument("--mode", default="random")
    parser.add_argument("--output_path_prefix", default="save-histories")

    args = parser.parse_args()
    params = vars(args)
    return params


#
#   Main
#
def main():        
    print("ScienceWorld 1.0 API Examples")    

    # Parse command line arguments
    args = parse_args()

    # Run a user console
    #userConsole(jarPath)

    # Run a model that chooses random actions until successfully reaching the goal
    if (args['mode'] == 'random'):
        randomModel(args)

    print("Exiting.")

if __name__ == "__main__":
    main()