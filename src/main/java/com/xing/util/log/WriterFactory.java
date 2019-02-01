package com.xing.util.log;

import com.xing.util.log.api.IWriter;
import com.xing.util.log.writer.FileWriter;
import com.xing.util.log.writer.SystemOutWriter;

public class WriterFactory {
    public static IWriter getLogWriter() {
        boolean findPackagePathAndDir = false;
        if (findPackagePathAndDir) {
            return FileWriter.get();
        } else {
            return SystemOutWriter.INSTANCE;
        }
    }
}
