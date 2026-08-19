from scienceworld import ScienceWorldEnv


ACTIONS = (
    "teleport to kitchen",
    "pour counter into sink",
    "activate sink",
    "use lighter on drawer",
    "look around",
)
NUM_RUNS = 8


env = ScienceWorldEnv()
missing_steam = 0

try:
    for run_idx in range(NUM_RUNS):
        env.load("task-1-boil", variationIdx=0, simplificationStr="teleportAction")
        env.reset()

        for action in ACTIONS:
            observation, _, _, _ = env.step(action)

        has_steam = "a substance called steam" in observation
        missing_steam += not has_steam
        print(f"Run {run_idx + 1}: steam={has_steam}")
finally:
    env.close()

print(f"{missing_steam}/{NUM_RUNS} runs are missing steam")
