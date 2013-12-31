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


# File Based Data Structures

reivewsInputXml="$resources/mr/reviews/fsstruct/ReviewsJob-xmlInput.xml"

# must comment out as it generates lots of splits (2,999) and runs for over an hour
#execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.SimpleTextXmlJob -conf $resources/mr/reviews/fsstruct/ReviewsJob-xmlInput.xml"

#execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.SimpleTextXmlJob_CombineFileInputFormat -conf $reivewsInputXml"

##############################
# Avro
export HADOOP_CLASSPATH=$avroMapRedJar

avroCreationConf="$resources/mr/reviews/fsstruct/avro/AvroCreation.xml"
execCommand "yarn jar $samplesJar mr.reviews.fsstruct.avro.AvroWriterSpecific -conf $avroCreationConf"

avroMapRedConf="$resources/mr/reviews/fsstruct/avro/AvroMapRed.xml"
execCommand "yarn jar $samplesJar mr.reviews.fsstruct.avro.ReviewAvroJob -libjars $avroMapRedJar -conf $avroMapRedConf"



