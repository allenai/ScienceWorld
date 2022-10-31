from scienceworld import ScienceWorldEnv


def test_observation_is_deterministic():
    env = ScienceWorldEnv("task-1-boil")
    obs_orig, _ = env.reset()

    for _ in range(30):
        obs, _ = env.reset()
        assert obs == obs_orig

        obs, _, _, _ = env.step("look around")
        assert obs == obs_orig


def test_multiple_instances():
    env1 = ScienceWorldEnv("task-1-boil")
    env2 = ScienceWorldEnv("task-1-boil")

    assert env1._gateway._gateway_client.port != env2._gateway._gateway_client.port

    obs1, _ = env1.reset()
    obs2, _ = env2.reset()

    # Check if the two observations are the same when ignoring the order in which objects are described.
    assert obs1 == obs2

    # Interact with one of the envs.
    env1.step("open door to art studio")

    # Check if the observations now differ from each other.
    obs1_1, _, _, _ = env1.step("look around")
    obs2_1, _, _, _ = env2.step("look around")
    assert obs1_1 != obs2_1

    # Resetting the second env doesn't affect the first one.
    env2.reset()

    obs1_2, _, _, _ = env1.step("look around")
    assert obs1_1 == obs1_2

    env2.step("open door to art studio")
    obs2_2, _, _, _ = env2.step("look around")
    assert obs1_2 == obs2_2
