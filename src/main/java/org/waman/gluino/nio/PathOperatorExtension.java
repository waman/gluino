package org.waman.gluino.nio;

import java.nio.file.*;
import java.util.*;
import java.io.File;
import java.net.URI;
import groovy.lang.*;

/**
 * Implemeted methods : 
 * <ul>
 *   <li>div() : Path, /</li>
 *   <li>plus() : Path, +</li>
 
 *   <li>previous() : Path, --</li>
 *   <li>bitwiseNegate() : Path, ~</li>
 
 *   <li>minus() : Path, -</li>
 *   <li>multiply() : Path, *</li>
 *   <li>power() : Path, **</li>
 
 *   <li>isCase() : Path, in</li>
 
 *   <li>asType() : Path, as</li>
 
 *   <li>leftShift() : Path, &lt;&lt;</li>
 * </ul>
 */
public class PathOperatorExtension{

    //********** File Search **********
    public static Path div(Path parent, Path child){
        return parent.resolve(child);
    }

    public static Path div(Path parent, String child){
        return div(parent, Paths.get(child));
    }
    
    public static Path plus(Path parent, Path child){
        return parent.resolve(child);
    }
    
    public static Path plus(Path parent, String child){
        return parent.resolve(child);
    }

    public static Path previous(Path path){
        return path.getParent();
    }

    public static Path bitwiseNegate(Path path){
        return path.getParent();
    }
    
    //********** File Search **********
    public static Path minus(Path path, Path sub){
        int i = 0, n = path.getNameCount();
        Path result = null;
        while(result == null && i < n){
            if(!path.getName(i).equals(sub)){
                result = path.getName(i);
                i++;
                break;
            }
            i++;
        }
        for(;i < n; i++){
            if(!path.getName(i).equals(sub))
                result = result.resolve(path.getName(i));
        }
        return result;
    }

    public static Path minus(Path path, String sub){
        return minus(path, Paths.get(sub));
    }

    //********** Type Transformations **********
    public static Object asType(Path path, Class<?> type){
        if(Path.class.equals(type))
            return path;
        
        else if(String.class.equals(type))
            return path.toString();
        
        else if(File.class.equals(type))
            return path.toFile();
        
        else if(URI.class.equals(type))
            return path.toUri();
        
        else if(Path[].class.equals(type))
            return toArray(path);
        
        else if(String[].class.equals(type))
            return toStringArray(path);
        
        else if(List.class.equals(type))
            return toList(path);
        
        throw new ClassCastException("Path object cannot transform into the type "+type);
    }
    
    public static Path[] toArray(Path path){
         int n = path.getNameCount();
         Path[] result = new Path[n];
         for(int i = 0; i < n; i++)
             result[i] = path.getName(i);
         return result;
    }
    
    public static String[] toStringArray(Path path){
         int n = path.getNameCount();
         String[] result = new String[n];
         for(int i = 0; i < n; i++)
             result[i] = path.getName(i).toString();
         return result;
    }
    
    public static List<Path> toList(Path path){
         List<Path> result = new ArrayList<>(path.getNameCount());
         for(Path p : path)
             result.add(p);
         return result;
    }
    
    public static List<String> toStringList(Path path){
         List<String> result = new ArrayList<>(path.getNameCount());
         for(Path p : path)
             result.add(p.toString());
         return result;
    }
}
