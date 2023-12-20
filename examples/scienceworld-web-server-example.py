# scienceworld-web-server-example.py
#
#   Uses pywebio to open a simple web-based interface for running ScienceWorld in the web browser.
#   After running, open a web browser and point to 'localhost:8080'.
#   It may take a 5-10 seconds to initialize the server on the first run.
#
#   pip install -U pywebio                          (for web server)

import json
import os.path
import time
import argparse

from datetime import datetime

from scienceworld import ScienceWorldEnv

import pywebio
import pywebio.output as pywebio_out


class OutputLog:
    #
    # Constructor
    #
    def __init__(self):
        self.out = ""
        self.title = ""

    def setTitle(self, titleStr: str):
        self.title = titleStr

    def addHeading(self, strIn: str):
        self.out += "<h1>" + strIn + "</h1>\n"

    def addSubheading(self, strIn: str):
        self.out += "<h2>" + strIn + "</h2>\n"

    def addHorizontalRule(self):
        self.out += "<hr>\n"

    def addPreformattedText(self, strIn: str):
        self.out += "<pre>\n" + strIn + "\n</pre>\n"

    def addStr(self, strIn: str):
        self.out += strIn + "\n"

    def getHTML(self):
        out = "<html>"
        out += "<head><title>" + self.title + "</title></head>\n"
        out += "<body>\n"
        out += self.out
        out += "</body>\n"
        out += "</html>\n"

        return out


def saveJSONHistory(history: list):
    pathOut = "recordings/"
    if not os.path.isdir(pathOut):
        os.mkdir(pathOut)
    taskName = history[-1]['taskName']
    varIdx = history[-1]['variationIdx']
    score = history[-1]['score']

    result = "success"
    if (score != 1.0):
        result = "failure"

    # timestamp
    dateTimeObj = datetime.now()
    timestampStr = dateTimeObj.strftime("timestamp%Y-%M-%d-%H-%M-%S")

    filenameOut = (pathOut + "recording-" + str(taskName) + "-var" + str(varIdx) +
                   "-" + str(result) + "-" + str(timestampStr) + ".json")

    print("Exporting " + filenameOut)

    with open(filenameOut, "w") as jsonFile:
        json.dump(history, jsonFile, indent=4, sort_keys=True)


#
#   Web server main
#
def app():
    exitCommands = ["quit", "exit"]

    simplificationStr = ""
    htmlLog = OutputLog()

    pywebio.session.set_env(title='ScienceWorld Demo', auto_scroll_bottom=True)

    # Initialize environment
    env = ScienceWorldEnv("", serverPath=None, envStepLimit=10_000)

    pywebio_out.put_markdown('## Science World (Text Simulation)')
    # put_button("Click here to export transcript", onclick=lambda: , color='success', outline=True)

    htmlLog.addHeading("Science World (Text Simulation)")
    htmlLog.addHorizontalRule()

    taskName = pywebio.input.select("Select a task:", env.get_task_names())
    maxVariations = env.get_max_variations(taskName)

    # variationIdx = slider("Task Variation: ", min_value=0, max_value=(maxVariations-1))
    variationIdx = pywebio.input.input('Enter the task variation (min = 0, max = ' + str(maxVariations) + "):")
    variationIdx = int(variationIdx) if variationIdx.isdigit() else 0

    # Load environment
    env.load(taskName, variationIdx, simplificationStr)
    initialObs, initialDict = env.reset()
    # time.sleep(1)

    # print("Possible actions: " + str(env.getPossibleActions()) )
    # print("Possible objects: " + str(env.getPossibleObjects()) )
    # print("Possible action/object combinations: " + str(env.getPossibleActionObjectCombinations()))

    pywebio_out.put_table([
        ["Task", env.get_task_description()],
        ["Variation", str(variationIdx) + " / " + str(maxVariations)]
    ])

    htmlLog.addStr("<b>Task:</b> " + env.get_task_description() + "<br>")
    htmlLog.addStr("<b>Variation:</b> " + str(variationIdx) + "<br>")
    htmlLog.addHorizontalRule()

    historyRecording = []

    userInputStr = "look around"        # First action
    consoleMoveCount = 0
    while (userInputStr not in exitCommands):
        # put_markdown("### Move " + str(env.getNumMoves()) )
        # htmlLog.addSubheading("Move " + str(env.getNumMoves()))
        pywebio_out.put_markdown("### Move " + str(consoleMoveCount))
        htmlLog.addSubheading("Move " + str(consoleMoveCount))

        # Send user input, get response
        observation, reward, isCompleted, additionalInfo = env.step(userInputStr)
        score = additionalInfo['score']

        # Output (server)
        pywebio_out.put_text(observation)
        pywebio_out.put_table([
            ["Reward:", str(reward)],
            ["Score:", str(score)],
            ["isCompleted:", str(isCompleted)]
        ])

        # Output (log)
        htmlLog.addPreformattedText(observation)
        if (score >= 1.0):
            htmlLog.addStr("<font color=green>Task Score: " + str(score) +
                           "  (isCompleted: " + str(isCompleted) + ") </font><br><br>")
        else:
            htmlLog.addStr("<font color=grey>Task Score: " + str(score) +
                           "  (isCompleted: " + str(isCompleted) + ") </font><br><br>")

        logFilename = "log-" + taskName + ".html"
        pywebio_out.put_file(logFilename, htmlLog.getHTML().encode(), '(click here to export transcript)')

        # print("\n" + observation)
        # print("Score: " + str(score))
        # print("isCompleted: " + str(isCompleted))

        # Record history
        packed = {
            'observation': observation,
            'reward': reward,
            'score': score,
            'isCompleted': isCompleted,
            'userInput': userInputStr,
            'taskName': taskName,
            'taskDescription': env.get_task_description(),
            'look': env.look(),
            'inventory': env.inventory(),
            'variationIdx': variationIdx,
            'consoleMoveCount': consoleMoveCount,
        }
        historyRecording.append(packed)

        # If this session is completed, save the recording
        if isCompleted:
            saveJSONHistory(historyRecording)

        # Get user input
        userInputStr = pywebio.input.input('Enter your action (`help` for list' +
                                           'of actions, `objects` for list of object referents) ')

        # Sanitize input
        userInputStr = userInputStr.lower().strip()

        pywebio_out.put_text("> " + userInputStr)
        htmlLog.addStr("User Input:<br>")
        htmlLog.addStr("<i> > " + userInputStr + "</i><br>")

        consoleMoveCount += 1

        time.sleep(1)

    print("Completed.")


def parse_args():
    desc = "Launch a webserver to interact with ScienceWorld from your browser."
    parser = argparse.ArgumentParser(desc)
    parser.add_argument("--port", type=int, default=8080,
                        help="Port to use for the webserver.")
    parser.add_argument("--debug", action="store_true",
                        help="Run webserver in debug mode.")

    return parser.parse_args()


if __name__ == '__main__':
    args = parse_args()
    pywebio.start_server(app, port=args.port, debug=args.debug)
