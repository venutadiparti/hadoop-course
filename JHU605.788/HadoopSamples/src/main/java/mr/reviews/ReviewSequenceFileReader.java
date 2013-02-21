package mr.reviews;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile.Reader;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ReviewSequenceFileReader extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {
        Path from = new Path(args[0]);
        
        FileSystem fs = FileSystem.get(getConf());
        Validate.isTrue(fs.exists(from));
        
        int i =0;
        Reader reader = null;
        try {
            reader = new Reader(getConf(), Reader.file(from));
            Writable key = (Writable)ReflectionUtils.newInstance(reader.getKeyClass(), getConf());
            Writable value = (Writable)ReflectionUtils.newInstance(reader.getValueClass(), getConf());
            readerLoop: while (reader.next(key, value)){
                printToScreen(key, value);
                i++;
                if (i>10){
                    break readerLoop;
                }
            }
        } finally {
            reader.close();
        }
        
        return 0;
    }

    private void printToScreen(Writable key, Writable value) {
        System.out.println("------------------");
        System.out.println("key="+key);
        System.out.println("value="+value);
    }

    public static void main(String[] args) throws Exception {
        int code = ToolRunner.run(new ReviewSequenceFileReader(), args);
        System.exit(code);
    }
}
