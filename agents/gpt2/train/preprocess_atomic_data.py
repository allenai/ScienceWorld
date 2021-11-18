import json

data_file = "/data/ai2-mosaic-public/ATOMIC10X.jsonl"

with open(data_file) as f:
    json_list = list(f)

train_data = {"texts":[]}
val_data = {"texts":[]}
test_data = {"texts":[]}

for data in json_list:
    raw_data = json.loads(data)
    if raw_data["p_valid_model"] < 0.8:
        continue
    text = "<head> " + raw_data["head"] + " </head> "\
            + "<relation> " + raw_data["relation"] + " </relation>"\
            + "[GEN]" + raw_data["tail"]
    label = raw_data["tail"]
    if raw_data["split"] == "train":
        train_data["texts"].append(text)
    elif raw_data["split"] == "val":
        val_data["texts"].append(text)
    else:
        test_data["texts"].append(text)

with open("atomic_train.json", "w") as f:
    json.dump(train_data, f)

with open("atomic_val.json", "w") as f:
    json.dump(val_data, f)

with open("atomic_test.json", "w") as f:
    json.dump(test_data, f)