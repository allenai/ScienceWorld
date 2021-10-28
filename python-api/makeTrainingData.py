# makeTrainingData.py

from os import listdir
from os.path import isfile, join
import json


def sanitize(strIn):
    return strIn.replace("\n", " ")

# Load one JSON recording file from a ScienceWorld session
def load(filename):
    print("Loading " + filename)
    f = open(filename, 'r')
    data = json.load(f)    

    taskName = data['taskName']
    taskDesc = data['taskDesc']
    taskHistory = data['history']

    out = []
    for elem in taskHistory:
        observation = sanitize( elem['observation'] )
        userInput = sanitize( elem['input'] )
        score = elem['score']
        iterations = elem['iterations']

        oneline = "TASK: " + taskDesc + "OBS: " + observation + " RESP: " + userInput

        out.append(oneline)
        
    return out



# Get a list of files
mypath = "saves/"
onlyfiles = [f for f in listdir(mypath) if isfile(join(mypath, f))]
onlyJSONFiles = [f for f in onlyfiles if f.endswith(".json")]

# Load each file
allLines = []
for filename in onlyJSONFiles:
    fileLines = load(mypath + filename)
    allLines.extend(fileLines)

# Export the result
filenameOut = "saveout.txt"
print("Writing " + str(filenameOut))
f = open(filenameOut, "w")
for line in allLines:
    f.write(line + "\n")



