package org.waman.gluino.nio

import java.nio.file.*
import java.nio.file.attribute.*

/**
 *
static Path	createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs)
static Path	createTempDirectory(String prefix, FileAttribute<?>... attrs)
static Path	createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs)
static Path	createTempFile(String prefix, String suffix, FileAttribute<?>... attrs)
 */
class GNioStaticExtension{
    
    @Lazy static Path userHome = Paths.get(System.getProperty("user.home"))

    /**
     * Get path of user.home
     */
    public static Path getUserHome(Path path){
        return userHome;
    }

    //********** Path of temporary file/directry **********
    /**
     * Create a temporary file with prefix and postfix into ${user.home} dir.
     */
    static Path createTempFile(
            Path path, String prefix = 'gluino-generated-', String suffix = '-temp', FileAttribute<?>... atts)
            throws IOException{
        return Files.createTempFile(prefix, suffix, atts)
    }

    /**
     * Create a temporary directory with prefix into ${user.home} dir.
     */
    static Path createTempDir(
            Path path, String prefix = 'gluino-generated-', FileAttribute<?>... atts)throws IOException{
        return Files.createTempDirectory(prefix, atts)
    }

    //********** PathMatcher **********
    /**
     * PathMatcher factory method from java.nio.file.FileSystem class.
     * 
     * @see FileSystem#getPathMatcher(String) 
     */
    static PathMatcher get(
            PathMatcher matcher, FileSystem fileSystem = FileSystems.getDefault(), String pattern){
        return fileSystem.getPathMatcher(pattern)
    }
}
