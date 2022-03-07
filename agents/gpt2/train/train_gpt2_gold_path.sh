#!/bin/bash

export TRAIN=gpt2_data/sciworld_formatted_train.json
export VAL=gpt2_data/sciworld_formatted_test.json
export MODELNAME=gpt2

deepspeed --num_gpus=4 run_clm.py \
        --deepspeed ds_config_test.json \
        --model_name_or_path ${MODELNAME} \
        --train_file ${TRAIN} \
        --validation_file ${VAL} \
        --do_train true \
        --do_eval true \
        --do_predict true \
        --dataset_config_name wikitext-2-raw-v1 \
        --per_device_train_batch_size 8 \
        --per_device_eval_batch_size 8 \
        --output_dir /media/scratch/scienceworld-logs/calm/gpt2_goldpath \
        --overwrite_output_dir\
        --num_train_epochs 1\