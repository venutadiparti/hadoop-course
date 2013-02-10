package mr.blogs;

import java.io.IOException;

import mr.blogs.model.BlogWritable;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

public class BlogInputFormat extends FileInputFormat<LongWritable, BlogWritable> {
    @Override
    public RecordReader<LongWritable, BlogWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        BlogsWritablesRecordReader reader = new BlogsWritablesRecordReader();
        reader.initialize(split, context);
        return reader;
    }
    static class BlogsWritablesRecordReader extends RecordReader<LongWritable, BlogWritable>{
        private final LineRecordReader lineReader = new LineRecordReader();
        private final BlogWritable blog = new BlogWritable();
        @Override
        public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
            lineReader.initialize(split, context);
        }

        @Override
        public boolean nextKeyValue() throws IOException, InterruptedException {
            boolean hasNextKey = lineReader.nextKeyValue();
            if (hasNextKey){
                parse(lineReader.getCurrentValue());
            }
            return hasNextKey;
        }
        
        private void parse(Text value) {
            // expecting input "<username>,<comment>,<postedTimestamp>"
            String[] split = value.toString().split(",");
            blog.setAuthor(split[0]);
            blog.setContent(split[1]);
            blog.setPostedTimestamp(Long.valueOf(split[2]));
        }

        @Override
        public LongWritable getCurrentKey() throws IOException, InterruptedException {
            return lineReader.getCurrentKey();
        }

        @Override
        public BlogWritable getCurrentValue() throws IOException, InterruptedException {
            return blog;
        }

        @Override
        public float getProgress() throws IOException, InterruptedException {
            return lineReader.getProgress();
        }

        @Override
        public void close() throws IOException {
            lineReader.close();
        }
    }
}
