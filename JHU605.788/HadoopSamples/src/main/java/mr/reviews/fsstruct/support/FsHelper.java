package mr.reviews.fsstruct.support;

import java.io.IOException;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class FsHelper {

    private final FileSystem fs;
    @SuppressWarnings("unused")
    private final Configuration conf; 
    public FsHelper(Configuration conf) throws IOException{
        Validate.notNull(conf);
        this.conf = conf;
        this.fs = FileSystem.get(conf);
    }
    
    public byte [] readBytes(Path path) throws IOException{
        FSDataInputStream in = null;
        try {
            in = fs.open(path);
            long bytesLen = fs.getFileStatus(path).getLen();
            byte[] buffer = new byte[(int)bytesLen];
            IOUtils.readFully(in, buffer, 0, buffer.length);
            return buffer;
        } finally {
            IOUtils.closeStream(in);
        }
    }
}
