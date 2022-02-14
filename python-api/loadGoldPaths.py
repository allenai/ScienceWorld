# loadGoldPaths.py

import json


filenameIn = "../goldsequences-7-8-11-12-13-14.json"
f = open(filenameIn)
data = json.load(f)

print(type(data))
print("Tasks stored in this gold data: " + str(data.keys()))

for taskIdx in data.keys():
    print("Task Index: " + str(taskIdx))

    taskData = data[taskIdx]
    taskName = taskData['taskName']
    goldSequences = taskData['goldActionSequences']

    for goldSequence in goldSequences:
        variationIdx = goldSequence['variationIdx']
        path = goldSequence['path']

        for step in path:
            action = step['action']
            obs = step['observation']
            score = step['score']
            isCompleted = step['isCompleted']

            print("> " + str(action))
            print(obs)
            print("")        
 