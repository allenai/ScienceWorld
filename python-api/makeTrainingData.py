# makeTrainingData.py

from os import listdir
from os.path import isfile, join
import json


def sanitize(strIn):
    return strIn.replace("\n", " ")

def pack(taskDesc, observation, userInput):
    oneline = "TASK: " + taskDesc + " OBS: " + observation + " RESP: " + userInput + " END:"
    return oneline

def packSource(taskDesc, observation):
    oneline = "TASK: " + taskDesc + " OBS: " + observation + " RESP: "
    return oneline

def packTarget(userInput):
    oneline = userInput + " END:"
    return oneline


# Load one JSON recording file from a ScienceWorld session
def loadAsText(filename):
    print("Loading " + filename)
    f = open(filename, 'r')
    data = json.load(f)    

    taskName = data['taskName']
    taskDesc = data['taskDesc']
    taskHistory = data['history']

    out = []
    lastObservation = ""
    for elem in taskHistory:
        observation = lastObservation                           # Compensates for history offset bug
        lastObservation = sanitize( elem['observation'] )        
        userInput = sanitize( elem['input'] )
        lastAction = sanitize( elem['input'] )
        score = elem['score']
        iterations = elem['iterations']

        oneline = pack(taskDesc, observation, userInput)

        out.append(oneline)
        
    return out

def loadAsJSONL(filename):
    print("Loading " + filename)
    f = open(filename, 'r')
    data = json.load(f)    

    taskName = data['taskName']
    taskDesc = data['taskDesc']
    taskHistory = data['history']

    out = []
    lastObservation = ""
    for elem in taskHistory:
        observation = lastObservation                           # Compensates for history offset bug
        lastObservation = sanitize( elem['observation'] )        
        userInput = sanitize( elem['input'] )
        score = elem['score']
        iterations = elem['iterations']

        sourceStr = packSource(taskDesc, observation)
        targetStr = packTarget(userInput)
        packed = {
            'source': sourceStr,
            'target': targetStr,
        }
        jsonStr = json.dumps(packed)

        out.append(jsonStr)
        
    return out



# Get a list of files
mypath = "saves/"
onlyfiles = [f for f in listdir(mypath) if isfile(join(mypath, f))]
onlyJSONFiles = [f for f in onlyfiles if f.endswith(".json")]

# Load each file
allLinesText = []
allLinesJSONL = []
for filename in onlyJSONFiles:
    allLinesText.extend( loadAsText(mypath + filename) )
    allLinesJSONL.extend( loadAsJSONL(mypath + filename) )



# Export the result
filenameOut = "saveout.txt"
print("Writing " + str(filenameOut))
f = open(filenameOut, "w")
for line in allLinesText:
    f.write(line + "\n")

filenameOut = "saveout.jsonl"
print("Writing " + str(filenameOut))
f = open(filenameOut, "w")
for line in allLinesJSONL:
    f.write(line + "\n")





