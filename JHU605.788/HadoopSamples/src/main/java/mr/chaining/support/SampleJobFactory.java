package mr.chaining.support;

import java.io.IOException;

import mr.chaining.support.RandomTextGen.RandomInputFormat;
import mr.chaining.support.RandomTextGen.RandomTextMapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.examples.WordCount.IntSumReducer;
import org.apache.hadoop.examples.WordCount.TokenizerMapper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.map.RegexMapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;

public class SampleJobFactory {

    public static Job randomTextWriter(Configuration inConf, String name, Path outputPath) {
        try {
            Configuration conf = new Configuration(inConf);
            setMemory(conf);
            
            conf.setInt(MRJobConfig.NUM_MAPS, 1);
            conf.setLong(RandomTextGen.BYTES_PER_MAP, 100*1024 ); // 100k
            
            Job job = Job.getInstance(conf, name);
            job.setJarByClass(SampleJobFactory.class);
            
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
            
            job.setInputFormatClass(RandomInputFormat.class);
            job.setMapperClass(RandomTextMapper.class); 
            
            job.setOutputFormatClass(TextOutputFormat.class);
            FileOutputFormat.setOutputPath(job, outputPath);
            job.setNumReduceTasks(0);
            return job;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to create [" + name + "] job", e);
        }
    }

    public static Job createWordCount(Configuration inConf, String name, Path input, Path output) {
        try {
            Configuration conf = new Configuration(inConf);
            setMemory(conf);
            Job job = Job.getInstance(conf, name);
            job.setJarByClass(SampleJobFactory.class);
            
            job.setMapperClass(TokenizerMapper.class);
            job.setCombinerClass(IntSumReducer.class);
            job.setReducerClass(IntSumReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            FileInputFormat.addInputPath(job, input);
            FileOutputFormat.setOutputPath(job, output);
            return job;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to create [" + name + "] job", e);
        }
    }
    
    public static Job createGrep(Configuration inConf, String name, Path input, Path output, String grepExpression) {
        try {
            Configuration conf = new Configuration(inConf);
            setMemory(conf);
            conf.set(RegexMapper.PATTERN, grepExpression);
            
            Job job = Job.getInstance(conf, name);
            job.setJarByClass(SampleJobFactory.class);

            FileInputFormat.setInputPaths(job, input);

            job.setMapperClass(RegexMapper.class);

            job.setCombinerClass(LongSumReducer.class);
            job.setReducerClass(LongSumReducer.class);

            FileOutputFormat.setOutputPath(job, output);
            job.setOutputFormatClass(TextOutputFormat.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(LongWritable.class);

            return job;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to create [" + name + "] job", e);
        }
    }
    
    public static void setMemory(Configuration conf){
        conf.setInt("mapreduce.map.memory.mb", 96);
        conf.setInt("mapreduce.reduce.memory.mb", 96);
        
        conf.set("mapreduce.map.java.opts", "-Xmx32m");
        conf.set("mapreduce.reduce.java.opts", "-Xmx32m");
    }
}
