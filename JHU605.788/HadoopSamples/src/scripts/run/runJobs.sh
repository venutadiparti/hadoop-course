#!/bin/sh
#/bin/bash

totalJobs=0
numFailed=0

startTime=`date '+%Y%m%d%H%M%S'`
logBase="/home/hadoop/Training/scripts/"
log=$logBase"log-allJobs.txt"
errorLog=$logBase"log-failedCommands.txt"

resources="../../main/resources"
samplesJar=$PLAY_AREA/HadoopSamples.jar
repo="/home/hadoop/.m2/repository"
avroMapRedJar="$repo/org/apache/avro/avro-mapred/1.7.4/avro-mapred-1.7.4-hadoop2.jar"

echo "Logs: [$log]"
echo "Errors: [$errorLog]"

function log(){
   echo "$1" >> /home/hadoop/Training/scripts/log-allJobs.txt
}
function logFailure(){
   echo "$1" >> /home/hadoop/Training/scripts/log-failedCommands.txt
   log "$1"
}

function execCommand(){
   execCommandExpectReturnCode "$1" 0
}

function execCommandExpectReturnCode(){
   log "------------------------------------"
   log "Executing: [$1]"
   eval $1
   result=$?
   if [ $result -ne $2 ]; then
       logFailure "  Failed to execute [$1], return code [$result] but expected [$2]"
       numFailed=`expr $numFailed + 1`
   fi
   log "Executed: [$1] return-code=[$result]"
   log "------------------------------------"
   totalJobs=`expr $totalJobs + 1`
}

function execStep(){
   hdfs dfs -rm -r "$2"
   cd "$3"
   execCommand "$1"
   hdfs dfs -rm -r "$2"
}

function printStats(){
   currTime=`date '+%Y%m%d%H%M%S'`
   runTime=`expr $currTime - $startTime`
   printMsg="Ran [$totalJobs] Hadoop jobs with [$numFailed] failures in [$runTime] seconds"
   log "$printMsg"
   echo "##########################################################"
   echo "#  $printMsg"
   echo "#  Logs: [$log]"
   echo "#  Errors: [$errorLog]"
   echo "##########################################################"
}

##############################
# Compression
hdfs dfs -rm /training/data/jdkSrc/jdkAppendedSrc.txt
wget -O jdkAppendedSrc.tar.gz "http://goo.gl/l1JXZP"
tar xvf jdkAppendedSrc.tar.gz
hdfs dfs -mkdir /training/data/jdkSrc/
hdfs dfs -put jdkAppendedSrc.txt /training/data/jdkSrc/
rm jdkAppendedSrc.tar.gz jdkAppendedSrc.txt
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar compression.CompareCompression /training/data/jdkSrc/jdkAppendedSrc.txt"

##############################
# File Based Data Structures
wget -O reviews-xml.tar.gz "http://goo.gl/zKhPTw"
tar xvf reviews-xml.tar.gz
hdfs dfs -mkdir /training/data/reviews-xml
hdfs dfs -put reviews-xml/* /training/data/reviews-xml/
rm reviews-xml.tar.gz*
rm reviews-xml -rf

reivewsInputXml="$resources/mr/reviews/fsstruct/ReviewsJob-xmlInput.xml"

# must comment out as it generates lots of splits (2,999) and runs for over an hour
#execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.SimpleTextXmlJob -conf $resources/mr/reviews/fsstruct/ReviewsJob-xmlInput.xml"

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.SimpleTextXmlJob_CombineFileInputFormat -conf $reivewsInputXml"

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.seq.ReviewSequenceFileWriter -conf $resources/mr/reviews/fsstruct/seq/SeqCreation.xml"
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.seq.SequenceFileReader /training/data/reviews-seq/reviews.seqfile 2"

execCommand "HADOOP_CLASSPATH=$PLAY_AREA/HadoopSamples.jar \
yarn jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples*.jar sort \
  -libjars $PLAY_AREA/HadoopSamples.jar \
  -r 1 \
  -inFormat org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat \
  -outFormat org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat \
  -outKey org.apache.hadoop.io.LongWritable \
  -outValue mr.reviews.model.ReviewWritable \
  /training/data/reviews-seq/reviews.seqfile /training/out/seq-to-map/"
execCommand "hdfs dfs -mv /training/out/seq-to-map/part-r-00000 /training/out/seq-to-map/data"
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.mf.MapFileFix /training/out/seq-to-map/"
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.mf.MfReader /training/out/seq-to-map/"

hdfs dfs -rm -r /training/data/reviews-seq/


##############################
# Avro
export HADOOP_CLASSPATH=$avroMapRedJar

avroCreationConf="$resources/mr/reviews/fsstruct/avro/AvroCreation.xml"
execCommand "yarn jar $samplesJar mr.reviews.fsstruct.avro.AvroWriterSpecific -conf $avroCreationConf"

avroMapRedConf="$resources/mr/reviews/fsstruct/avro/AvroMapRed.xml"
execCommand "yarn jar $samplesJar mr.reviews.fsstruct.avro.ReviewAvroJob -libjars $avroMapRedJar -conf $avroMapRedConf"



