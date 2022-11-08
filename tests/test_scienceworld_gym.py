import numpy as np

import gymnasium as gym
import scienceworld


def test_making_scienceworld_env():
    env = gym.make("ScienceWorld-v0")
    seed = 20221108

    _, info = env.reset(seed=seed, options={"task": "task-1-boil"})
    assert info["taskName"] == "task-1-boil"

    _, info = env.reset(seed=seed, options={"task": "task-1-boil", "variation": 10})
    assert info["taskName"] == "task-1-boil"
    assert info["variationIdx"] == 10

    _, info = env.reset(seed=seed, options={"task": "task-1-boil", "simplification": "openDoors"})
    assert info["taskName"] == "task-1-boil"
    assert info["simplificationStr"] == "openDoors"

    _, info = env.reset(seed=seed, options={"task": "task-1-boil", "generate_gold_path": True})
    assert info["simplificationStr"] == ""

    # Test named variation set.
    _, info = env.reset(seed=seed, options={"task": "task-1-boil", "variation": "train"})
    assert info["variationIdx"] in env.unwrapped.env.getVariationsTrain()
    _, info = env.reset(seed=seed, options={"task": "task-1-boil", "variation": "dev"})
    assert info["variationIdx"] in env.unwrapped.env.getVariationsDev()
    _, info = env.reset(seed=seed, options={"task": "task-1-boil", "variation": "test"})
    assert info["variationIdx"] in env.unwrapped.env.getVariationsTest()

    # Test list of variation IDs.
    _, info = env.reset(seed=seed, options={"task": "task-1-boil", "variation": [2,10,29]})
    assert info["variationIdx"] in [2,10,29]

    # Test env.reset with cached options. Expect to iterate over possible variations.
    _, info = env.reset(seed=seed, options={"task": "task-1-boil", "variation": [2,10,29]})
    variations = set()
    variations.add(info["variationIdx"])
    _, info = env.reset()
    variations.add(info["variationIdx"])
    _, info = env.reset()
    variations.add(info["variationIdx"])
    assert len(variations.intersection([2,10,29])) == 3


def test_making_scienceworld_vector_env():
    envs = gym.vector.make("ScienceWorld-v0", num_envs=3, asynchronous=False)

    obs, infos = envs.reset(options={"task": "task-1-boil"})
    obs1, rewards1, dones1, truncated1, infos1 = envs.step(("open studio door", "open door to kitchen", "wait"))

    obs, infos = envs.reset(options=[{"task": "task-1-boil", "variation": 1},
                                     {"task": "task-1-boil", "variation": 2},
                                     {"task": "task-1-boil", "variation": 3}])


    # Test envs.reset with cached options. Expect to iterate over possible variations.
    _, infos = envs.reset()
    assert np.array_equal(infos["variationIdx"], (1, 2, 3))

    _, infos = envs.reset(options=[{"task": "task-1-boil", "variation": "train"},
                                   {"task": "task-1-boil", "variation": "dev"},
                                   {"task": "task-1-boil", "variation": "test"}])

    for _ in range(10):
        _, infos = envs.reset()
        infos["variationIdx"][0] in envs.envs[0].unwrapped.env.getVariationsTrain()
        infos["variationIdx"][1] in envs.envs[1].unwrapped.env.getVariationsDev()
        infos["variationIdx"][2] in envs.envs[2].unwrapped.env.getVariationsTest()
