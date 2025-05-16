import sys
import time
import argparse

from scienceworld import ScienceWorldEnv


prompt_toolkit_available = False
try:
    # For command line history and autocompletion.
    from prompt_toolkit import prompt
    from prompt_toolkit.completion import WordCompleter
    from prompt_toolkit.history import InMemoryHistory
    prompt_toolkit_available = sys.stdout.isatty()
except ImportError:
    pass

try:
    # For command line history when prompt_toolkit is not available.
    import readline  # noqa: F401
except ImportError:
    pass


def userConsole(args):
    """ Example user input console, to play through a game. """
    history = None
    if prompt_toolkit_available:
        history = InMemoryHistory()

    exitCommands = ["quit", "exit"]

    taskIdx = args['task_num']
    simplificationStr = args['simplification_str']

    # Initialize environment
    env = ScienceWorldEnv("", args['jar_path'], envStepLimit=args['env_step_limit'])
    taskNames = env.get_task_names()
    print("Task Names: " + str(taskNames))

    # Choose task
    taskName = taskNames[taskIdx]
    env.load(taskName, args['var_num'], simplificationStr, generateGoldPath=True)
    print("Starting Task " + str(taskIdx) + ": " + taskName)
    time.sleep(2)

    # Reset the environment
    initialObs, initialDict = env.reset()

    #
    #   Examples of how to access much of the environment information that the API exposes.
    #   (Many of these are similar to the Jericho API)
    #
    print("Task Names: " + str(taskNames))
    print("Possible actions: " + str(env.get_possible_actions()))
    print("Possible objects: " + str(env.get_possible_objects()))
    templates, lut = env.get_possible_action_object_combinations()
    print("Possible action/object combinations: " + str(templates))
    # print("Object IDX to Object Referent LUT: " + str(lut))
    print("Vocabulary: " + str(env.get_vocabulary()))
    print("Possible actions (with IDs): " + str(env.get_possible_actions_with_IDs()))
    print("Possible object types: " + str(env.get_object_types()))
    print("Object IDX to Object Referent LUT: " + str(lut))
    print("\n")
    print("Possible object referents LUT: " + str(env.get_possible_object_referent_LUT()))
    print("\n")
    print("Valid action-object combinations: " +
          str(env.get_valid_action_object_combinations()))
    print("\n")
    print("Object_ids to type_ids: " + str(env.get_all_object_types_LUTJSON()))
    print("\n")
    print("All objects, their ids, types, and referents: " +
          str(env.get_all_object_ids_types_referents_LUTJSON()))
    print("\n")
    print("Valid action-object combinations (with templates): " +
          str(env.get_valid_action_object_combinations_with_templates()))
    print("\n")
    print("Object Type LUT: " + str(env.get_possible_object_referent_types_LUT()))
    print("Variations (train): " + str(env.get_variations_train()))

    print("")
    print("----------------------------------------------------------------------------------")
    print("")

    print("Gold Path:" + str(env.get_gold_action_sequence()))

    print("Task Name: " + taskName)
    print("Variation: " + str(args['var_num']) + " / " + str(env.get_max_variations(taskName)))
    print("Task Description: " + str(env.get_task_description()))

    #
    #   Main user input loop
    #
    userInputStr = "look around"        # First action
    while (userInputStr not in exitCommands):
        if (userInputStr == "help"):
            print("Possible actions: ")
            for actionStr in env.get_possible_actions():
                print("\t" + str(actionStr))

        elif (userInputStr == "objects"):
            print("Possible objects (one referent listed per object): ")
            for actionStr in env.get_possible_objects():
                print("\t" + str(actionStr))

        elif (userInputStr == "valid"):
            print("Valid action-object combinations:")
            print(env.get_valid_action_object_combinations_with_templates())

        elif (userInputStr == 'goals'):
            print(env.get_goal_progress())

        else:
            # Send user input, get response
            observation, reward, isCompleted, info = env.step(userInputStr)
            score = info['score']
            print("\n" + observation)
            print("Reward: " + str(reward))
            print("Score: " + str(score))
            print("isCompleted: " + str(isCompleted))
            # print("info: " + str(info))

        print("'help' lists valid action templates, 'objects' lists valid objects, use <tab> to list valid actions. ")
        print("'goals' lists progress on subgoals.")
        print("type 'exit' to quit.")

        # Select a random action
        valid_actions = env.get_valid_action_object_combinations()

        # Get user input
        if prompt_toolkit_available:
            actions_completer = WordCompleter(valid_actions, ignore_case=True, sentence=True)
            userInputStr = prompt('> ', completer=actions_completer,
                                  history=history, enable_history_search=True)
        else:
            print("Valid Actions: " + str(valid_actions))
            userInputStr = input('> ')

        # Sanitize input
        userInputStr = userInputStr.lower().strip()

    # Display run history
    runHistory = env.get_run_history()
    print("Run History:")
    print(runHistory)
    for item in runHistory:
        print(item)
        print("")

    # Display subgoal progress
    print(env.get_goal_progress())

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

#   Parse command line arguments


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
    print("ScienceWorld 1.0 API Examples - Human")

    # Parse command line arguments
    args = parse_args()
    args["simplification_str"] = build_simplification_str(args)
    userConsole(args)


if __name__ == "__main__":
    main()
