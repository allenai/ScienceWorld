import json
import os

record_dir = "/home/ruoyao/Documents/projects/virtualenv-scala2/python-api/recordings"

output = []

for filename in os.listdir(record_dir):
    with open(os.path.join(record_dir, filename)) as f:
        data = json.load(f)
    
    for i in range(len(data)):
        if i == 0:
            output_text = f"[CLS] {data[i]['taslDescription']} [SEP] [SEP] {data[i]['observation']} [SEP] {data[i]['userInput']} [SEP]"
        else:
            output_text = f"[CLS] {data[i-1]['observation']} [SEP] {data[i-1]['userInput']} [SEP] {data[i]['observation']} [SEP] {data[i]['userInput']} [SEP]"

with open("record_data.json", "w") as f:
    for data in output:
        json.dump(data, f)
        f.write('\n')