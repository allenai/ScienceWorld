#!/usr/bin/env bash

OUTPUT=$(./simulator/package.sh | tail -n 4) # could be 2, but just in case

if [[ $OUTPUT = *'[success]'* ]]; then
    echo 'Compiled Successfully!'
    exit 0
else
    echo 'Compile failed!'
    exit 1
fi
