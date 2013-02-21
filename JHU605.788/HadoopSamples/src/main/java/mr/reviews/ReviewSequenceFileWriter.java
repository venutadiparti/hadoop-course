package mr.reviews;

import java.io.File;
import java.io.FileReader;

import mr.reviews.model.ReviewWritable;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.LineReader;

public class ReviewSequenceFileWriter extends Configured implements Tool {

    private final static Logger LOG = LoggerFactory.getLogger(ReviewSequenceFileWriter.class);

    @Override
    public int run(String[] args) throws Exception {
        File fromDir = new File(args[0]);
        Validate.isTrue(fromDir.exists());
        Path to = new Path(args[1]);

        FileSystem fs = FileSystem.get(getConf());
        fs.delete(to, true);
        Validate.isTrue(!fs.exists(to));
        if (!fs.exists(to.getParent())) {
            fs.mkdirs(to.getParent());
            LOG.info("Created root for sequence files [{}])", to.getParent());
        }

        LongWritable key = new LongWritable();
        ReviewWritable value = new ReviewWritable();

        long count = 0;
        Writer writer = null;
        try {
            writer = SequenceFile.createWriter(getConf(), Writer.file(to),
                    Writer.compression(CompressionType.BLOCK, new SnappyCodec()), Writer.keyClass(key.getClass()),
                    Writer.valueClass(value.getClass()));
            for (File from : fromDir.listFiles()) {
                FileReader inputReader = new FileReader(from);
                LineReader linerReader = new LineReader(inputReader);
                String line = null;
                while ((line = linerReader.readLine()) != null) {
                    populateWritable(value, line);
                    key.set(count++);
                    writer.append(key, value);
                }
            }
        } finally {
            writer.close();
        }

        return 0;
    }

    private void populateWritable(ReviewWritable value, String line) {
        String[] parsed = line.split(",");

        value.setAuthor(parsed[0]);
        value.setContent(parsed[1]);
        value.setPostedTimestamp(Long.parseLong(parsed[2]));
    }

    public static void main(String[] args) throws Exception {
        int code = ToolRunner.run(new ReviewSequenceFileWriter(), args);
        System.exit(code);
    }
}
