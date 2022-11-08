import numpy as np

import gymnasium as gym
from gymnasium import spaces

import scienceworld


class String(spaces.Space):
    def __init__(self, ):
        super().__init__(dtype=str)

    def sample(self):
        return ''

    def contains(self, obj):
        return isinstance(obj, str)

    def __eq__(self, other) -> bool:
        """Check whether ``other`` is equivalent to this instance."""
        return (
            isinstance(other, String)
        )


class ScienceWorldEnv(gym.Env):
    metadata = {"render_modes": ["human", "ansi"]}

    def __init__(self, render_mode=None):
        self.env = scienceworld.ScienceWorldEnv()

        assert render_mode is None or render_mode in self.metadata["render_modes"]

        self.observation_space = String()
        self.action_space = String()
        self.options = {}
        self.variation_id = 0

    def reset(self, seed=None, options=None):
        # We need the following line to seed self.np_random
        super().reset(seed=seed)

        if options is not None:
            self.options = dict(options)

            task = self.options.get("task")
            self.env.load(task)

            variations = self.options.get("variation")
            if variations == "train" or variations is None:
                variations = self.env.getVariationsTrain()
            elif variations == "dev":
                variations = self.env.getVariationsDev()
            elif variations == "test":
                variations = self.env.getVariationsTest()
            elif isinstance(variations, int):
                variations = [variations]

            self.options["variation"] = list(variations)
            self.np_random.shuffle(self.options["variation"])
            self.variation_id = 0

        task = self.options["task"]
        variation = self.options["variation"][self.variation_id]
        simplification = self.options.get("simplification", "")
        generate_gold_path = self.options.get("generate_gold_path", False)
        # TODO: is task is not provided, choose one at random.

        # Advance variation counter.
        self.variation_id = (self.variation_id + 1) % len(self.options["variation"])

        self.env.load(task, variation, simplification, generate_gold_path)
        self.observation, info = self.env.reset()
        self.last_command = "look around"

        if self.render_mode == "human":
            print(">", self.last_command, "\n", self.observation)

        return self.observation, info

    def step(self, command):
        self.last_command = command
        self.observation, reward, terminated, info = self.env.step(command)

        if self.render_mode == "human":
            print(">", self.last_command, "\n", self.observation)

        return self.observation, reward, terminated, False, info  # Truncated is always False


    def render(self):
        if self.render_mode == "ansi":
            return ">" + self.last_command + "\n" + self.observation
