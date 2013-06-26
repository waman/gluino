package org.waman.gluino.nio;

import java.io.*;
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;
import groovy.lang.Closure;

/**
 * Implemented methods : 
 * <ul>
 *   <li>getBytes() : byte[]</li>
 *   <li>eachByte(Closure) : void</li>
 *   <li>eachByte(int, Closure) : void</li>
 *   <li>setBytes(byte[]) : void</li>
 *   <li>append(byte) : void</li>
 *   <li>leftShift(byte) : Path</li>
 * </ul>
 */
public class ByteIOExtension{

    public static byte[] getBytes(Path path)throws IOException{
        return Files.readAllBytes(path);
    }
    
    public static void setBytes(Path path, byte[] bytes)throws IOException{
        Files.write(path, bytes);
    }
    
    public static void append(Path path, byte b)throws IOException{
        Files.write(path, new byte[]{b}, CREATE, APPEND);
    }
    
    public static Path leftShift(Path path, byte b)throws IOException{
        append(path, b);
        return path;
    }
}