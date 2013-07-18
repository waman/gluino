package org.waman.gluino.nio;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.IOException;
import java.net.URI;

/**
 *
static Path	createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs)
static Path	createTempDirectory(String prefix, FileAttribute<?>... attrs)
static Path	createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs)
static Path	createTempFile(String prefix, String suffix, FileAttribute<?>... attrs)
 */
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
}
