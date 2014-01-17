package examples;

import hdfs.*;
import mr.reviews.ReviewJob;
import mr.reviews.fsstruct.SimpleTextXmlJob;
import mr.reviews.fsstruct.SimpleTextXmlJob_CombineFileInputFormat;
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
                ReviewJob.class, SimpleTextXmlJob.class,
                SimpleTextXmlJob_CombineFileInputFormat.class
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
            pgd.addClass(apiStr+"-"+c.getSimpleName(), c, apiStr + " API: [" + c.getCanonicalName() + "] class");
        }
    }

}
