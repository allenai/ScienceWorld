<h1 align="center">
ScienceWorld
</h1>

<p align="center">
<!-- Version badge using shields.io -->
  <a href="https://github.com/allenai/ScienceWorld/releases">
    <img src="https://img.shields.io/github/v/release/allenai/ScienceWorld">
  </a>
<!-- Link to tutorials badge using shields.io -->
  <a href="https://huggingface.co/spaces/MarcCote/ScienceWorld">
    <img src="https://img.shields.io/badge/🤗-Demo-yellow">
  </a>
<!-- Follow on twitter badge using shields.io -->
  <a href="https://sciworld.apps.allenai.org">
    <img src="https://img.shields.io/badge/Website-green">
  </a>
</p>

ScienceWorld is a text-based virtual environment centered around accomplishing tasks from the standardized elementary science curriculum.  This code accompanies the paper [ScienceWorld: Is your Textual Agent Smarter than a 5th grader?](https://arxiv.org/abs/2203.07540).

<h3 align="center"><img src="https://github.com/allenai/ScienceWorld/blob/main/media/scienceworld_environment.png" width="75%"/></h3>

### Demo and examples

You can try ScienceWorld yourself via our [HuggingFace Space](https://huggingface.co/spaces/MarcCote/ScienceWorld) or read some of the [playthrough transcripts](https://sciworld.apps.allenai.org/explore).

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
**Before running:** You will have to have `Java 1.8+` installed on your system (shipped with most linux distributions) and `Python 3.8+`. We recommend creating a conda environment like this:

```bash
conda create --name scienceworld python=3.8
conda activate scienceworld
```

Then, install ScienceWorld either from PyPi:

    pip install scienceworld

or from source in development mode:

    git clone https://github.com/allenai/ScienceWorld.git
    cd ScienceWorld
    pip install .


Run an example random agent, on task 13 (classification: place a non-living thing in a box), for 5 episodes:

    python examples/random_agent.py --task-num=13 --num-episodes=5 --simplifications-preset easy

Run a user console where you can interact with the environment, on task 3 (change of state: melting):

    python examples/human.py --task-num=3 --num-episodes=5


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

    python examples/scienceworld-web-server-example.py

Point your web browser to:
`localhost:8080`


# ScienceWorld Design
ScienceWorld is written in Scala (2.12.9), and compiles using `sbt` into a JAR file that is run with Java.  For convenience, a Python API is provided (Python >= 3.8), which interfaces using the `py4j` package.

If you modified the Scala code, you can recompile the JAR file by running:
```bash
./simulator/package.sh
pip install -e .
```

# Tasks
The tasks are listed in the table below along with their number of variations. Either the task ID or its name can be used to a task with `env.load()`.

| Task ID | Task Name                                | # Variations |
|-------|----------------------------------------------------|------|
|   1-1 |                                             boil |   30 |
|   1-2 |                                             melt |   30 |
|   1-3 |                                           freeze |   30 |
|   1-4 |                    change-the-state-of-matter-of |   30 |
|   2-1 |                                  use-thermometer |  540 |
|   2-2 |            measure-melting-point-known-substance |  436 |
|   2-3 |          measure-melting-point-unknown-substance |  300 |
|   3-1 |                                  power-component |   20 |
|   3-2 | power-component-renewable-vs-nonrenewable-energy |   20 |
|   3-3 |                                test-conductivity |  900 |
|   3-4 |          test-conductivity-of-unknown-substances |  600 |
|   4-1 |                                find-living-thing |  300 |
|   4-2 |                            find-non-living-thing |  300 |
|   4-3 |                                       find-plant |  300 |
|   4-4 |                                      find-animal |  300 |
|   5-1 |                                       grow-plant |  126 |
|   5-2 |                                       grow-fruit |  126 |
|   6-1 |                                    chemistry-mix |   32 |
|   6-2 |              chemistry-mix-paint-secondary-color |   36 |
|   6-3 |               chemistry-mix-paint-tertiary-color |   36 |
|   7-1 |                           lifespan-longest-lived |  125 |
|   7-2 |                          lifespan-shortest-lived |  125 |
|   7-3 |       lifespan-longest-lived-then-shortest-lived |  125 |
|   8-1 |                           identify-life-stages-1 |   14 |
|   8-2 |                           identify-life-stages-2 |   10 |
|   9-1 |                   inclined-plane-determine-angle |  168 |
|   9-2 |           inclined-plane-friction-named-surfaces | 1386 |
|   9-3 |         inclined-plane-friction-unnamed-surfaces |  162 |
|  10-1 |                   mendelian-genetics-known-plant |  120 |
|  10-2 |                 mendelian-genetics-unknown-plant |  480 |

# Baseline Agents
**DRRN:** https://github.com/cognitiveailab/drrn-scienceworld

**KG-A2C:** https://github.com/cognitiveailab/kga2c-scienceworld

**CALM:** https://github.com/cognitiveailab/calm-scienceworld

**Behavior Cloning and Decision Transformer:** https://github.com/cognitiveailab/t5-scienceworld
