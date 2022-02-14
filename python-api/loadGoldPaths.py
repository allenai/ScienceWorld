# loadGoldPaths.py

import json


filenameIn = "../goldsequences-7-8-11-12-13-14.json"
f = open(filenameIn)
data = json.load(f)

print(type(data))
print("Tasks stored in this gold data: " + str(data.keys()))
numMoves = 0
numSequences = 0

for taskIdx in data.keys():
    print("Task Index: " + str(taskIdx))

    taskData = data[taskIdx]
    taskName = taskData['taskName']
    
    goldSequences = taskData['goldActionSequences']

    for goldSequence in goldSequences:
        variationIdx = goldSequence['variationIdx']
        taskDescription = goldSequence['taskDescription']
        path = goldSequence['path']

        print("\n\n-----------------------------------------------------------\n")

        print("TaskDescription: " + str(taskDescription))

        for step in path:
            action = step['action']
            obs = step['observation']
            score = step['score']
            isCompleted = step['isCompleted']

            print("> " + str(action))
            print(obs)
            print("")        
 
            numMoves +=1
        numSequences += 1


print("----------------------")
print("Summary Statistics:")
print("numTasks: " + str(len(data.keys())))
print("numSequences: " + str(numSequences))
print("numMoves: " + str(numMoves))