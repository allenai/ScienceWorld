import os
from collections import OrderedDict


def is_in_debug_mode() -> bool:
    """Determine whether debug mode should be enabled.

    Debug mode sends JAR output to the console and allows the user to attach a debugger at port 5005.

    To enable debug mode, set the environment variable SCIENCEWORLD_DEBUG to "1" or "true".
    """
    if "SCIENCEWORLD_DEBUG" not in os.environ:
        return False
    env_value = os.environ["SCIENCEWORLD_DEBUG"].lower()
    if env_value in {"1", "true"}:
        return True
    elif env_value in {"", "0", "false"}:
        return False
    else:
        raise ValueError(f'{env_value!r} is not a valid value for "SCIENCEWORLD_DEBUG"')

DEBUG_MODE = is_in_debug_mode()

ID2TASK = OrderedDict([
    ("1-1", "boil"),
    ("1-2", "melt"),
    ("1-3", "freeze"),
    ("1-4", "change-the-state-of-matter-of"),
    ("2-1", "use-thermometer"),
    ("2-2", "measure-melting-point-(known-substance)"),
    ("2-3", "measure-melting-point-(unknown-substance)"),
    ("3-1", "power-component"),
    ("3-2", "power-component-(renewable-vs-nonrenewable-energy)"),
    ("3-3", "test-conductivity"),
    ("3-4", "test-conductivity-of-unknown-substances"),
    ("4-1", "find-living-thing"),
    ("4-2", "find-non-living-thing"),
    ("4-3", "find-plant"),
    ("4-4", "find-animal"),
    ("5-1", "grow-plant"),
    ("5-2", "grow-fruit"),
    ("6-1", "chemistry-mix"),
    ("6-2", "chemistry-mix-paint-(secondary-color)"),
    ("6-3", "chemistry-mix-paint-(tertiary-color)"),
    ("7-1", "lifespan-(longest-lived)"),
    ("7-2", "lifespan-(shortest-lived)"),
    ("7-3", "lifespan-(longest-lived-then-shortest-lived)"),
    ("8-1", "identify-life-stages-1"),
    ("8-2", "identify-life-stages-2"),
    ("9-1", "inclined-plane-determine-angle"),
    ("9-2", "inclined-plane-friction-(named-surfaces)"),
    ("9-3", "inclined-plane-friction-(unnamed-surfaces)"),
    ("10-1", "mendelian-genetics-(known-plant)"),
    ("10-2", "mendelian-genetics-(unknown-plant)"),
])

NAME2ID = {
    # Names used in the paper.
    "Matter Changes of State (Melting)": "1-1",
    "Matter Changes of State (Freezing)": "1-3",
    "Matter Changes of State (Any)": "1-4",
    "Measurement Use Thermometer": "2-1",
    "Measurement Measuring Boiling Point (known)": "2-2",
    "Measurement Measuring Boiling Point (unknown)": "2-3",
    "Electricity Create a circuit": "3-1",
    "Electricity Renewable vs Non-renewable Energy": "3-2",
    "Electricity Test Conductivity (known)": "3-3",
    "Electricity Test Conductivity (unknown)": "3-4",
    "Classification Find a living thing": "4-1",
    "Classification Find a non-living thing": "4-2",
    "Classification Find a plant": "4-3",
    "Classification Find an animal": "4-4",
    "Biology Grow a plant": "5-1",
    "Biology Grow a fruit": "5-2",
    "Chemistry Mixing (generic)": "6-1",
    "Chemistry Mixing paints (secondary colours)": "6-2",
    "Chemistry Mixing paints (tertiary colours)": "6-3",
    "Biology Identify longest-lived animal": "7-1",
    "Biology Identify shortest-lived animal": "7-2",
    "Biology Identify longest-then-shortest-lived animal": "7-3",
    "Biology Identify life stages (plant)": "8-1",
    "Biology Identify life stages (animal)": "8-2",
    "Forces Inclined Planes (determine angle)": "9-1",
    "Forces Friction (known surfaces)": "9-2",
    "Forces Friction (unknown surfaces)": "9-3",
    "Biology Mendelian Genetics (known plants)": "10-1",
    "Biology Mendelian Genetics (unknown plants)": "10-2",
}
