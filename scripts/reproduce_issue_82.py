from scienceworld import ScienceWorldEnv


env = ScienceWorldEnv("1-1")
try:
    _, info = env.reset()
    for action in info["valid"]:
        if action.startswith("open ") and "door" in action:
            print(action)
finally:
    env.close()
