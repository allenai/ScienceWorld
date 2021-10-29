#!/bin/bash
export DATAIN=../../../python-api/saveout.json
#export DATAIN=../../../python-api/test.json
export OUTPUT_DIR=/tmp/test-t5


python run_summarization.py \
    --model_name_or_path t5-base \
    --do_train \
    --do_eval \
    --num_train_epochs 5 \
    --train_file ${DATAIN} \
    --validation_file ${DATAIN} \
    --source_prefix "agent: " \
    --text_column "source" \
    --summary_column "target" \
    --output_dir ${OUTPUT_DIR} \
    --overwrite_output_dir \
    --per_device_train_batch_size=1 \
    --per_device_eval_batch_size=1 \
    --predict_with_generate
