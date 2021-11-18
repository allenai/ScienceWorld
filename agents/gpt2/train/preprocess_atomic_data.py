import json

data_file = "/data/ai2-mosaic-public/ATOMIC10X.jsonl"

with open(data_file) as f:
    json_list = list(f)

train_data = {"texts":[],"labels":[]}
val_data = {"texts":[],"labels":[]}
test_data = {"texts":[],"labels":[]}

for data in json_list:
    raw_data = json.loads(data)
    if raw_data["p_valid_model"] < 0.8:
        continue
    text = "[GEN]" + " <head> " + raw_data["head"] + " </head> "\
            +"<relation> " + raw_data["relation"] + " </relation>"
    label = raw_data["tail"]
    if raw_data["split"] == "train":
        train_data["texts"].append(text)
        train_data["labels"].append(label)
    elif raw_data["split"] == "val":
        val_data["texts"].append(text)
        val_data["labels"].append(label)
    else:
        test_data["texts"].append(text)
        test_data["labels"].append(label)

with open("atomic_train.json", "w") as f:
    json.dump(train_data, f)

with open("atomic_train.json", "w") as f:
    json.dump(train_data, f)

with open("atomic_train.json", "w") as f:
    json.dump(train_data, f)