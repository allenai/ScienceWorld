#
#   Examples
#
#   conda create --name scienceworld python=3.8
#   conda activate scienceworld
#   pip install py4j                                (for scala-python interface)
#   pip install -U pywebio                          (for web server)


from scienceworld import ScienceWorldEnv
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
    print("Task Names: " + str(taskNames))

    # Choose task
    taskName = taskNames[taskIdx]        # Just get first task        
    env.load(taskName, 0, "")            # Load the task, so we have access to some extra accessors e.g. getRandomVariationTrain() )
    maxVariations = env.getMaxVariations(taskName)    
    print("Starting Task " + str(taskIdx) + ": " + taskName)
    time.sleep(2)

    # Start running episodes
    for episodeIdx in range(0, numEpisodes):        
        # Pick a random task variation
        randVariationIdx = env.getRandomVariationTrain()
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

            ## Any action (valid or not)
            #templates, lut = env.getPossibleActionObjectCombinations()            
            #print("Possible action/object combinations: " + str(templates))
            #print("Object IDX to Object Referent LUT: " + str(lut))
            #randomTemplate = random.choice( templates )        
            #print("Next random action: " + str(randomTemplate))
            #userInputStr = randomTemplate["action"]

            ## Only valid actions
            validActions = env.getValidActionObjectCombinationsWithTemplates()
            randomAction = random.choice( validActions )        
            print("Next random action: " + str(randomAction))
            userInputStr = randomAction["action"]

                        
            print(list(lut.keys())[-1])

            # Sanitize input
            userInputStr = userInputStr.lower().strip()
            print("Choosing random action: " + str(userInputStr))

            # Keep track of the number of commands sent to the environment in this episode
            curIter += 1

        print("Goal Progress:")
        print(env.getGoalProgressStr())
        time.sleep(1)

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
    print (" Simplifications: " + str(simplificationStr))
    print ("---------------------------------------------------------------------")
    print (" Epsiode scores: " + str(finalScores))    
    print (" Average episode score: " + str(avg))
    print ("---------------------------------------------------------------------")
    print ("")

    print("Shutting down server...")    
    env.shutdown()

    print("Completed.")


# Example user input console, to play through a game. 
def userConsole(args):
    exitCommands = ["quit", "exit"]    

    taskIdx = args['task_num']
    simplificationStr = args['simplification_str']      

    # Initialize environment
    env = ScienceWorldEnv("", args['jar_path'], envStepLimit = args['env_step_limit'] , threadNum = 0)
    taskNames = env.getTaskNames()
    print("Task Names: " + str(taskNames))

    # Choose task
    taskName = taskNames[taskIdx]
    env.load(taskName, args['var_num'], simplificationStr)
    print("Starting Task " + str(taskIdx) + ": " + taskName)
    time.sleep(2)    

    # Reset the environment
    initialObs, initialDict = env.reset()
    
    
    #
    #   Examples of how to access much of the environment information that the API exposes. 
    #   (Many of these are similar to the Jericho API)
    #
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
    print("Object Type LUT: " + str(env.getPossibleObjectReferentTypesLUT()))
    print("Variations (train): " + str(env.getVariationsTrain() ))    

    print("")
    print("----------------------------------------------------------------------------------")
    print("")

    print("Task Name: " + taskName)
    print("Variation: " + str(args['var_num']) + " / " + str(env.getMaxVariations(taskName)))
    print("Task Description: " + str(env.getTaskDescription()) )        
    
    #
    #   Main user input loop
    #
    userInputStr = "look around"        # First action
    while (userInputStr not in exitCommands):
        if (userInputStr == "help"):
            print("Possible actions: ")
            for actionStr in env.getPossibleActions():
                print("\t" + str(actionStr))

        elif (userInputStr == "objects"):
            print("Possible objects (one referent listed per object): ")
            for actionStr in env.getPossibleObjects():
                print("\t" + str(actionStr))

        elif (userInputStr == "valid"):        
            print("Valid action-object combinations:")
            print(env.getValidActionObjectCombinationsWithTemplates())

        elif (userInputStr == 'goals'):
            print(env.getGoalProgressStr())
            
        else:
            # Send user input, get response
            observation, score, isCompleted, info = env.step(userInputStr)
            print("\n" + observation)
            print("Score: " + str(score))
            print("isCompleted: " + str(isCompleted))
            #print("info: " + str(info))

        print("'help' lists valid action templates, 'objects' lists valid objects, 'valid' lists valid action-object combinations (long!). ")
        print("'goals' lists progress on subgoals.")
        print("type 'exit' to quit.")

        # Get user input        
        userInputStr = input('> ')
        # Sanitize input
        userInputStr = userInputStr.lower().strip()

    
    # Display run history     
    runHistory = env.getRunHistory()    
    print("Run History:")
    print(runHistory)
    for item in runHistory:
        print(item)
        print("")

    # Display subgoal progress
    print(env.getGoalProgressStr())


    print("Shutting down server...")    
    env.shutdown()

    print("Completed.")


#
#   Parse command line arguments
#
def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--jar_path", type=str)
    parser.add_argument("--task_num", type=int, default=13)
    parser.add_argument("--var_num", type=int, default=0)    
    parser.add_argument("--env_step_limit", type=int, default=100)    
    parser.add_argument("--num_episodes", type=int, default=5)    
    parser.add_argument("--simplification_str", default="easy")
    parser.add_argument("--max_episode_per_file", type=int, default=1000)
    parser.add_argument("--mode", default="randomagent")
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
    if (args['mode'] == 'userconsole'):
        userConsole(args)

    # Run a model that chooses random actions until successfully reaching the goal
    if (args['mode'] == 'randomagent'):
        randomModel(args)

    print("Exiting.")

if __name__ == "__main__":
    main()
