package mr.joins;

import java.io.IOException;

import mr.joins.support.TextPair;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ReduceSideJoinWithSecondarySortUsersPostsMapper extends Mapper<LongWritable, Text, TextPair, TextPair> {
    private final Text indicator = new Text("L");
    private final Text joinKey = new Text();

    private final TextPair joinKeyPair = new TextPair();
    private final TextPair valuePair = new TextPair();
    {
        joinKeyPair.setSecond(indicator);
        valuePair.setSecond(indicator);
    }
    
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String [] split = value.toString().split(",");
        joinKey.set(split[0]);
        
        joinKeyPair.setFirst(joinKey);
        valuePair.setFirst(value);
        context.write(joinKeyPair, valuePair);
    }
}