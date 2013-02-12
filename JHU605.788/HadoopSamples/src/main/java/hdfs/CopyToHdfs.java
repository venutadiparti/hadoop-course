package hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class CopyToHdfs {

	public static void main(String[] args) throws IOException {
		FileSystem fs = FileSystem.get(new Configuration());
		Path fromLocal = new Path(System.getenv("PLAY_AREA") + "/data/books/hamlet.txt");
		Path toHdfs = new Path("/training/playArea/hamlet.txt");
		fs.copyFromLocalFile(fromLocal, toHdfs);
	}

}
