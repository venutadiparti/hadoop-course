package mr.wordcount;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class StartsWithCountMapperTest {

    class KeyValContainer{
        public KeyValContainer(Text key, IntWritable value) {
            this.key = key;
            this.value = value;
        }
        Text key;
        IntWritable value;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void processLineOfText() throws IOException, InterruptedException{
        StartsWithCountMapper underTest = new StartsWithCountMapper();
        LongWritable inputKey = new LongWritable();
        Text inputValue = new Text("Must process this line of text");
        Context context = mock(Context.class, RETURNS_DEEP_STUBS);
        final List<KeyValContainer> collected = new ArrayList<KeyValContainer>();
        Answer ans = new Answer() {
            @Override
            public Object answer(InvocationOnMock inv) {
                Object[] args = inv.getArguments();
                KeyValContainer keyVal = new KeyValContainer(
                        new Text(((Text)args[0]).copyBytes()), 
                        new IntWritable(((IntWritable)args[1]).get()));
                collected.add(keyVal);
                return inv.getMock();
            }
        };
        doAnswer(ans).when(context).write(anyObject(), anyObject());

        underTest.map(inputKey, inputValue, context);
        
        assertEquals(6, collected.size());
        assertKeyVal(collected.get(0), "M", 1);
        assertKeyVal(collected.get(1), "p", 1);
        assertKeyVal(collected.get(2), "t", 1);
        assertKeyVal(collected.get(3), "l", 1);
        assertKeyVal(collected.get(4), "o", 1);
        assertKeyVal(collected.get(5), "t", 1);
    }
    
    private void assertKeyVal(KeyValContainer container, String expectedK, int expectedV){
        assertEquals(expectedK, container.key.toString());
        assertEquals(expectedV, container.value.get());
    }
    
    @Test
    public void processLineOfTextWithMRUnit() {
        LongWritable inputKey = new LongWritable();
        Text inputValue = new Text("Must process this line of text");
        new MapDriver<LongWritable, Text, Text, IntWritable>()
            .withMapper(new StartsWithCountMapper())
            .withInput(inputKey, inputValue)
            .withOutput(new Text("M"), new IntWritable(1))
            .withOutput(new Text("p"), new IntWritable(1))
            .withOutput(new Text("t"), new IntWritable(1))
            .withOutput(new Text("l"), new IntWritable(1))
            .withOutput(new Text("o"), new IntWritable(1))
            .withOutput(new Text("t"), new IntWritable(1))
            .runTest();
    }
}
