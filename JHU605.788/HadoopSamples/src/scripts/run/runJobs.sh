#!/bin/sh
#/bin/bash

totalJobs=0
numFailed=0

startTime=`date '+%Y%m%d%H%M%S'`
logBase="/home/hadoop/Training/play_area/"
log=$logBase"log-allJobs.txt"
errorLog=$logBase"log-failedCommands.txt"

resources="/home/hadoop/Training/intellij/workspace/HadoopSamples/src/main/resources"
samplesJar=$PLAY_AREA/HadoopSamples.jar
repo="/home/hadoop/.m2/repository"
avroMapRedJar="$repo/org/apache/avro/avro-mapred/1.7.4/avro-mapred-1.7.4-hadoop2.jar"

echo "Logs: [$log]"
echo "Errors: [$errorLog]"

function log(){
   echo "$1" >> $log
}
function logFailure(){
   echo "$1" >> $errorLog
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

#############################
# HDFS
execCommand "java -cp $PLAY_AREA/HadoopSamples.jar:/usr/lib/hadoop/*:/usr/lib/hadoop/lib/* hdfs.LoadConfigurations"
execCommand "java -cp $PLAY_AREA/HadoopSamples.jar:/usr/lib/hadoop/*:/usr/lib/hadoop/lib/* hdfs.SimpleLs"
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar hdfs.SimpleLs"

echo "Hello from readme.txt" > readMe.txt
hdfs dfs -mkdir -p /training/data/
hdfs dfs -put readMe.txt /training/data/
rm readMe.txt
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar hdfs.ReadFile"
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar hdfs.SeekReadFile"
hdfs dfs -rm /training/data/readMe.txt

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar hdfs.WriteToFile"
execCommand "hdfs dfs -rm /training/playArea/writeMe.txt"

echo "Hello from writeme.txt" > writeMe.txt
hdfs dfs -ls /training/playArea/
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar hdfs.CopyToHdfs ./writeMe.txt /training/playArea/"
hdfs dfs -ls /training/playArea/
rm writeMe.txt
execCommand "hdfs dfs -rm /training/playArea/writeMe.txt"

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar hdfs.LsWithPathFilter"

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar hdfs.SimpleGlobbing 'examples_input/glob/201*'"
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar hdfs.SimpleGlobbing 'examples_input/glob/20[01][17]/'"
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar hdfs.SimpleGlobbing 'examples_input/glob/201?/0[1-5]/*'"

##############################
# First MapReduce Job
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.wordcount.StartsWithCountJob examples_input/books/hamlet.txt /training/playArea/wordCount/"
hdfs dfs -rm -r /training/playArea/wordCount/

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.wordcount.StartsWithCountJob examples_input/books/*.txt /training/playArea/wordCount/"
hdfs dfs -rm -r /training/playArea/wordCount/

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.wordcount.StartsWithCountJob 'examples_input/books/{juliusCaesar,hamlet}.txt' /training/playArea/wordCount/"
hdfs dfs -rm -r /training/playArea/wordCount/

##############################
# MapReduce Features
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.wordcount.StartsWithCountJob_UserCounters examples_input/books/hamlet.txt /training/playArea/wordCount/"
hdfs dfs -rm -r /training/playArea/wordCount/

mkdir -p $PLAY_AREA/data
echo "b" >> $PLAY_AREA/data/startWithExcludeFile.txt
echo "c" >> $PLAY_AREA/data/startWithExcludeFile.txt
echo "d" >> $PLAY_AREA/data/startWithExcludeFile.txt
echo "e" >> $PLAY_AREA/data/startWithExcludeFile.txt
echo "f" >> $PLAY_AREA/data/startWithExcludeFile.txt
echo "G" >> $PLAY_AREA/data/startWithExcludeFile.txt

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.wordcount.StartsWithCountJob_DistCache -files $PLAY_AREA/data/startWithExcludeFile.txt examples_input/books/hamlet.txt /training/playArea/wordCount/"
rm $PLAY_AREA/data/startWithExcludeFile.txt
hdfs dfs -rm -r /training/playArea/wordCount/

##############################
# MapReduce Components
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.ReviewJob -conf $resources/mr/reviews/ReviewsJob-smallInput.xml"

##############################
# Streaming
STREAMING_SCRIPTS=/home/hadoop/Training/intellij/workspace/HadoopSamples/src/main/resources/streaming/

hdfs dfs -rm -r /training/out/streaming/
execCommand "yarn jar /usr/lib/hadoop-mapreduce/hadoop-streaming.jar -D mapred.job.name='Count Job via Streaming' -files $STREAMING_SCRIPTS/countMap.py,$STREAMING_SCRIPTS/countReduce.py -input examples_input/books/hamlet.txt -output /training/out/streaming/ -mapper countMap.py -combiner countReduce.py -reducer countReduce.py"
hdfs dfs -rm -r /training/out/streaming/

execCommand "yarn jar /usr/lib/hadoop-mapreduce/hadoop-streaming.jar -D mapred.job.name='Count Job via Streaming' -files $STREAMING_SCRIPTS/countMap_withReporting.py,$STREAMING_SCRIPTS/countReduce.py -input examples_input/books/hamlet.txt -output /training/out/streaming/ -mapper countMap_withReporting.py -combiner countReduce.py -reducer countReduce.py"
hdfs dfs -rm -r /training/out/streaming/

##############################
# Compression
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar compression.CompareCompression examples_input/txt/jdkAppendedSrc.txt"
yarn jar ~/Training/play_area/HadoopSamples.jar mr.wordcount.StartsWithCountJob examples_input/txt/compressedTest/compressed.bz2 /training/playArea/wordCount/
hdfs dfs -rm -r /training/playArea/wordCount/

yarn jar ~/Training/play_area/HadoopSamples.jar mr.wordcount.StartsWithCountJob examples_input/txt/compressedTest/compressed.gz /training/playArea/wordCount/
hdfs dfs -rm -r /training/playArea/wordCount/

yarn jar ~/Training/play_area/HadoopSamples.jar mr.wordcount.StartsWithCountJob examples_input/txt/compressedTest/compressed.lz4 /training/playArea/wordCount/
hdfs dfs -rm -r /training/playArea/wordCount/

yarn jar ~/Training/play_area/HadoopSamples.jar mr.wordcount.StartsWithCountJob examples_input/txt/compressedTest/compressed.snappy /training/playArea/wordCount/
hdfs dfs -rm -r /training/playArea/wordCount/

##############################
# File Based Data Structures
# must comment out as it generates lots of splits (2,999) and runs for over an hour
#execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.SimpleTextXmlJob -conf $resources/mr/reviews/fsstruct/ReviewsJob-xmlInput.xml"

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.SimpleTextXmlJob_CombineFileInputFormat -conf $resources/mr/reviews/fsstruct/ReviewsJob-xmlInput.xml"

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.seq.ReviewSequenceFileWriter -conf $resources/mr/reviews/fsstruct/seq/SeqCreation.xml"
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.seq.SequenceFileReader examples_input/reviews-seq/reviews.seqfile 2"

execCommand "HADOOP_CLASSPATH=$PLAY_AREA/HadoopSamples.jar \
yarn jar /usr/lib/hadoop-mapreduce/hadoop-mapreduce-examples.jar sort \
  -libjars $PLAY_AREA/HadoopSamples.jar \
  -r 1 \
  -inFormat org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat \
  -outFormat org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat \
  -outKey org.apache.hadoop.io.LongWritable \
  -outValue mr.reviews.model.ReviewWritable \
  examples_input/reviews-seq/reviews.seqfile /training/out/seq-to-map/"
execCommand "hdfs dfs -mv /training/out/seq-to-map/part-r-00000 /training/out/seq-to-map/data"
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.mf.MapFileFix /training/out/seq-to-map/"
execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.mf.MfReader /training/out/seq-to-map/"

hdfs dfs -rm -r examples_input/reviews-seq/
hdfs dfs -rm -r /training/out/seq-to-map/


##############################
# Avro
export HADOOP_CLASSPATH=$avroMapRedJar

avroCreationConf="$resources/mr/reviews/fsstruct/avro/AvroCreation.xml"
execCommand "yarn jar $samplesJar mr.reviews.fsstruct.avro.AvroWriterSpecific -conf $avroCreationConf"

avroMapRedConf="$resources/mr/reviews/fsstruct/avro/AvroMapRed.xml"
execCommand "yarn jar $samplesJar mr.reviews.fsstruct.avro.ReviewAvroJob -libjars $avroMapRedJar -conf $avroMapRedConf"

execCommand "yarn jar $PLAY_AREA/HadoopSamples.jar mr.reviews.fsstruct.avro.AvroSorter -libjars $avroMapRedJar examples_input/reviews-avro/reviews.avro \
    examples_input/reviews-avro-sorted $resources/mr/reviews/fsstruct/avro/readModel/review_sort.avsc"
hdfs dfs -rm -r examples_input/reviews-avro-sorted



