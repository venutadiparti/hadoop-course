#!/bin/bash

source ./jobExecSupport.sh
source ./sharedProps.sh

export HADOOP_CLASSPATH=$avroMapRedJar
avroCreationConf="$resources/mr/reviews/fsstruct/avro/AvroCreation.xml"
execCommand "yarn jar $samplesJar mr.reviews.fsstruct.avro.AvroWriterSpecific -conf $avroCreationConf"

avroMapRedConf="$resources/mr/reviews/fsstruct/avro/AvroMapRed.xml"
execCommand "yarn jar $samplesJar mr.reviews.fsstruct.avro.ReviewAvroJob -libjars $avroMapRedJar -conf $avroMapRedConf"

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.avro.AvroSorter -libjars $avroMapRedJar examples_input/reviews-avro/reviews.avro \
    examples_input/reviews-avro-sorted $resources/mr/reviews/fsstruct/avro/readModel/review_sort.avsc"
hdfs dfs -rm -r examples_input/reviews-avro-sorted

printStats