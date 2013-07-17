package org.waman.gluino.nio;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.IOException;
import java.net.URI;

public class NioStaticExtension{

    //********** Path **********
    /**
     * Path factory method from java.nio.file.Path.
     */
    public static Path get(Path path, String first, String... rest){
        return Paths.get(first, rest);
    }

    /**
     * Path factory method from java.nio.file.Path.
     */
    public static Path get(Path path, URI uri){
        return Paths.get(uri);
    }

    /**
     * Get path of user.home
     */
    public static Path getUserHome(Path path){
        return Paths.get(System.getProperty("user.home"));
    }

    //********** Path of temporary file/directry **********
    /**
     * Create a temporary file into ${user.home} dir.
     */
    public static Path createTempFile(Path path, FileAttribute<?>... atts)throws IOException{
        return Files.createTempFile("gluino-generated-", "-temp", atts);
    }
    
    /**
     * Create a temporary file with prefix and postfix into ${user.home} dir.
     */
    public static Path createTempFile(Path path, String prefix, String suffix, FileAttribute<?>... atts)
            throws IOException{
        return Files.createTempFile(prefix, suffix, atts);
    }

    /**
     * Create a temporary directory into ${user.home} dir.
     */
    public static Path createTempDir(Path path, FileAttribute<?>... atts)throws IOException{
        return Files.createTempDirectory("gluino-generated-", atts);
    }

    /**
     * Create a temporary directory with prefix into ${user.home} dir.
     */
    public static Path createTempDir(Path path, String prefix, FileAttribute<?>... atts)throws IOException{
        return Files.createTempDirectory(prefix, atts);
    }

    //********** PathMatcher **********
    /**
     * PathMatcher factory method from java.nio.file.FileSystem class.
     */
    public static PathMatcher get(PathMatcher matcher, String pattern){
        return get(matcher, FileSystems.getDefault(), pattern);
    }

    /**
     * PathMatcher factory method from java.nio.file.FileSystem class.
     */
    public static PathMatcher get(PathMatcher matcher, FileSystem fileSystem, String pattern){
        return fileSystem.getPathMatcher(pattern);
    }
}
