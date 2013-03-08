package mr.reviews.fsstruct.avro;

import java.io.InputStream;

import mr.reviews.fsstruct.avro.model.ReviewAvro;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class AvroReaderSpecific extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {
        Path inputPath = new Path(args[0]);
        FileSystem fs = FileSystem.get(getConf());
        Validate.isTrue(fs.exists(inputPath) && fs.isFile(inputPath));

        InputStream in = null;
        DataFileStream<ReviewAvro> reader = null;
        try {
            in = fs.open(inputPath);
            // Prior 1.7.3 release will use classloader that many not have 
            // knowledge of ReviewAvro class will not be found; avro then defaults
            // to GenericData$Record which cause this code to get
            // ClassCastException; This issue is addressed/explained in
            // https://issues.apache.org/jira/browse/AVRO-1123
            SpecificData specificData = new SpecificData(this.getClass().getClassLoader());
            SpecificDatumReader<ReviewAvro> specificDatumReader = new SpecificDatumReader<ReviewAvro>(
                    ReviewAvro.SCHEMA$, ReviewAvro.SCHEMA$, specificData);
            reader = new DataFileStream<ReviewAvro>(in, specificDatumReader);
            ReviewAvro review = null;
            while (reader.hasNext()) {
                // ReviewAvro object could be re-used in this way
                // - reduces allocation and GC
                review = reader.next(review);
                print(review);
            }
        } finally {
            IOUtils.closeStream(in);
            IOUtils.closeStream(reader);
        }
        return 0;
    }

    private void print(ReviewAvro review) {
        String str = ToStringBuilder.reflectionToString(review, ToStringStyle.SIMPLE_STYLE);
        System.out.println(str);
    }

    public static void main(String[] args) throws Exception {
        int code = ToolRunner.run(new AvroReaderSpecific(), args);
        System.exit(code);
    }
}
