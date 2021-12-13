import json
import os

data_dir = "/home/ruoyao/Documents/datasets/entailment_trees_emnlp2021_data_v2/dataset/task_1"
train_file = "train.jsonl"
dev_file = "dev.jsonl"

def preprocess(data_file, name):
    output = []
    with open(os.path.join(data_dir, data_file)) as f:
        data_list = list(f)

    for json_str in data_list:
        data = json.loads(json_str)
        output.append({"test": data["full_text_proof"]})

    with open(f"entailment_bank_{name}.json", "w") as f:
        for data in output:
            json.dump(data, f)
            f.write('\n')

preprocess(train_file, "train")
preprocess(dev_file, "val")
