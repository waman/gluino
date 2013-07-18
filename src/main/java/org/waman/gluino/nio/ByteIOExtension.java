package org.waman.gluino.nio;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.channels.SeekableByteChannel;
import static java.nio.file.StandardOpenOption.*;

import java.util.Set;
import groovy.lang.Closure;

/**
 * Implemented methods : 
 * <ul>
 *   <li>methods similar to java.io.File with GDK:
       <ul>
 *       <li>getBytes() : byte[]</li>
 *       <li>eachByte(Closure) : void</li>
 *       <li>eachByte(int, Closure) : void</li>
 *       <li>setBytes(byte[]) : void</li>
 *       <li>append(byte) : void</li>
 *       <li>leftShift(byte) : Path</li>
 *     </ul>
 *   </li>
 *   <li>methods similar to java.nio.file.Files:
       <ul>
 *       <li>readAllBytes() : byte[]</li>
 *       <li>write(byte[], OpenOption...) : Path</li>
 *       <li>newInputStream(OpenOption...) : InputStream</li>
 *       <li>newOutputStream(OpenOption...) : OutputStream</li>
 *       <li>newByteChannel(OpenOption...) : SeekableByteChannel</li>
 *       <li>newByteChannel(Set&lt;? extends OpenOption>, FileAttribute&lt;?>...) : SeekableByteChannel</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public class ByteIOExtension{

    //********** Methods similar to java.io.File with GDK **********
    /**
     * @see File#getBytes()
     */
    public static byte[] getBytes(Path path)throws IOException{
        return Files.readAllBytes(path);
    }
    
    /**
     * @see File#setBytes(byte[])
     */
    public static void setBytes(Path path, byte[] bytes)throws IOException{
        Files.write(path, bytes);
    }
    
    /**
     * @see File#append(byte)
     */
    public static void append(Path path, byte b)throws IOException{
        Files.write(path, new byte[]{b}, CREATE, APPEND);
    }
    
    /**
     * @see File#leftShift(byte)
     */
    public static Path leftShift(Path path, byte b)throws IOException{
        append(path, b);
        return path;
    }
    
    //********** Methods similar to java.nio.file.Files **********
    /**
     * @see Files#readAllBytes(Path)
     */
    public static byte[] readAllBytes(Path path)throws IOException{
        return Files.readAllBytes(path);
    }
    
    /**
     * @see Files#write(Path, byte[], OpenOption...)
     */
    public static Path write(Path path, byte[] bytes, OpenOption... options)throws IOException{
        return Files.write(path, bytes, options);
    }
    
    /**
     * @see Files#newInputStream(Path, OpenOption...)
     */
    public static InputStream newInputStream(Path path, OpenOption... options)throws IOException{
        return Files.newInputStream(path, options);
    }
    
    /**
     * @see Files#newOutputStream(Path, OpenOption...) : SeekableByteChannel</li>
 *       <li> : 
     */
    public static OutputStream newOutputStream(Path path, OpenOption... options)throws IOException{
        return Files.newOutputStream(path, options);
    }
    
    /**
     * @see Files#newByteChannel(Path, OpenOption...)
     */
    public static SeekableByteChannel newByteChannel(Path path, OpenOption... options)throws IOException{
        return Files.newByteChannel(path, options);
    }
    
    /**
     * @see Files#newByteChannel(Path, Set<? extends OpenOption>, FileAttribute<?>...)
     */
    public static SeekableByteChannel newByteChannel(
            Path path, Set<? extends OpenOption> options, FileAttribute<?>... atts)throws IOException{
        return Files.newByteChannel(path, options, atts);
    }
}