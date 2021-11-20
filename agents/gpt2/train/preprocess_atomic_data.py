import json

data_file = "/data/ai2-mosaic-public/ATOMIC10X.jsonl"

with open(data_file) as f:
    json_list = list(f)

train_data = []
val_data = []
test_data = []

for data in json_list:
    raw_data = json.loads(data)
    if raw_data["p_valid_model"] < 0.8:
        continue
    if raw_data["split"] != "test":
        text = "<head> " + raw_data["head"] + " </head> "\
                + "<relation> " + raw_data["relation"] + " </relation>"\
                + " [GEN] " + raw_data["tail"]
        label = raw_data["tail"]
        if raw_data["split"] == "train":
            train_data.append({"text": text})
        elif raw_data["split"] == "val":
            val_data.append({"text": text})
    else:
        text = "<head> " + raw_data["head"] + " </head> "\
                + "<relation> " + raw_data["relation"] + " </relation>"\
                + " [GEN] "
        label = raw_data["tail"]
        test_data.append({"text": text, "label":raw_data["tail"]})

with open("atomic_train.jsonl", "w") as f:
    for data in train_data:
        json.dump(train_data, f)
        f.write('\n')

with open("atomic_val.jsonl", "w") as f:
    for data in val_data:
        json.dump(val_data, f)
        f.write('\n')

with open("atomic_test.jsonl", "w") as f:
    for data in test_data:
        json.dump(test_data, f)
        f.write('\n')