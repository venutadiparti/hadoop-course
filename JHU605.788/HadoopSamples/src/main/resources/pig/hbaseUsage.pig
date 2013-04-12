reviews = LOAD 'ReviewTable' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage(
		'reviews:user reviews:timestamp reviews:text')
		AS (user:chararray, timestamp:long, text:chararray);
found = FILTER reviews BY text matches '.*affordable.*';
STORE found INTO 'ReviewReportTable' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage(
		'reviewKeywordHits:user reviewKeywordHits:timestamp reviewKeywordHits:text');