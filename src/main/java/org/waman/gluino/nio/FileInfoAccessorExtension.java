package org.waman.gluino.nio;

import java.io.IOException;
import java.nio.file.*;

public class FileInfoAccessorExtension{
    
    public static long size(Path path)throws IOException{
        return Files.size(path);
    }
}
