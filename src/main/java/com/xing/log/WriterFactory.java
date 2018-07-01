package com.xing.log;

import com.xing.log.api.IWriter;
import com.xing.log.writer.FileWriter;
import com.xing.log.writer.SystemOutWriter;

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
