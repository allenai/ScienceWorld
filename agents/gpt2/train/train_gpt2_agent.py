#!/bin/bash
export DATAIN=../../../python-api/saveout.txt
export OUTPUT_DIR=/tmp/test-clm


python run_clm.py \
    --model_name_or_path gpt2-medium \
    --train_file ${DATAIN} \
    --validation_file ${DATAIN} \
    --do_train \
    --do_eval \
    --output_dir ${OUTPUT_DIR} \
	--num_train_epochs 10 \
	--per_device_train_batch_size 1 \
	--per_device_eval_batch_size 1 \
	--overwrite_output_dir


