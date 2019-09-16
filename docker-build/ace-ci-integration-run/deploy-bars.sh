#!/bin/bash

if [ -z "$1" ]
then
    echo "No directory name supplied"
    exit 1
fi

for filename in $1/*.bar; do
    [ -e "$filename" ] || continue
    echo "Deploying $filename"
    mqsibar -c -a $filename -w /home/aceuser/ace-server
done
