# ScienceWorld

ScienceWorld is a text-based virtual environment centered around accomplishing tasks from the standardized elementary science curriculum.  This code accompanies the paper [ScienceWorld: Is your Textual Agent Smarter than a 5th grader?](ScienceWorld-preprint.pdf).

<h3 align="center"><img src="https://github.com/allenai/ScienceWorld/blob/main/media/scienceworld_environment.png" width="75%"/></h3>

### Examples

Read some of the playthrough transcripts available at: https://sciworld.apps.allenai.org/explore

### Citation
```
@misc{scienceworld2022,
    title={ScienceWorld: Is your Agent Smarter than a 5th Grader?},
    author={Ruoyao Wang and Peter Jansen and Marc-Alexandre C{\^o}t{\'e} and Prithviraj Ammanabrolu},
    year={2022},
    eprint={2203.07540},
    archivePrefix={arXiv},
    primaryClass={cs.CL},
    url={https://arxiv.org/abs/2203.07540}
}
```

# Quickstart
**Before running:** You will have to have `Java 1.8+` installed on your system (shipped with most linux distributions).

Install with pip:
```bash
conda create --name scienceworld python=3.8
conda activate scienceworld
pip install scienceworld
```

Run an example random agent, on task 13 (classification: place a non-living thing in a box), for 5 episodes:
> python examples/random_agent.py --task-num=13 --num-episodes=5 --simplifications-preset easy

Run a user console where you can interact with the environment, on task 3 (change of state: melting):
> python examples/human.py --task-num=3 --num-episodes=5


# Web Server Demo

A web server demo is also available, that allows running a ScienceWorld user console that can be interacted with in a web browser.

<h3 align="center"><img src="https://github.com/allenai/ScienceWorld/blob/main/media/web_demo_screenshot.png" width="75%"/></h3>

To run the web server demo:
```bash
conda create --name scienceworld python=3.8
conda activate scienceworld
pip install scienceworld[webserver]
```

Run the web server:
> python examples/scienceworld-web-server-example.py

Point your web browser to:
`localhost:8080`


# ScienceWorld Design
ScienceWorld is written in Scala (2.12.9), and compiles using `sbt` into a JAR file that is run with `java`.  For convenience, a `python` API is provided, which interfaces using the `py4j` package.

**Ports:** ScienceWorld is nominally run as a server, which interfaces to the Python API with `py4j` through a port.  The default port is `25335`.  The actual port used will be 25335 + the thread number provided when a ScienceWorld class is instantiated.

**Threads:** ScienceWorld is designed to run many threads simultaneously, if desired.  To do so, initialize one `ScienceWorldEnv` object per thread, and provide a unique `threadNum` for each thread.  Don't forget to close down servers that you instantiate using the `env.shutdown()` command.  If you are spawning many threads (10+) at the same time, you may wish to add a short delay (5-10 seconds) after initialization to wait for all the servers to initialize.

# Tasks
The subtasks and their associated subtask indices are listed below.  Subtask indices are automatically assigned by sorting task names:

| SubTask #  | Task Name                                          |
|-----|-----------------------------------------------------------|
| 0  | task-1-boil                                               |
| 1  | task-1-change-the-state-of-matter-of                      |
| 2  | task-1-freeze                                             |
| 3  | task-1-melt                                               |
| 4  | task-10-measure-melting-point-(known-substance)           |
| 5  | task-10-measure-melting-point-(unknown-substance)         |
| 6  | task-10-use-thermometer                                   |
| 7  | task-2-power-component                                    |
| 8  | task-2-power-component-(renewable-vs-nonrenewable-energy) |
| 9  | task-2a-test-conductivity                                 |
| 10 | task-2a-test-conductivity-of-unknown-substances           |
| 11 | task-3-find-animal                                        |
| 12 | task-3-find-living-thing                                  |
| 13 | task-3-find-non-living-thing                              |
| 14 | task-3-find-plant                                         |
| 15 | task-4-grow-fruit                                         |
| 16 | task-4-grow-plant                                         |
| 17 | task-5-chemistry-mix                                      |
| 18 | task-5-chemistry-mix-paint-(secondary-color)              |
| 19 | task-5-chemistry-mix-paint-(tertiary-color)               |
| 20 | task-6-lifespan-(longest-lived)                           |
| 21 | task-6-lifespan-(longest-lived-then-shortest-lived)       |
| 22 | task-6-lifespan-(shortest-lived)                          |
| 23 | task-7-identify-life-stages-1 (animal)                    |
| 24 | task-7-identify-life-stages-2 (plant)                     |
| 25 | task-8-inclined-plane-determine-angle                     |
| 26 | task-8-inclined-plane-friction-(named-surfaces)           |
| 27 | task-8-inclined-plane-friction-(unnamed-surfaces)         |
| 28 | task-9-mendellian-genetics-(known-plant)                  |
| 29 | task-9-mendellian-genetics-(unknown-plant)                |


# Baseline Agents
Coming soon!

# Additional Documentation
Coming soon!
