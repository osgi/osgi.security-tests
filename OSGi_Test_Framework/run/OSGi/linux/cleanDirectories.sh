#!/bin/bash

# Delete of directories created during bundle's execution 

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$DIR/../../.."

cd $ROOT

TMP_DIR="tmp"

if [ -d $TMP_DIR ]; then
  rm -r $TMP_DIR
fi
