package mr.reviews.fsstruct.hbase;

import org.apache.hadoop.hbase.util.Bytes;


/**
Statement to create schema:
  create 'ReviewTable', {NAME=>'reviews',COMPRESSION=>'snappy'}
 */
public interface ReviewHBaseSchema {
    public static String REVIEW_TABLE = "ReviewTable";

    public static byte[] REVIEW_FAMILY_CONTENT = Bytes.toBytes("reviews");
    public static byte[] REVIEW_COLUMN_USER = Bytes.toBytes("user");
    public static byte[] REVIEW_COLUMN_TEXT = Bytes.toBytes("text");
    public static byte[] REVIEW_COLUMN_TIMESTAMP = Bytes.toBytes("timestamp");
}
