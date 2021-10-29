#!/bin/bash
export DATAIN=saveout.txt
export MODEL_DIR=/tmp/test-clm
#export MODEL_DIR=gpt2-medium


python run_generation.py \
    --model_type=gpt2 \
    --model_name_or_path=${MODEL_DIR}
