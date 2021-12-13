import json
import os

data_dir = "/home/ruoyao/Documents/datasets/emnlp-2021-teacher-ratings/ratings-json-train-dev-test"
train_file = "wt21-train.teacher-ratings.json"
dev_file = "wt21-dev.teacher-ratings.json"

def preprocess(data_file, name):
    output = []

    with open(os.path.join(data_dir, data_file)) as f:
        data_list = json.load(f)
    
    for data in data_list['rankingProblems']:
        query_text = data["queryText"]
        for support_info in data["documents"]:
            if support_info["relevance"] >= 4:
                support_sentence = " [DOC] " + support_info["docText"]
                query_text += support_sentence
        assert(len(query_text.split()) < 400)
        output.append({"text": query_text})
    
    with open(f"teacher_rating_{name}.json", "w") as f:
        for data in output:
            json.dump(data, f)
            f.write('\n')



preprocess(train_file, 'train')
preprocess(dev_file, 'val')