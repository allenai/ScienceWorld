# scienceworld-web-server-example.py
#
#   Uses pywebio to open a simple web-based interface for running ScienceWorld in the web browser. 
#   After running, open a web browser and point to 'localhost:8080'. 
#   It may take a 5-10 seconds to initialize the server on the first run. 
#
#   pip install -U pywebio                          (for web server)

from py4j.java_gateway import JavaGateway, GatewayParameters
import subprocess
import random
import timeit
import time
import json
import py4j

from datetime import datetime

from scienceworld import ScienceWorldEnv

# Web interface
from pywebio.input import *
from pywebio.output import *
from pywebio.input import textarea, input
from pywebio.session import *
from pywebio import start_server

class OutputLog:
    #
    # Constructor
    #
    def __init__(self):
        self.out = ""
        self.title = ""

    def setTitle(self, titleStr:str):
        self.title = titleStr

    def addHeading(self, strIn:str):
        self.out += "<h1>" + strIn + "</h1>\n"

    def addSubheading(self, strIn:str):
        self.out += "<h2>" + strIn + "</h2>\n"

    def addHorizontalRule(self):
        self.out += "<hr>\n"

    def addPreformattedText(self, strIn:str):
        self.out += "<pre>\n" + strIn + "\n</pre>\n"

    def addStr(self, strIn:str):
        self.out += strIn + "\n"

    def getHTML(self):
        out = "<html>"
        out += "<head><title>" + self.title + "</title></head>\n"
        out += "<body>\n"
        out += self.out
        out += "</body>\n"
        out += "</html>\n"

        return out

#
#   Save JSON history
#
def saveJSONHistory(history:list):
    pathOut = "recordings/"
    taskName = history[-1]['taskName']
    varIdx = history[-1]['variationIdx']
    score = history[-1]['score']
    
    result = "success"
    if (score != 1.0):
        result = "failure"

    # timestamp
    dateTimeObj = datetime.now()
    timestampStr = dateTimeObj.strftime("timestamp%Y-%b-%d-%H-%M-%S")

    filenameOut = pathOut + "recording-" + str(taskName) + "-var" + str(varIdx) + "-" + str(result) + str(timestampStr) + ".json"

    print ("Exporting " + filenameOut)

    with open(filenameOut, "w") as jsonFile:
        json.dump(history, jsonFile, indent=4, sort_keys=True)


#
#   Web server main
#
def app():    
    exitCommands = ["quit", "exit"]

    simplificationStr = ""    
    htmlLog = OutputLog()
    
    set_env(title='ScienceWorld Demo', auto_scroll_bottom=True)    

    # Initialize environment    
    env = ScienceWorldEnv("", serverPath=None, envStepLimit = 100, threadNum = 5)

    put_markdown('## Science World (Text Simulation)')
    #put_button("Click here to export transcript", onclick=lambda: , color='success', outline=True)

    htmlLog.addHeading("Science World (Text Simulation)")
    htmlLog.addHorizontalRule()    

    taskName = select("Select a task:", env.getTaskNames())    
    maxVariations = env.getMaxVariations(taskName)

    #variationIdx = slider("Task Variation: ", min_value=0, max_value=(maxVariations-1))    
    variationIdx = input('Enter the task variation (min = 0, max = ' + str(maxVariations) + "):")
    variationIdx = int(variationIdx) if variationIdx.isdigit() else 0

    # Load environment
    env.load(taskName, variationIdx, simplificationStr)    
    initialObs, initialDict = env.reset()
    #time.sleep(1)

    #print("Possible actions: " + str(env.getPossibleActions()) )
    #print("Possible objects: " + str(env.getPossibleObjects()) )
    #print("Possible action/object combinations: " + str(env.getPossibleActionObjectCombinations()))

    put_table([
        ["Task", env.getTaskDescription()],
        ["Variation", str(variationIdx) + " / " + str(maxVariations)]
    ])

    htmlLog.addStr("<b>Task:</b> " + env.getTaskDescription() + "<br>")
    htmlLog.addStr("<b>Variation:</b> " + str(variationIdx) + "<br>")
    htmlLog.addHorizontalRule()

    historyRecording = []

    userInputStr = "look around"        # First action
    consoleMoveCount = 0
    while (userInputStr not in exitCommands):
        #put_markdown("### Move " + str(env.getNumMoves()) )
        #htmlLog.addSubheading("Move " + str(env.getNumMoves()))
        put_markdown("### Move " + str(consoleMoveCount) )
        htmlLog.addSubheading("Move " + str(consoleMoveCount))

        # Send user input, get response
        observation, score, isCompleted, additionalInfo = env.step(userInputStr)        
        
        # Output (server)
        put_text(observation)
        put_table([
            ["Score:", str(score)],
            ["isCompleted:", str(isCompleted)]
        ])

        # Output (log)
        htmlLog.addPreformattedText(observation)
        if (score >= 1.0):
            htmlLog.addStr("<font color=green>Task Score: " + str(score) + "  (isCompleted: " + str(isCompleted) + ") </font><br><br>")
        else:
            htmlLog.addStr("<font color=grey>Task Score: " + str(score) + "  (isCompleted: " + str(isCompleted) + ") </font><br><br>")
        
        logFilename = "log-" + taskName + ".html"
        put_file(logFilename, htmlLog.getHTML().encode(), '(click here to export transcript)')

        #print("\n" + observation)
        #print("Score: " + str(score))
        #print("isCompleted: " + str(isCompleted))

        # Get user input
        userInputStr = input('Enter your action (`help` for list of actions, `objects` for list of object referents) ')
        
        # Sanitize input
        userInputStr = userInputStr.lower().strip()

        put_text("> " + userInputStr)
        htmlLog.addStr("User Input:<br>")
        htmlLog.addStr("<i> > " + userInputStr + "</i><br>")
    
        # Record history
        packed = {
            'observation': observation, 
            'score': score,
            'isCompleted': isCompleted,
            'userInput': userInputStr,
            'taskName': taskName,
            'taskDescription': env.getTaskDescription(),
            'look': env.look(),
            'inventory': env.inventory(),
            'variationIdx': variationIdx,
            'consoleMoveCount': consoleMoveCount,
        }
        historyRecording.append(packed) 
        
        # If this session is completed, save the recording
        if (isCompleted == True):
            saveJSONHistory(historyRecording)

        consoleMoveCount += 1

        time.sleep(1)    


    print("Shutting down server...")    
    env.shutdown()

    print("Completed.")



if __name__ == '__main__':
    start_server(app, port=8080, debug=True)
