package mr.blogs;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import mr.blogs.model.BlogKeyWritable;
import mr.blogs.model.BlogReport;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlogOutputFormat extends FileOutputFormat<BlogKeyWritable, BlogReport>{
    private static Logger LOG = LoggerFactory.getLogger(BlogOutputFormat.class);
    @Override
    public RecordWriter<BlogKeyWritable, BlogReport> getRecordWriter(TaskAttemptContext job) throws IOException,
            InterruptedException {
        Path outPath = getOutputPath(job);
        return new BlogRecordWriter(job.getConfiguration(), outPath);
    }
    
    class BlogRecordWriter extends RecordWriter<BlogKeyWritable, BlogReport>{
        private Path mrRootPath;
        private FileSystem fs;
       
        private Configuration conf;
        
        public BlogRecordWriter(Configuration conf, Path mrRootPath) throws IOException{
            this.mrRootPath = mrRootPath;
            this.conf = conf;
            this.fs = FileSystem.get(conf);
        }
        
        @Override
        public void write(BlogKeyWritable key, BlogReport blog) throws IOException, InterruptedException {
            Path filePath = new Path(mrRootPath, key.getAuthor() + "_" + key.getKeyword() + "-" + blog.getNumBlogs() + ".txt");
            LOG.info("Opening new file to [{}]", filePath);
            FSDataOutputStream out = fs.create(filePath);
            try {
                InputStream in = new BufferedInputStream(
                      new ByteArrayInputStream(blog.getFullReport().getBytes()));
                IOUtils.copyBytes(in, out, conf);
                IOUtils.closeStream(in);
            } finally {
                IOUtils.closeStream(out);
            }
        }

        @Override
        public void close(TaskAttemptContext context) throws IOException, InterruptedException {
            fs.close();
        }
    }

}
