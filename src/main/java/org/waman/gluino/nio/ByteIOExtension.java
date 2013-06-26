package org.waman.gluino.nio;

import java.io.*;
import java.nio.file.*;

public class ByteIOExtension{

    public static byte[] getBytes(Path path)throws IOException{
        return Files.readAllBytes(path);
    }
}