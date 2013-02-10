package mr.blogs;

import java.io.IOException;

import mr.blogs.model.BlogKeyWritable;
import mr.blogs.model.BlogReport;
import mr.blogs.model.BlogWritable;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlogJob extends Configured implements Tool {
    
    private static Logger LOG = LoggerFactory.getLogger(BlogJob.class); 
    public final static String PROP_FIND_VALUE  = "report.value";
    public final static String PROP_INPUT_PATH = "report.input.path";
    public final static String PROP_OUTPUT_PATH = "report.output.path";
    
	@Override
	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(getConf(), this.getClass().getName());
		job.setJarByClass(getClass());

		// configure output and input source
		String input = getConf().get(PROP_INPUT_PATH);
		Validate.notEmpty(input, "Must provide input path via [" + PROP_INPUT_PATH + "] property");
		BlogInputFormat.addInputPath(job, new Path(input));
		job.setInputFormatClass(BlogInputFormat.class);

		// configure mapper and reducer
		job.setMapperClass(BlogMapper.class);
//		job.setCombinerClass(CustomWritableReducer.class);
		job.setReducerClass(BlogReducer.class);
		
		// configure keys used between mappers and reducers
		job.setMapOutputKeyClass(BlogKeyWritable.class);
		job.setMapOutputValueClass(BlogWritable.class);
		
		Path outPath = getOutputPath();
	    BlogOutputFormat.setOutputPath(job, outPath);
	    job.setOutputFormatClass(BlogOutputFormat.class);
	    job.setOutputKeyClass(BlogKeyWritable.class);
	    job.setOutputValueClass(BlogReport.class);
		
		return job.waitForCompletion(true) ? 0 : 1;
	}

    private Path getOutputPath() throws IOException {
        String out = getConf().get(PROP_OUTPUT_PATH);
	    Validate.notEmpty(out, "You must provide value to find via [" + PROP_OUTPUT_PATH + "]");
	    Path outPath = new Path(out);
	    FileSystem fs = FileSystem.get(getConf());
	    if (fs.exists(outPath)){
	        fs.delete(outPath, true);
	        LOG.info("Removed output directory [" + outPath + "]");
	    }
        return outPath;
    }

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new BlogJob(), args);
		System.exit(exitCode);
	}
}
