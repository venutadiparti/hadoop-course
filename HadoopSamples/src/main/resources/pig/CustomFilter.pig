--CustomFilter.pig
REGISTER HadoopSamples.jar
DEFINE isShort pig.IsShort();
posts = LOAD '/training/data/user-posts.txt' USING PigStorage(',') AS (user:chararray,post:chararray,date:long);
filtered = FILTER posts BY isShort(posts.post);
dump filtered;