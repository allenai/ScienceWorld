#!/bin/bash
export DATAIN=../../../python-api/saveout.json
#export DATAIN=../../../python-api/test.json
export MODEL_DIR=/tmp/test-t5
#export MODEL_DIR=t5-base
export OUTPUT_DIR=/tmp/out

python run_summarization.py \
    --model_name_or_path ${MODEL_DIR} \
    --do_eval \
    --do_predict true \
    --output_dir ${OUTPUT_DIR} \
    --validation_file ${DATAIN} \
    --test_file ${DATAIN} \
    --source_prefix "agent: " \
    --per_device_train_batch_size=1 \
    --per_device_eval_batch_size=1 \
    --predict_with_generate \
    

    
