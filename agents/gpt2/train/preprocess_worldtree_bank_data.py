import os
import re
import json

data_dir = "/home/ruoyao/Documents/data/WorldtreeExplanationCorpusV2.1_Feb2020/explanations-plaintext"
train_file = "explanations.plaintext.train.txt"
dev_file = "explanations.plaintext.dev.txt"
test_file = "explanations.plaintext.test.txt"

def preprocess(data_path):
    with open(data_path) as f:
        raw_text = f.readlines()

    data = []
    begin_line = '^Question:.*'
    explanations = r'^(.*)\((.*)\) \((.*)\)$'
    new_item_done = True
    # line[0] Question: idx
    # line[1] Question: question
    # line[2] Correct Answer: answer
    # line[3] Explanation:
    # line[4+n] explanation
    i = 0
    while i < len(raw_text):
        if new_item_done and re.match(begin_line, raw_text[i]):
            new_item_done = False
            question = raw_text[i+1].strip()[10:] # remove the "Question: " at the beginning
            question = question.replace('\t', ' ')
            question = question.replace('\n', ' ')
            output = "[QUESTION] " + question + ' '
            answer = raw_text[i+2][16:-1]
            output = output + "[ANSWER] " + answer + " "
            j = i + 4
            while raw_text[j] != '\n':
                m = re.match(explanations, raw_text[j])
                explanation_text = m.group(1)
                importance = m.group(3)
                if importance == "ROLE: CENTRAL":
                    explanation = "[CENTRAL] " + explanation_text
                elif importance == "ROLE: BACKGROUND":
                    explanation = "[BACKGROUND] " + explanation_text
                elif importance == "ROLE: LEXGLUE":
                    explanation = "[LEXGLUE] " + explanation_text
                elif importance == "ROLE: ROLE":
                    explanation = "[ROLE] " + explanation_text
                elif importance == "ROLE: GROUNDING":
                    explanation = "[GROUNDING] " + explanation_text
                elif importance == "ROLE: NEG":
                    explanation = "[NEG] " + explanation_text
                elif importance == "ROLE: NE":
                    explanation = "[NE] " + explanation_text
                else:
                    print(importance)
                output = output + explanation
                j += 1
            i = j
            data.append({"text": output})
            new_item_done = True
        else:
            i += 1
    return data

train_data = preprocess(os.path.join(data_dir, train_file))
dev_data = preprocess(os.path.join(data_dir, dev_file))

with open("worldtree_train.json", "w") as f:
    for data in train_data:
        json.dump(data, f)
        f.write('\n')

with open("worldtree_dev.json", "w") as f:
    for data in dev_data:
        json.dump(data, f)
        f.write('\n')