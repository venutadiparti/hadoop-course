--LeftOuterJoin.pig
posts = load '/training/data/user-posts.txt' using PigStorage(',') as (user:chararray,post:chararray,date:long);
likes = load '/training/data/user-likes.txt' using PigStorage(',') as (user:chararray,likes:int,date:long);
userInfo = join posts by user left outer, likes by user;
dump userInfo;