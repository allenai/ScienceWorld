from .version import __version__
from .scienceworld import ScienceWorldEnv
from .scienceworld import BufferedHistorySaver

from gymnasium.envs.registration import register

register(
     id="ScienceWorld-v0",
     entry_point="scienceworld.scienceworld_gym:ScienceWorldEnv",
)
