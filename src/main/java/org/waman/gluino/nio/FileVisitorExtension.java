package org.waman.gluino.nio;

import java.nio.file.*;
import java.io.IOException;
import java.util.Set;

/**
 * Implemented methods : 
 * <ul>
 *   <li>methods similar to java.io.File with GDK:
       <ul>
 *       <li></li>
 *     </ul>
 *   </li>
 *   <li>methods similar to java.nio.file.Files:
       <ul>
 *       <li>newDirectoryStream() : DirectoryStream</li>
 *       <li>newDirectoryStream(DirectoryStream.Filter<? super Path>) : DirectoryStream</li>
 *       <li>newDirectoryStream(String) : DirectoryStream</li>
 
 *       <li>walkFileTree(FileVisitor<? super Path>) : Path</li>
 *       <li>walkFileTree(Set<FileVisitOption>, int, FileVisitor<? super Path>) : Path</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public class FileVisitorExtension{
    
    //********** Methods similar to java.io.File with GDK **********

    
    //********** Methods similar to java.nio.file.Files **********
    /**
     * @see Files#newDirectoryStream(Path)
     */
    public static DirectoryStream newDirectoryStream(Path path)throws IOException{
        return Files.newDirectoryStream(path);
    }
    
    /**
     * @see Files#newDirectoryStream(Path, DirectoryStream.Filter<? super Path>)
     */
    public static DirectoryStream newDirectoryStream(
            Path path, DirectoryStream.Filter<? super Path> filter)
            throws IOException{
        return Files.newDirectoryStream(path, filter);
    }
    
    /**
     * @see Files#newDirectoryStream(Path, String)
     */
    public static DirectoryStream newDirectoryStream(Path path, String glob)throws IOException{
        return Files.newDirectoryStream(path, glob);
    }
    
    /**
     * @see Files#walkFileTree(Path, FileVisitor<? super Path>)
     */
    public static Path walkFileTree(Path path, FileVisitor<? super Path> visitor)throws IOException{
        return Files.walkFileTree(path, visitor);
    }
    
    /**
     * @see Files#walkFileTree(Path, Set<FileVisitOption>, int, FileVisitor<? super Path>)
     */
    public static Path walkFileTree(
            Path path, Set<FileVisitOption> options, int maxDepth, FileVisitor<? super Path> visitor)
            throws IOException{
        return Files.walkFileTree(path, options, maxDepth, visitor);
    }
}
