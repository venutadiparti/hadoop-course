package mr.blogs;

import java.io.IOException;

import mr.blogs.model.BlogKeyWritable;
import mr.blogs.model.BlogReport;
import mr.blogs.model.BlogWritable;

import org.apache.hadoop.mapreduce.Reducer;

public class BlogReducer extends Reducer<BlogKeyWritable, BlogWritable, BlogKeyWritable, BlogReport> {
    @Override
    protected void reduce(BlogKeyWritable key, Iterable<BlogWritable> blogs, Context context) throws IOException,
            InterruptedException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Blogs that mention [" + key.getKeyword() + "] by [" + key.getAuthor() + "]\n");
        int numOfBlogs = 0;
        for (BlogWritable blog : blogs){
            strBuilder.append("   " + blog.getPostedTimestamp() + ":" + blog.getContent() + "\n");
            numOfBlogs++;
        }
        BlogReport report = new BlogReport(numOfBlogs, key.getKeyword(), key.getAuthor(), strBuilder.toString());
        context.write(key, report);
    }
}
