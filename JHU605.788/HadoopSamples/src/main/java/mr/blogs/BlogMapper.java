package mr.blogs;

import static mr.blogs.BlogJob.PROP_FIND_VALUE;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import mr.blogs.model.BlogKeyWritable;
import mr.blogs.model.BlogWritable;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class BlogMapper extends Mapper<LongWritable, BlogWritable, BlogKeyWritable, BlogWritable> {

    private BlogKeyWritable keyWritable = new BlogKeyWritable();
    private List<String> valuesToLookFor;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        String value = context.getConfiguration().get(PROP_FIND_VALUE);
        Validate.notEmpty(value, "You must provide value to find via [" + PROP_FIND_VALUE + "]");
        valuesToLookFor = Arrays.asList(value.split(","));
    }

    @Override
    protected void map(LongWritable lineNum, BlogWritable incomingBlog, Context context) throws IOException, InterruptedException {
        for (String valueToLookFor : valuesToLookFor) {
            if (isValueIn(valueToLookFor, incomingBlog.getContent())) {
                keyWritable.setKeyword(valueToLookFor);
                keyWritable.setAuthor(incomingBlog.getAuthor());
                context.write(keyWritable, incomingBlog);
            }
        }
    }

    private boolean isValueIn(String valueToLookFor, String content) {
        // this logic can be as intricate as it needs to be for the bussiness logic
        return content.contains(valueToLookFor);
    }

   
}
