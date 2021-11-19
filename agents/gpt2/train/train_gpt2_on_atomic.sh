#!/bin/bash

#--model_name_or_path gpt2-medium \
export DATAIN=atomic_train.json
export MODELNAME=gpt2-large
export TOKENIZERPATH=/home/ruoyao/comet-distill-tokenizer

deepspeed --num_gpus=4 run_clm.py \
        --deepspeed ds_config_test.json \
        --model_name_or_path ${MODELNAME} \
        --train_file ${DATAIN} \
	--tokenizer_name ${TOKENIZERPATH}\
        --do_train true \
        --do_eval true \
        --do_predict true \
        --dataset_config_name wikitext-2-raw-v1 \
        --per_device_train_batch_size 1 \
        --per_device_eval_batch_size 1 \
        --output_dir /home/ruoyao/output_dump/test-clm \
        --overwrite_output_dir 

#        --fp16 \