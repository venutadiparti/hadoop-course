package oozie.workflows;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AverageByLetterMapper extends Mapper<Text, IntWritable, Text, IntWritable> {
	private final Text text = new Text();
	@Override	
	protected void map(Text key, IntWritable value, Context context)
			throws IOException, InterruptedException {
		text.set(key.toString().substring(0,1));
		context.write(text, value);
	}
}