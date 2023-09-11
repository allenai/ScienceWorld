import time
import argparse

from scienceworld import ScienceWorldEnv


def userConsole(args):
    """ Example user input console, to play through a game. """
    exitCommands = ["quit", "exit"]

    taskIdx = args['task_num']
    simplificationStr = args['simplification_str']

    # Number of agents
    numAgents = 5

    # Initialize environment
    env = ScienceWorldEnv("", args['jar_path'], envStepLimit = args['env_step_limit'])
    taskNames = env.getTaskNames()
    print("Task Names: " + str(taskNames))

    # Choose task
    taskName = taskNames[taskIdx]
    env.load(taskName, args['var_num'], simplificationStr, numAgents=numAgents, generateGoldPath=True)
    print("Starting Task " + str(taskIdx) + ": " + taskName)
    time.sleep(2)


    # Reset the environment
    initialObs, initialDict = env.reset()



    #
    #   Examples of how to access much of the environment information that the API exposes.
    #   (Many of these are similar to the Jericho API)
    #
    print("Task Names: " + str(taskNames))
    # print("Possible actions: " + str(env.getPossibleActions()) )
    # print("Possible objects: " + str(env.getPossibleObjects()) )
    # templates, lut = env.getPossibleActionObjectCombinations()
    # print("Possible action/object combinations: " + str(templates))
    # #print("Object IDX to Object Referent LUT: " + str(lut))
    # print("Vocabulary: " + str(env.getVocabulary()) )
    # print("Possible actions (with IDs): " + str(env.getPossibleActionsWithIDs()))
    # print("Possible object types: " + str(env.getObjectTypes()))
    # print("Object IDX to Object Referent LUT: " + str(lut))
    # print("\n")
    # print("Possible object referents LUT: " + str(env.getPossibleObjectReferentLUT()))
    # print("\n")
    # print("Valid action-object combinations: " + str(env.getValidActionObjectCombinations()))
    # print("\n")
    # print("Object_ids to type_ids: " + str(env.getAllObjectTypesLUTJSON()))
    # print("\n")
    # print("All objects, their ids, types, and referents: " + str(env.getAllObjectIdsTypesReferentsLUTJSON() ))
    # print("\n")
    # print("Valid action-object combinations (with templates): " + str(env.getValidActionObjectCombinationsWithTemplates() ))
    # print("\n")
    # print("Object Type LUT: " + str(env.getPossibleObjectReferentTypesLUT()))
    # print("Variations (train): " + str(env.getVariationsTrain() ))

    print("")
    print("----------------------------------------------------------------------------------")
    print("")


    print("Gold Path:" + str(env.getGoldActionSequence()))

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
            for actionStr in env.getPossibleObjects(agentIdx = 0):                   # hardcoded in this example to agent 0
                print("\t" + str(actionStr))

        elif (userInputStr == "valid"):
            print("Valid action-object combinations:")
            print(env.getValidActionObjectCombinationsWithTemplates(agentIdx = 0))   # hardcoded in this example to agent 0

        elif (userInputStr == 'goals'):
            print(env.getGoalProgressStr())

        else:
            # Just repeat the action N times, for each agent
            agentActions = [userInputStr] * numAgents
            print("Sending actions:")
            for i in range(numAgents):
                print("Agent " + str(i) + ": " + str(agentActions[i]))            
            print("")
            
            # Send user input, get response
            observations, reward, isCompleted, info = env.stepMultiAgent(agentActions)
            score = info['score']
            print("\nObservatins:\n")
            for i in range(numAgents):
                print("----------------------------------------------------------------------------------")
                print("Agent " + str(i) + ": \n" + str(observations[i]))

            print("----------------------------------------------------------------------------------")
            print("Reward: " + str(reward))
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
    print(env.getGoalProgressJSON())
    

    print("Completed.")


def build_simplification_str(args):
    """ Build simplification_str from args. """
    simplifications = list()
    if args["teleport"]:
        simplifications.append("teleportAction")

    if args["self_watering_plants"]:
        simplifications.append("selfWateringFlowerPots")

    if args["open_containers"]:
        simplifications.append("openContainers")

    if args["open_doors"]:
        simplifications.append("openDoors")

    if args["no_electrical"]:
        simplifications.append("noElectricalAction")

    return args["simplifications_preset"] or ",".join(simplifications)

#
#   Parse command line arguments
#
def parse_args():
    desc = "Play through a game using the console."
    parser = argparse.ArgumentParser(desc)
    parser.add_argument("--jar_path", type=str,
                        help="Path to the ScienceWorld jar file. Default: use builtin.")
    parser.add_argument("--task-num", type=int, default=13,
                        help="Specify the task number to play. Default: %(default)s")
    parser.add_argument("--var-num", type=int, default=0,
                        help="Specify the task variation number to play. Default: %(default)s")
    parser.add_argument("--env-step-limit", type=int, default=100,
                        help="Maximum number of steps per episode. Default: %(default)s")
    parser.add_argument("--num-episodes", type=int, default=5,
                        help="Number of episodes to play. Default: %(default)s")

    simplification_group = parser.add_argument_group('Game simplifications')
    simplification_group.add_argument("--simplifications-preset", choices=['easy'],
                                      help="Choose a preset among: 'easy' (apply all possible simplifications).")
    simplification_group.add_argument("--teleport", action="store_true",
                                      help="Lets agents instantly move to any location.")
    simplification_group.add_argument("--self-watering-plants", action="store_true",
                                      help="Plants do not have to be frequently watered.")
    simplification_group.add_argument("--open-containers", action="store_true",
                                      help="All containers are opened by default.")
    simplification_group.add_argument("--open-doors", action="store_true",
                                      help="All doors are opened by default.")
    simplification_group.add_argument("--no-electrical", action="store_true",
                                      help="Remove the electrical actions (reduces the size of the action space).")

    args = parser.parse_args()
    params = vars(args)
    return params


def main():
    print("ScienceWorld 1.0 API Examples - MultiAgent Console")
    print("--------------------------------------------------")
    print("NOTE: While this is a multi-agent example, it just sends the same action to all agents.")
    print("      This is just a simple example to show how to use the API.")

    # Parse command line arguments
    args = parse_args()
    args["simplification_str"] = build_simplification_str(args)
    userConsole(args)


if __name__ == "__main__":
    main()
