
# Solution for InputOutput Exercises
hdfs dfs -rm -r /training/playArea/InputOutput
yarn jar $PLAY_AREA/Solutions.jar mapRed.inputAndOutput.CountTokens /training/data/hamlet.txt /training/playArea/InputOutput/CountTokens
hdfs dfs -rm -r /training/playArea/InputOutput

# Solution for InputOutput Exercises
yarn jar $PLAY_AREA/Solutions.jar mapRed.inputAndOutput.UniqueCounterTool
echo "truncate 'Exercise_InputAndOutput_Result'" | hbase shell

# Solution for features Exercises - counters
yarn jar $PLAY_AREA/Solutions.jar mapRed.features.UniqueCounterTool
echo "truncate 'Exercise_InputAndOutput_Result'" | hbase shell

# Solution for features Exercises - Distributed Cache
hdfs dfs -rm -r /training/playArea/LineSampler
yarn jar $PLAY_AREA/Solutions.jar mapRed.features.LineSamplerTool -files $PLAY_AREA/exercises/mapRed/tokensToRetain.txt /training/data/hamlet.txt /training/playArea/LineSampler
hdfs dfs -rm -r /training/playArea/LineSampler

# Jobs on YARN Exercises
hdfs dfs -rm -r /training/playArea/JobsOnYarn
yarn jar $PLAY_AREA/Exercises.jar mapRed.jobOnYARN.JobWithFailures /training/data/hamlet.txt /training/playArea/JobsOnYarn
hdfs dfs -rm -r /training/playArea/JobsOnYarn

# Streaming Job 1
cat $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/inputTest.txt | \
	$HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueMapper.py | \
	sort | $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueReducer.py
hdfs dfs -rm -r /training/playArea/streaming/CountUnique
yarn jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
	-D mapred.job.name="Count Job via Streaming" \
	-files $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueMapper.py,\
	$HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueReducer.py \
	-input /training/data/war_and_peace.txt \
	-output /training/playArea/streaming/CountUnique \
	-mapper CountUniqueMapper.py \
	-combiner CountUniqueReducer.py \
	-reducer CountUniqueReducer.py
hdfs dfs -rm -r /training/playArea/streaming/CountUnique

# Streaming Job 2
cat $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/inputTest.txt | \
	$HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/LengthDividerMapper.py | \
	sort | $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueReducer.py
hdfs dfs -rm -r /training/playArea/streaming/LengthDivider
yarn jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
	-D mapred.job.name="Count Job via Streaming" \
	-files $HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/LengthDividerMapper.py,\
	$HADOOP_SOLUTIONS_SRC/resources/mapRed/streaming/CountUniqueReducer.py \
	-input /training/data/war_and_peace.txt \
	-output /training/playArea/streaming/LengthDivider \
	-mapper LengthDividerMapper.py \
	-combiner CountUniqueReducer.py \
	-reducer CountUniqueReducer.py
hdfs dfs -rm -r /training/playArea/streaming/LengthDivider

# Worflow Exercise Solution - JobControl
hdfs dfs -rm -r /training/playArea/JobControlWorkflow
yarn jar $PLAY_AREA/Solutions.jar mapRed.workflows.JobControlWorkflow \
	/training/data/hamlet.txt /training/playArea/JobControlWorkflow
hdfs dfs -rm -r /training/playArea/JobControlWorkflow



