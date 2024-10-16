from scienceworld import ScienceWorldEnv


def test_observation_is_deterministic():
    env = ScienceWorldEnv("1-1")
    obs_orig, _ = env.reset()

    for _ in range(30):
        obs, _ = env.reset()
        assert obs == obs_orig

        obs, _, _, _ = env.step("look around")
        assert obs == obs_orig


def test_multiple_instances():
    env1 = ScienceWorldEnv("1-1")
    env2 = ScienceWorldEnv("1-1")

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


def test_closing_env():
    env = ScienceWorldEnv()
    env.task_names  # Load task names.
    assert env._gateway.java_process.poll() is None
    env.close()
    env._gateway.java_process.wait(5)
    assert env._gateway.java_process.poll() is not None


def test_variation_sets_are_disjoint():
    env = ScienceWorldEnv()

    for task in env.get_task_names():
        env.load(task)
        train = set(env.get_variations_train())
        dev = set(env.get_variations_dev())
        test = set(env.get_variations_test())
        assert set.isdisjoint(train, dev)
        assert set.isdisjoint(train, test)
        assert set.isdisjoint(dev, test)


def test_load():
    # Test loading former task names.
    FORMER_TASK_NAMES = {
        "1-1": "task-1-boil",
        "1-2": "task-1-melt",
        "1-3": "task-1-freeze",
        "1-4": "task-1-change-the-state-of-matter-of",
        "2-1": "task-10-use-thermometer",
        "2-2": "task-10-measure-melting-point-(known-substance)",
        "2-3": "task-10-measure-melting-point-(unknown-substance)",
        "3-1": "task-2-power-component",
        "3-2": "task-2-power-component-(renewable-vs-nonrenewable-energy)",
        "3-3": "task-2a-test-conductivity",
        "3-4": "task-2a-test-conductivity-of-unknown-substances",
        "4-1": "task-3-find-living-thing",
        "4-2": "task-3-find-non-living-thing",
        "4-3": "task-3-find-plant",
        "4-4": "task-3-find-animal",
        "5-1": "task-4-grow-plant",
        "5-2": "task-4-grow-fruit",
        "6-1": "task-5-chemistry-mix",
        "6-2": "task-5-chemistry-mix-paint-(secondary-color)",
        "6-3": "task-5-chemistry-mix-paint-(tertiary-color)",
        "7-1": "task-6-lifespan-(longest-lived)",
        "7-2": "task-6-lifespan-(shortest-lived)",
        "7-3": "task-6-lifespan-(longest-lived-then-shortest-lived)",
        "8-1": "task-7-identify-life-stages-1",
        "8-2": "task-7-identify-life-stages-2",
        "9-1": "task-8-inclined-plane-determine-angle",
        "9-2": "task-8-inclined-plane-friction-(named-surfaces)",
        "9-3": "task-8-inclined-plane-friction-(unnamed-surfaces)",
        "10-1": "task-9-mendellian-genetics-(known-plant)",
        "10-2": "task-9-mendellian-genetics-(unknown-plant)"
    }

    env = ScienceWorldEnv()
    for task_id, former_task_name in FORMER_TASK_NAMES.items():
        env.load(task_id)
        obs1, info1 = env.reset()

        # Load a task using its former name.
        env.load(former_task_name)
        obs2, info2 = env.reset()
        assert obs1 == obs2, f"{task_id} is not the same as {former_task_name}"
        assert info1 == info2, f"{task_id} is not the same as {former_task_name}"

        env.server.load(former_task_name, 0, "", False)
        obs2, _, _, info2 = env.step("look around")
        assert obs1 == obs2, f"{task_id} is not the same as {former_task_name}"
        assert info1 == info2, f"{task_id} is not the same as {former_task_name}"

        # Load a task using its new name.
        task_name = env.tasks[task_id]
        env.load(task_name)
        obs2, info2 = env.reset()
        assert obs1 == obs2, f"{task_id} is not the same as {task_name}"
        assert info1 == info2, f"{task_id} is not the same as {task_name}"


def test_consistent_task_names():
    """Verify that Scala and Python code use the same task names."""
    env = ScienceWorldEnv()
    assert sorted(env.task_names) == sorted(env.get_task_names())


def test_obj_tree():
    env = ScienceWorldEnv("1-1")
    env.reset()
    obj_tree = env.getObjectTree()
    print(obj_tree)
