package mr.blogs.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import mr.blogs.model.BlogKeyWritable;
import mr.blogs.model.BlogWritable;

import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Writable;
import org.junit.Test;

public class WritableTest {

    @Test
    public void testBlogWritableSerDer() throws IOException {
        String author = "author";
        String content = "content";
        Long posted = 100000000l;
        
        BlogWritable origWritable = new BlogWritable(author, content, posted);
        
        DataOutputBuffer b1 = writeWritable(origWritable);
        DataInputBuffer in = new DataInputBuffer();
        in.reset(b1.getData(), b1.getLength());

        BlogWritable result = new BlogWritable();
        result.readFields(in);
        assertEquals(author, result.getAuthor());
        assertEquals(content, result.getContent());
        assertEquals(posted, result.getPostedTimestamp());
    }
    
    @Test
    public void testBlogKeyWritableSerDer() throws IOException {
        String author = "author";
        String keyword = "keyword";
        
        BlogKeyWritable origWritable = new BlogKeyWritable(author,keyword);
        
        DataOutputBuffer b1 = writeWritable(origWritable);
        DataInputBuffer in = new DataInputBuffer();
        in.reset(b1.getData(), b1.getLength());

        BlogKeyWritable result = new BlogKeyWritable();
        result.readFields(in);
        assertEquals(author, result.getAuthor());
        assertEquals(keyword, result.getKeyword());
    }
    
    protected DataOutputBuffer writeWritable(Writable writable)
            throws IOException {
        DataOutputBuffer out = new DataOutputBuffer(1024);
        writable.write(out);
        out.flush();
        return out;
    }

}
