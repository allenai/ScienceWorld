import os
import pandas as pd
import json

data_dir = "/home/ruoyao/Documents/datasets/WorldtreeExplanationCorpusV2.1_Feb2020/tablestore/v2.1/tables"
data = []

for filename in os.listdir(data_dir):
    with open(os.path.join(data_dir, filename)) as f:
        df = pd.read_csv(f, sep='\t')
        for n, row in df.iterrows():
            word_list = []
            for column_name in df.columns:
                if column_name.startswith('[SKIP]') or column_name.startswith('Unnamed'):
                    continue
                elif pd.isna(row[column_name]):
                    continue
                else:
                    word_list.append(str(row[column_name]))
            sentence = ' '.join(word_list)
            data.append({'text':sentence})

with open("worldtree_kb.json", "w") as f:
    for d in data:
        json.dump(d, f)
        f.write('\n')