package examples;

import compression.CompareCompression;
import compression.ReviewJob_withCompression;
import compression.SimpleCompression;
import crunch.StartsWithCountCrunch;
import crunch.join.JoinPostsAndLikesCrunch;
import crunch.reviews.avro.ReviewReportCrunch;
import crunch.reviews.avro.ReviewReportCrunch1;
import crunch.reviews.hbase.ReviewReportCrunchWithHBase;
import hdfs.*;
import mr.chaining.JobControlDriver;
import mr.chaining.SimpleLinearDriver;
import mr.chaining.SimpleParallelDriver;
import mr.chaining.TaskChainingExample;
import mr.reviews.ReviewJob;
import mr.reviews.fsstruct.SimpleTextXmlJob;
import mr.reviews.fsstruct.SimpleTextXmlJob_CombineFileInputFormat;
import mr.reviews.fsstruct.mf.MapFileFix;
import mr.reviews.fsstruct.seq.ReviewSequenceFileJob;
import mr.wordcount.*;
import org.apache.hadoop.util.ProgramDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class ExamplesDriver {
    public static void main(String argv[]) throws Exception {
        int exitCode = -1;
        ProgramDriver pgd = new ProgramDriver();
        Class<?>[] hdfs = {
                SimpleLs.class, BadRename.class, CopyToHdfs.class,
                DeleteFile.class, LoadConfigurations.class,
                LsWithPathFilter.class, MkDir.class, ReadFile.class,
                SeekReadFile.class, SimpleGlobbing.class,
                SimpleLs.class, WriteToFile.class};

        Class<?>[] mr = {
                StartsWithCountJob.class,StartsWithCountJob_DistCache.class,
                StartsWithCountJob_DistCacheAPI.class,StartsWithCountJob_PrintCounters.class,
                StartsWithCountJob_UserCounters.class,
                ReviewJob.class, SimpleTextXmlJob.class,
                SimpleTextXmlJob_CombineFileInputFormat.class,
                ReviewSequenceFileJob.class
        };

        try {
            addClasses(pgd, hdfs, "HDFS");
            addClasses(pgd, mr, "MapReduce");
            pgd.driver(argv);

            // Success
            exitCode = 0;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        System.exit(exitCode);
    }

    static void addClasses(ProgramDriver pgd, Class<?>[] classes, String apiStr) throws Throwable {
        for (Class c : classes) {
            pgd.addClass(apiStr + "-" + c.getSimpleName(), c, apiStr + " API: [" + c.getCanonicalName() + "] class");
        }
    }

}
