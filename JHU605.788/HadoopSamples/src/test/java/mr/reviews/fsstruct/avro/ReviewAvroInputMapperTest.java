package mr.reviews.fsstruct.avro;

import java.io.IOException;
import java.util.Arrays;

import mr.reviews.ReviewJob;
import mr.reviews.fsstruct.avro.ReviewAvroMapper;
import mr.reviews.fsstruct.avro.model.ReviewAvro;
import mr.reviews.fsstruct.avro.model.ReviewKeyAvro;
import mr.reviews.fsstruct.avro.model.ReviewReportAvro;

import org.apache.avro.hadoop.io.AvroSerialization;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Test;

public class ReviewAvroInputMapperTest {

    private Configuration conf;
    
    @Before
    public void before(){
        conf = new Configuration();
        conf.setStrings("io.serializations", new String[] {
                AvroSerialization.class.getName()
            });
        AvroSerialization.setKeyWriterSchema(conf, ReviewKeyAvro.SCHEMA$);
        AvroSerialization.setValueWriterSchema(conf, ReviewReportAvro.SCHEMA$);
    }
    
    @Test
    public void testValueFoundInReview() throws IOException {
        ReviewAvro inputReview = ReviewAvro.newBuilder()
                .setUser("user1").setTimestamp(13330820823030l)
                .setText("This is where review would go").build();
        String keyword = "where";
        conf.set(ReviewJob.PROP_FIND_VALUE, keyword);
        
        ReviewKeyAvro foundKey = ReviewKeyAvro.newBuilder()
                .setUser(inputReview.getUser())
                .setKeyword(keyword).build();
        
        ReviewReportAvro foundValue = ReviewReportAvro.newBuilder()
                .setKeyword(keyword).setNumReviews(1)
                .setReviews(Arrays.asList(inputReview))
                .setUser(inputReview.getUser()).build();
        
        new MapDriver<AvroKey<ReviewAvro>, NullWritable, AvroKey<ReviewKeyAvro>, AvroValue<ReviewReportAvro>>()
            .withConfiguration(conf)
            .withMapper(new ReviewAvroMapper())
            .withInput(new AvroKey<ReviewAvro>(inputReview), NullWritable.get())
            .withOutput(new AvroKey<ReviewKeyAvro>(foundKey), new AvroValue<ReviewReportAvro>(foundValue))
            .runTest();
    }

    
    @Test
    public void testNoValueFound() throws IOException {
        ReviewAvro inputReview = ReviewAvro.newBuilder()
                .setUser("user1").setTimestamp(13330820823030l)
                .setText("This is where review would go").build();
        String keyword = "wontfind";
        conf.set(ReviewJob.PROP_FIND_VALUE, keyword);
        
        new MapDriver<AvroKey<ReviewAvro>, NullWritable, AvroKey<ReviewKeyAvro>, AvroValue<ReviewReportAvro>>()
            .withConfiguration(conf)
            .withMapper(new ReviewAvroMapper())
            .withInput(new AvroKey<ReviewAvro>(inputReview), NullWritable.get())
            .runTest();
    }
}
