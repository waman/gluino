package org.waman.gluino.nio;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

public class JavascriptFileTypeDetector extends FileTypeDetector{

    @Override
    public String probeContentType(Path path) throws IOException {
        if(path.getFileName().toString().endsWith("js"))
            return "text/javascript";
        else
            return null;
    }
}
