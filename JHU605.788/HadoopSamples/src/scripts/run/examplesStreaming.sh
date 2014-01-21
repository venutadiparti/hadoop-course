#!/bin/bash

source ./jobExecSupport.sh
source ./sharedProps.sh


hdfs dfs -rm -r /training/out/streaming/
execCommand "yarn jar /usr/lib/hadoop-mapreduce/hadoop-streaming.jar -D mapred.job.name='Count Job via Streaming' \
    -files $resources/streaming/countMap.py,$resources/streaming/countReduce.py -input examples_input/books/hamlet.txt \
    -output /training/out/streaming/ -mapper countMap.py -combiner countReduce.py -reducer countReduce.py"
hdfs dfs -rm -r /training/out/streaming/

execCommand "yarn jar /usr/lib/hadoop-mapreduce/hadoop-streaming.jar -D mapred.job.name='Count Job via Streaming' \
    -files $resources/streaming/countMap_withReporting.py,$resources/streaming/countReduce.py -input examples_input/books/hamlet.txt \
    -output /training/out/streaming/ -mapper countMap_withReporting.py -combiner countReduce.py -reducer countReduce.py"
hdfs dfs -rm -r /training/out/streaming/

printStats