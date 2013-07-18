package org.waman.gluino.nio;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;

/**
 *
static Path	createFile(Path path, FileAttribute<?>... attrs)
static Path	createDirectory(Path dir, FileAttribute<?>... attrs)
static Path	createDirectories(Path dir, FileAttribute<?>... attrs)
static Path	createLink(Path link, Path existing)
static Path	createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs)

static boolean	exists(Path path, LinkOption... options)
static boolean	notExists(Path path, LinkOption... options)

static void	delete(Path path)
static boolean	deleteIfExists(Path path)

static Path	move(Path source, Path target, CopyOption... options)
static long	copy(Path source, OutputStream out)
static Path	copy(Path source, Path target, CopyOption... options)
 */
public class FileOperationExtension{

    //********** Create **********
    /**
     * @see Files.createFile(Path, FileAttribute<?>...)
     */
    public static Path createFile(Path path, FileAttribute<?>... atts)throws IOException{
        return Files.createFile(path, atts);
    }
    
    /**
     * @see Files.createDirectory(Path, FileAttribute<?>...)
     */
    public static Path createDirectory(Path path, FileAttribute<?>... atts)throws IOException{
        return Files.createDirectory(path, atts);
    }
    
    /**
     * @see Files.createDirectories(Path, FileAttribute<?>...)
     */
    public static Path createDirectories(Path path, FileAttribute<?>... atts)throws IOException{
        return Files.createDirectories(path, atts);
    }
    
    /**
     * @see Files.createLink(Path, Path)
     */
    public static Path createLink(Path link, Path target)throws IOException{
        return Files.createLink(link, target);
    }
    
    /**
     * @see Files.SymbolicLink(Path, Path, FileAttribute<?>...)
     */
    public static Path createSymbolicLink(Path link, Path target, FileAttribute<?>... atts)throws IOException{
        return Files.createSymbolicLink(link, target, atts);
    }

    //********** Existence **********
    /**
     * @see Files.exists(Path, LinkOption...)
     */
    public static boolean exists(Path path, LinkOption... options){
        return Files.exists(path, options);
    }

    /**
     * @see Files.notExists(Path, LinkOption...)
     */
    public static boolean notExists(Path path, LinkOption... options){
        return Files.notExists(path, options);
    }

    //********** Delete **********
    /**
     * @see Files.delete(Path)
     */
    public static void delete(Path path)throws IOException{
        Files.delete(path);
    }

    /**
     * @see Files.deleteIfExists(Path)
     */
    public static boolean deleteIfExists(Path path)throws IOException{
        return Files.deleteIfExists(path);
    }

    //********** move(), renameTo(), copy() **********
    /**
     * @see Files#move(Path, Path, CopyOption...)
     */
    public static Path move(Path src, Path target, CopyOption... options)throws IOException{
        return Files.move(src, target, options);
    }

    /**
     * @see File#renameTo(String)
     * @see Files#move(Path, Path, CopyOption...)
     */
    public static boolean renameTo(Path src, String target, CopyOption... options){
        try{
            Files.move(src, Paths.get(target), options);
            return true;
            
        }catch(Exception ex){
            return false;
        }
    }
    
    /**
     * @see Files#copy(Path, OutputStream)
     */
    public static long copy(Path src, java.io.OutputStream os)throws IOException{
        return Files.copy(src, os);
    }

    /**
     * @see Files#copy(Path, Path, CopyOption...)
     */
    public static Path copy(Path src, Path target, CopyOption... options)throws IOException{
        return Files.copy(src, target, options);
    }
}
