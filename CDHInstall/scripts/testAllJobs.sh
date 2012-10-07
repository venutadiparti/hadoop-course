#/bin/bash
function log(){
   echo "$1" >> /home/hadoop/Training/scripts/log-allJobs.txt
}
function logFailure(){
   echo "$1" >> /home/hadoop/Training/scripts/log-failedCommands.txt
   log "$1"
}

function execCommand(){
   log "------------------------------------"
   log "Executing: [$1]"
   eval $1
   if [ $? != 0 ]; then
       logFailure "  Failed to execute [$1] "
   fi
   log "Executed: [$1] return-code=[$?]"
   log "------------------------------------"
}

function execStep(){
   hdfs dfs -rm -r "$2"
   cd "$3"
   execCommand "$1"
   hdfs dfs -rm -r "$2"
}

log "############################################
# Solutions
############################################"

# Solution for InputOutput Exercises
execStep "yarn jar $PLAY_AREA/Solutions.jar mapRed.inputAndOutput.CountTokens \
/training/data/hamlet.txt /training/playArea/InputOutput/CountTokens" "/training/playArea/InputOutput" "$PLAY_AREA/"

# Solution for InputOutput Exercises
execCommand "yarn jar $PLAY_AREA/Solutions.jar mapRed.inputAndOutput.UniqueCounterTool"
echo "truncate 'Exercise_InputAndOutput_Result'" | hbase shell

# Solution for features Exercises - counters
execCommand "yarn jar $PLAY_AREA/Solutions.jar mapRed.features.UniqueCounterTool"
echo "truncate 'Exercise_InputAndOutput_Result'" | hbase shell

# Solution for features Exercises - Distributed Cache
hdfs dfs -rm -r /training/playArea/LineSampler
execStep "yarn jar $PLAY_AREA/Solutions.jar mapRed.features.LineSamplerTool \
-files $PLAY_AREA/exercises/mapRed/tokensToRetain.txt \
/training/data/hamlet.txt /training/playArea/LineSampler" "/training/playArea/LineSampler" "$PLAY_AREA/"

# Jobs on YARN Exercises
execStep "yarn jar $PLAY_AREA/Exercises.jar mapRed.jobOnYARN.JobWithFailures \
/training/data/hamlet.txt /training/playArea/JobsOnYarn" "/training/playArea/JobsOnYarn" "$PLAY_AREA/"

# Streaming Job 1
execCommand "cat $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/inputTest.txt | \
$HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueMapper.py | \
sort | $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueReducer.py"

execStep "yarn jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
-D mapred.job.name='Count Job via Streaming' \
-files $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueMapper.py,\
$HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueReducer.py \
-input /training/data/war_and_peace.txt \
-output /training/playArea/streaming/CountUnique \
-mapper CountUniqueMapper.py \
-combiner CountUniqueReducer.py \
-reducer CountUniqueReducer.py" "/training/playArea/streaming/CountUnique" "$PLAY_AREA/"

# Streaming Job 2
execCommand "cat $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/inputTest.txt | \
$HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/LengthDividerMapper.py | \
sort | $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueReducer.py"
execStep "yarn jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
-D mapred.job.name="Count Job via Streaming" \
-files $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/LengthDividerMapper.py,\
$HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueReducer.py \
-input /training/data/war_and_peace.txt \
-output /training/playArea/streaming/LengthDivider \
-mapper LengthDividerMapper.py \
-combiner CountUniqueReducer.py \
-reducer CountUniqueReducer.py" "/training/playArea/streaming/LengthDivider" "$PLAY_AREA/"

# Worflow Exercise Solution - JobControl
execStep "yarn jar $PLAY_AREA/Solutions.jar mapRed.workflows.JobControlWorkflow \
/training/data/hamlet.txt /training/playArea/JobControlWorkflow" "/training/playArea/JobControlWorkflow" "$PLAY_AREA/"

# Oozie Workflow
execStep "/home/hadoop/Training/scripts/runOozieWorkflow.sh $PLAY_AREA/oozie/oozie-exercise-workflow" "/training/playArea/JobControlWorkflow" "$PLAY_AREA/"

# pig first lecture solution
execStep "pig MostOccuredTokens.pig" "/training/playArea/pig/mostOccuredTokens" "$PLAY_AREA/pig/scripts-solutions"

# pig second lectuture solution
execStep "pig BookJoin.pig" "/training/exercises/pig/bookPurchases" "$PLAY_AREA/pig/scripts-solutions"
execStep "pig BookCogroup.pig" "/training/exercises/pig/bookPurchases" "$PLAY_AREA/pig/scripts-solutions"

