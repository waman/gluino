package org.waman.gluino.nio

import java.nio.file.*
import java.nio.charset.Charset

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
 *       <li>readAllLines(Charset) : List&lt;String></li>
 *       <li>write(Iterable&lt;? extends CharSequence>, Charset, OpenOption...) : Path</li>
 *       <li>newBufferedReader(Charset) : BufferedReader</li>
 *       <li>newBufferedWriter(Charset, OpenOption...) : BufferedWriter</li>
 *     </ul>
 *   </li>
 * </ul>
 */
class GCharIOExtension{
    
    //********** Methods similar to java.io.File with GDK **********
    /**
     * @see File#getText()
     */
    static String getText(
            Path path, Charset cs = Charset.defaultCharset())
            throws IOException{
        List<String> lines = readAllLines(path, cs);
        
        StringBuilder builder = new StringBuilder();
        for(String line : lines)
            builder.append(line);
        
        return builder.toString();
    }
    
    //********** Methods similar to java.nio.file.Files **********
    /**
     * @see Files#readAllLines(Path path, Charset cs)
     */
    static List<String> readAllLines(
            Path path, Charset cs = Charset.defaultCharset())
            throws IOException{
        return Files.readAllLines(path, cs);
    }

    /**
     * @see Files#write(Path path, Iterable<? extends CharSequence> lines, Charset cs, OpenOption... options)
     */
    static Path write(
            Path path, Iterable<? extends CharSequence> lines,
            Charset cs = Charset.defaultCharset(), OpenOption... options)
            throws IOException{
        return Files.write(path, lines, cs, options);
    }
    
    /**
     * @see Files#newBufferedReader(Path path, Charset cs)
     */
    static BufferedReader newBufferedReader(
            Path path, Charset cs = Charset.defaultCharset())
            throws IOException{
        return Files.newBufferedReader(path, cs);
    }
    
    /**
     * @see Files#newBufferedWriter(Path path, Charset cs, OpenOption... options)
     */
    static BufferedWriter newBufferedWriter(
            Path path, Charset cs = Charset.defaultCharset(), OpenOption... options)
            throws IOException{
        return Files.newBufferedWriter(path, cs, options);
    }
}
