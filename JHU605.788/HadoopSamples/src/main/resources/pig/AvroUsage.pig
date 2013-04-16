--AvroUsage.pig
REGISTER /home/hadoop/Training/play_area/avroLibs/*.jar
reviews = LOAD '/training/data/reviews-avro/reviews.avro' USING org.apache.pig.piggybank.storage.avro.AvroStorage();
found = FILTER reviews BY text matches '.*affordable.*';
STORE found INTO '/training/playArea/pigAvro' USING org.apache.pig.piggybank.storage.avro.AvroStorage();