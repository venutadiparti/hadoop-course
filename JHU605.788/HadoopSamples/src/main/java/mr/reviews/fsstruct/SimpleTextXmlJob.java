package mr.reviews.fsstruct;

import java.io.IOException;
import java.util.List;

import mr.reviews.ReviewOutputFormat;
import mr.reviews.ReviewReducer;
import mr.reviews.model.ReviewKeyWritable;
import mr.reviews.model.ReviewReport;
import mr.reviews.model.ReviewWritable;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class SimpleTextXmlJob extends Configured implements Tool{

    static class XmlProcessMapper extends Mapper<NullWritable, BytesWritable, ReviewKeyWritable, ReviewWritable> {
        private final ReviewKeyWritable keyWritable = new ReviewKeyWritable();
        private final ReviewWritable reviewWritable = new ReviewWritable();
        
        private List<String> valuesToLookFor;
        private Path path;
        private final XmlHelper helper = new XmlHelper();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            // assumption of input split type - not overly common but sometimes useful
            FileSplit fs = (FileSplit)context.getInputSplit();
            path = fs.getPath();
            
            ConfHelper confHelper = new ConfHelper(context.getConfiguration());
            valuesToLookFor = confHelper.getSearchTerms();
        }

        @Override
        protected void map(NullWritable key, BytesWritable value, Context context) throws IOException, InterruptedException {
            try {
                Review review = helper.convert(value.copyBytes());
                process(review, context);
            } catch (Exception e){
                throw new RuntimeException("Failed to process path [" + path + "] full text=[" + value.toString() + "]", e);
            }
        }
        public void process(Review review, Context context) throws IOException, InterruptedException {
            for (String valueToLookFor : valuesToLookFor) {
                if (isValueIn(valueToLookFor, review.getText())) {
                    updateWritables(valueToLookFor, review);
                    context.write(keyWritable, reviewWritable);
                }
            }
        }

        private boolean isValueIn(String valueToLookFor, String text) {
            // this logic can be as intricate as it needs to be
            return text.contains(valueToLookFor);
        }

        private void updateWritables(String valueToLookFor, Review review) {
            keyWritable.setKeyword(valueToLookFor);
            keyWritable.setAuthor(review.getUser());
            
            reviewWritable.setAuthor(review.getUser());
            reviewWritable.setContent(review.getText());
            reviewWritable.setPostedTimestamp(review.getTimestamp());
        }
    }
    
    @Override
    public int run(String[] args) throws Exception {
        ConfHelper confHelper = new ConfHelper(getConf());
        Job job = Job.getInstance(getConf(), this.getClass().getName());
        job.setJarByClass(getClass());
        // configure output and input source
        EntireFileTextInputFormat.addInputPath(job, confHelper.getInput());
        job.setInputFormatClass(EntireFileTextInputFormat.class);

        // configure mapper and reducer
        job.setMapperClass(XmlProcessMapper.class);
        job.setReducerClass(ReviewReducer.class);
        
        // configure keys used between mappers and reducers
        job.setMapOutputKeyClass(ReviewKeyWritable.class);
        job.setMapOutputValueClass(ReviewWritable.class);
        
        ReviewOutputFormat.setOutputPath(job, confHelper.getOutputPath());
        job.setOutputFormatClass(ReviewOutputFormat.class);
        job.setOutputKeyClass(ReviewKeyWritable.class);
        job.setOutputValueClass(ReviewReport.class);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new SimpleTextXmlJob(), args);
        System.exit(exitCode);
    }

}
