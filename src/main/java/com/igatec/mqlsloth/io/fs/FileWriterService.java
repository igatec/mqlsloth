package com.igatec.mqlsloth.io.fs;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileWriterService {

    public void writeNewFile(File file, String content) throws IOException {
        FileUtils.writeStringToFile(file, content, false);
    }
}
