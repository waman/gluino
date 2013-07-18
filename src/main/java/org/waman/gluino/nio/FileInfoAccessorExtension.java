package org.waman.gluino.nio;

import java.nio.file.*;
import java.nio.file.attribute.*;

import java.io.IOException;
import java.util.Set;
import java.util.Map;

/**
 * Implemented methods : 
 * <ul>
 *   <li>methods similar to java.io.File with GDK:
 *     <ul>
 *       <li></li>
 *     </ul>
 *   </li>
 *   <li>methods similar to java.nio.file.Files:
 *     <ul>
 *       <li>size() : long</li>
 *       <li>getLastModifiedTime(LinkOption...) : FileTime</li>
 *       <li>setLastModifiedTime(FileTime) : Path</li>
 *       <li>getOwner(LinkOption...) : UserPrincipal</li>
 *       <li>setOwner(UserPrincipal) : Path</li>
 *       <li>getPosixFilePermissions(LinkOption...) : Set&lt;PosixFilePermission></li>
 *       <li>setPosixFilePermissions(Set&lt;PosixFilePermission>) : Path</li>
 *
 *       <li>isSameFile(Path) : boolean</li>
 *       <li>isRegularFile(LinkedOption... options) : boolean</li>
 *       <li>isDirectory(LinkedOption... options) : boolean</li>
 *       <li>isExecutable() : boolean</li>
 *       <li>isHidden() : boolean</li>
 *       <li>isSymbolicLink() : boolean</li>
 *       <li>isReadable() : boolean</li>
 *       <li>isWritable() : boolean</li>
 *
 *       <li>getAttribute(String, LinkOption...) : Object</li>
 *       <li>readAttributes(String, LinkOption...) : Map&lt;String,Object></li>
 *       <li>readAttributes(Class&lt;A>, LinkOption...) : &lt;A extends BasicFileAttributes> A</li>
 *       <li>setAttribute(String, Object, LinkOption...) : Path</li>
 *       <li>getFileAttributeView(Class&lt;V>, LinkOption...) : &lt;V extends FileAttributeView> V</li>
 *
 *       <li>getFileStore() : FileStore</li>
 *       <li>probeContentType() : String</li>
 *       <li>readSymbolicLink() : Path</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public class FileInfoAccessorExtension{
    
    //********** getXxxx() (value-getter) **********
    /**
     * @see Files#size(Path)
     */
    public static long size(Path path)throws IOException{
        return Files.size(path);
    }
    
    /**
     * @see Files#getLastModifiedTime(Path, LinkOption...)
     */
    public static FileTime getLastModifiedTime(Path path, LinkOption... options)throws IOException{
        return Files.getLastModifiedTime(path, options);
    }
    
    /**
     * @see Files#setLastModifiedTime(Path, FileTime)
     */
    public static Path setLastModifiedTime(Path path, FileTime time)throws IOException{
        return Files.setLastModifiedTime(path, time);
    }
    
    /**
     * @see Files#getOwner(Path, LinkOption...)
     */
    public static UserPrincipal getOwner(Path path, LinkOption... options)throws IOException{
        return Files.getOwner(path, options);
    }
    
    /**
     * @see Files#setOwner(Path, UserPrincipal)
     */
    public static Path setOwner(Path path, UserPrincipal owner)throws IOException{
        return Files.setOwner(path, owner);
    }
    
    /**
     * @see Files#getPosixFilePermissions(Path, LinkOption...)
     */
     
    public static Set<PosixFilePermission> getPosixFilePermissions(Path path)throws IOException{
        return Files.getPosixFilePermissions(path);
    }
    
    /**
     * @see Files#setPosixFilePermissions(Path, Set&lt;PosixFilePermission)
     */
    public static Path setPosixFilePermissions(Path path, Set<PosixFilePermission> perms)throws IOException{
        return Files.setPosixFilePermissions(path, perms);
    }
    
    //********** isXxxx() (boolean-getter) **********
    /**
     * @see Files#isSameFile(Path, Path)
     */
    public static boolean isSameFile(Path path1, Path path2)throws IOException{
        return Files.isSameFile(path1, path2);
    }
    
    /**
     * @see Files#isRegularFile(Path, LinkOption...)
     */
    public static boolean isRegularFile(Path path, LinkOption... options){
        return Files.isRegularFile(path, options);
    }
    
    /**
     * @see Files#isDirectory(Path, LinkOption...)
     */
    public static boolean isDirectory(Path path, LinkOption... options){
        return Files.isDirectory(path, options);
    }
    
    /**
     * @see Files#isSymbolicLink(Path)
     */
    public static boolean isSymbolicLink(Path path){
        return Files.isSymbolicLink(path);
    }
    
    /**
     * @see Files#isExecutable(Path)
     */
    public static boolean isExecutable(Path path){
        return Files.isExecutable(path);
    }
    
    /**
     * @see Files#isHidden(Path)
     */
    public static boolean isHidden(Path path)throws IOException{
        return Files.isHidden(path);
    }
    
    /**
     * @see Files#isReadable(Path)
     */
    public static boolean isReadable(Path path){
        return Files.isReadable(path);
    }
    
    /**
     * @see Files#isWritable(Path)
     */
    public static boolean isWritable(Path path){
        return Files.isWritable(path);
    }
    
    //********** File Attribute Accessor **********
    /**
     * @see Files#getAttribute(Path, String, LinkOption...)
     */
    public static Object getAttribute(Path path, String name, LinkOption... options)
            throws IOException{
        return Files.getAttribute(path, name, options);
    }
    
    /**
     * @see Files#readAttributes(Path, String, LinkOption...)
     */
    public static Map<String, Object> readAttributes(Path path, String names, LinkOption... options)
            throws IOException{
        return Files.readAttributes(path, names, options);
    }
    
    /**
     * @see Files#readAttribute(Path, Class, LinkOption...)
     */
    public static <A extends BasicFileAttributes>
    A readAttribute(Path path, Class<A> att, LinkOption... options)
            throws IOException{
        return Files.readAttributes(path, att, options);
    }
    
    /**
     * @see Files#getAttribute(Path, String, LinkOption...)
     */
    public static Path setAttribute(Path path, String name, Object value, LinkOption... options)
            throws IOException{
        return Files.setAttribute(path, name, value, options);
    }
    
    /**
     * @see Files#getFileAttributeView(Path, Class, LinkOption...)
     */
    public static <V extends FileAttributeView>
    V getAttribute(Path path, Class<V> type, LinkOption... options){
        return Files.getFileAttributeView(path, type, options);
    }
    
    //********** Other methods **********
    /**
     * @see Files#getFileStore(Path)
     */
    public static FileStore getFileStore(Path path)throws IOException{
        return Files.getFileStore(path);
    }
    
    /**
     * @see Files#probeContentType(Path)
     */
    public static String probeContentType(Path path)throws IOException{
        return Files.probeContentType(path);
    }
    
    /**
     * @see Files#readSymbolicLink(Path)
     */
    public static Path readSymbolicLink(Path path)throws IOException{
        return Files.readSymbolicLink(path);
    }
}
