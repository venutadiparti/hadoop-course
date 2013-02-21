package mr.reviews;

import java.io.IOException;

import mr.reviews.model.ReviewKeyWritable;
import mr.reviews.model.ReviewReport;
import mr.reviews.model.ReviewWritable;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviewSequenceFileJob extends Configured implements Tool {
    
    private static Logger LOG = LoggerFactory.getLogger(ReviewSequenceFileJob.class); 
    public final static String PROP_FIND_VALUE  = "report.value";
    public final static String PROP_INPUT_PATH = "report.input.path";
    public final static String PROP_OUTPUT_PATH = "report.output.path";
    
	@Override
	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(getConf(), this.getClass().getName());
		job.setJarByClass(getClass());
		configureJob(job);
		return job.waitForCompletion(true) ? 0 : 1;
	}

    private void configureJob(Job job) throws IOException {
        // configure output and input source
		String input = getInput();

		SequenceFileInputFormat.addInputPath(job, new Path(input));
		job.setInputFormatClass(SequenceFileInputFormat.class);

        Path outPath = getOutputPath();
        SequenceFileOutputFormat.setOutputPath(job, outPath);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        
        SequenceFileOutputFormat.setCompressOutput(job, true);
        SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.BLOCK);
        SequenceFileOutputFormat.setOutputCompressorClass(job, SnappyCodec.class);
        
        job.setOutputKeyClass(ReviewKeyWritable.class);
        job.setOutputValueClass(ReviewReport.class);
		
		// configure mapper and reducer
		job.setMapperClass(ReviewMapper.class);
		job.setReducerClass(ReviewReducer.class);
		
		// configure keys used between mappers and reducers
		job.setMapOutputKeyClass(ReviewKeyWritable.class);
		job.setMapOutputValueClass(ReviewWritable.class);
    }

    private String getInput() {
        String input = getConf().get(PROP_INPUT_PATH);
		Validate.notEmpty(input, "Must provide input path via [" + PROP_INPUT_PATH + "] property");
        return input;
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
		int exitCode = ToolRunner.run(new ReviewSequenceFileJob(), args);
		System.exit(exitCode);
	}
}
