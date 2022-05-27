import time
import random
import argparse

from scienceworld import ScienceWorldEnv


def randomModel(args):
    """ Example random agent -- randomly picks an action at each step. """
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
            observation, reward, isCompleted, info = env.step(userInputStr)
            score = info['reward']

            print("\n>>> " + observation)
            print("Reward: " + str(reward))
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


def build_simplification_str(args):
    """ Build simplification_str from args. """
    simplifications = set()
    if args["teleport"]:
        simplifications.add("teleportAction")

    if args["self_watering_plants"]:
        simplifications.add("selfWateringFlowerPots")

    if args["open_containers"]:
        simplifications.add("openContainers")

    if args["open_doors"]:
        simplifications.add("openDoors")

    if args["no_electrical"]:
        simplifications.add("noElectricalAction")

    return args["simplifications_preset"] or ",".join(simplifications)


#
#   Parse command line arguments
#
def parse_args():
    desc = "Run a model that chooses random actions until successfully reaching the goal."
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
    parser.add_argument("--seed", type=int,
                        help="Seed the random generator used for sampling random actions.")
    parser.add_argument("--output-path-prefix", default="save-histories",
                        help="Path prefix to use for saving episode transcripts. Default: %(default)s")
    parser.add_argument("--max-episode-per-file", type=int, default=1000,
                        help="Maximum number of epsiodes per transcript file. Default: %(default)s")

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
    print("ScienceWorld 1.0 API Examples - Random Agent")

    # Parse command line arguments
    args = parse_args()
    random.seed(args["seed"])
    args["simplification_str"] = build_simplification_str(args)
    randomModel(args)


if __name__ == "__main__":
    main()
