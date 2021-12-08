#!/bin/bash

#--model_name_or_path gpt2-medium \
export TRAIN=worldtree_train.json
export VAL=worldtree_dev.json
export MODELNAME=gpt2-large
export TOKENIZERPATH=/home/ruoyao/world_tree_tokenizer

deepspeed --num_gpus=4 run_clm.py \
        --deepspeed ds_config_test.json \
        --model_name_or_path ${MODELNAME} \
        --train_file ${TRAIN} \
        --validation_file ${VAL} \
	--tokenizer_name ${TOKENIZERPATH}\
        --do_train true \
        --do_eval true \
        --do_predict true \
        --dataset_config_name wikitext-2-raw-v1 \
        --per_device_train_batch_size 16 \
        --per_device_eval_batch_size 16 \
        --output_dir /home/ruoyao/output_dump/gpt2-large-world-tree \
        --overwrite_output_dir\
        --num_train_epochs 1\


#        --fp16 \