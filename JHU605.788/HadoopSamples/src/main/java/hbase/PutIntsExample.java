package hbase;

import static org.apache.hadoop.hbase.util.Bytes.toBytes;
import static org.apache.hadoop.hbase.util.Bytes.toInt;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

/**
 *
yarn jar $PLAY_AREA/HadoopSamples.jar hbase.PutIntsExample
 */
public class PutIntsExample {

	public static void main(String[] args) throws IOException {
		Configuration conf = HBaseConfiguration.create();
		HTable hTable = new HTable(conf, "HBaseSamples");
		
		byte [] family = toBytes("test");
		byte [] column = toBytes("col1");
		for ( int i = 0; i < 130; i++ ){
		    Put put1 = new Put(toBytes(i));
		    put1.add(family, column, toBytes("val1"));
		    hTable.put(put1);
		}
		hTable.flushCommits();
		
        ResultScanner scanner = hTable.getScanner(new Scan());        
        for ( Result result : scanner){
            System.out.println("row id: " + toInt(result.getRow())); 
        } 
        scanner.close();
		
		hTable.close();
	}

}
