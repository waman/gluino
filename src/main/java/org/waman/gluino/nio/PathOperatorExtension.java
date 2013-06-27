package org.waman.gluino.nio;

import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.net.URI;

/**
 * Implemeted methods : 
 * <ul>
 *   <li>div() : Path, /</li>
 
 *   <li>previous() : Path, --</li>
 *   <li>bitwiseNegate() : Path, ~</li>
 
 *   <li>plus() : Path, +</li>
 *   <li>minus() : Path, -</li>
 *   <li>multiply() : Path, *</li>
 *   <li>power() : Path, **</li>
 *
 *   <li>getAt() : Path, []</li>
 *   <li>setAt() : Path, [] = </li>
 
 *   <li>isCase() : Path, in</li>
 
 *   <li>asType() : Path, as</li>
 
 *   <li>leftShift() : Path, &lt;&lt;</li>
 * </ul>
 */
public class PathOperatorExtension{

    public static Path div(Path parent, Path child){
        return parent.resolve(child);
    }

    public static Path div(Path parent, String child){
        return div(parent, Paths.get(child));
    }

    public static Path previous(Path path){
        return path.getParent();
    }

    public static Path bitwiseNegate(Path path){
        return path.getParent();
    }

    public static Path plus(Path path0, Path path1){
        return null;
    }

    public static Path plus(Path path, String s){
        return plus(path, Paths.get(s));
    } 

    public static Object asType(Path path, Class<?> type){
        if(Path.class.equals(type))
            return path;
        
        else if(String.class.equals(type))
            return path.toString();
        
        else if(File.class.equals(type))
            return path.toFile();
        
        else if(URI.class.equals(type))
            return path.toUri();
        
        else if(Path[].class.equals(type)){
             int n = path.getNameCount();
             Path[] result = new Path[n];
             for(int i = 0; i < n; i++)
                 result[i] = path.getName(i);
             return result;
        }
        
        else if(String[].class.equals(type)){
             int n = path.getNameCount();
             String[] result = new String[n];
             for(int i = 0; i < n; i++)
                 result[i] = path.getName(i).toString();
             return result;
        }
        
        else if(List.class.equals(type)){
             List<Path> result = new ArrayList<>(path.getNameCount());
             for(Path p : path)
                 result.add(p);
             return result;
        }
        
        throw new ClassCastException("Path object cannot transform into the type "+type);
    }
}
