package mr.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Test;

public class StartsWithCountMapperTest {

    @Test
    public void processLineOfText() {
        LongWritable inputKey = new LongWritable();
        Text inputValue = new Text("Must process this line of text");
        new MapDriver<LongWritable, Text, Text, IntWritable>()
            .withMapper(new StartsWithCountMapper())
            .withInput(inputKey, inputValue)
            .withOutput(new Text("M"), new IntWritable(1))
            .withOutput(new Text("p"), new IntWritable(1))
            .withOutput(new Text("t"), new IntWritable(1))
            .withOutput(new Text("l"), new IntWritable(1))
            .withOutput(new Text("o"), new IntWritable(1))
            .withOutput(new Text("t"), new IntWritable(1))
            .runTest();
    }
}
