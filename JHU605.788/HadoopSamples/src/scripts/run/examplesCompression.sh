#!/bin/bash

source ./jobExecSupport.sh
source ./sharedProps.sh

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar compression.CompareCompression examples_input/txt/jdkAppendedSrc.txt"
yarn jar ~/Training/play_area/HadoopSamples.jar mr.wordcount.StartsWithCountJob examples_input/txt/compressedTest/compressed.bz2 /training/playArea/wordCount/
hdfs dfs -rm -r /training/playArea/wordCount/

yarn jar ~/Training/play_area/HadoopSamples.jar mr.wordcount.StartsWithCountJob examples_input/txt/compressedTest/compressed.gz /training/playArea/wordCount/
hdfs dfs -rm -r /training/playArea/wordCount/

yarn jar ~/Training/play_area/HadoopSamples.jar mr.wordcount.StartsWithCountJob examples_input/txt/compressedTest/compressed.lz4 /training/playArea/wordCount/
hdfs dfs -rm -r /training/playArea/wordCount/

yarn jar ~/Training/play_area/HadoopSamples.jar mr.wordcount.StartsWithCountJob examples_input/txt/compressedTest/compressed.snappy /training/playArea/wordCount/
hdfs dfs -rm -r /training/playArea/wordCount/

printStats