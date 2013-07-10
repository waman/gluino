package org.waman.gluino.nio;

import java.nio.file.*;
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
