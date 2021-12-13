#!/bin/bash

#--model_name_or_path gpt2-medium \
export TRAIN=worldtree_kb.json
export MODELNAME=gpt2

deepspeed --num_gpus=4 run_clm.py \
        --deepspeed ds_config_test.json \
        --model_name_or_path ${MODELNAME} \
        --train_file ${TRAIN} \
	    --tokenizer_name gpt2\
        --do_train true \
        --do_eval true \
        --do_predict true \
        --dataset_config_name wikitext-2-raw-v1 \
        --per_device_train_batch_size 16 \
        --per_device_eval_batch_size 16 \
        --output_dir /home/ruoyao/output_dump/gpt2-world-tree-kb \
        --overwrite_output_dir\
        --num_train_epochs 3\


#        --fp16 \