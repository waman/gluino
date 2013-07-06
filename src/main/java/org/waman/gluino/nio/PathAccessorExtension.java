package org.waman.gluino.nio;

import java.util.*;
import java.nio.file.*;
import groovy.lang.*;

/**
 * Implemeted methods : 
 * <ul>
 *   <li>getAt() : Path, []</li>
 *   <li>setAt() : Path, [] = </li>
 * </ul>
 */
public class PathAccessorExtension{

    //********** Accessors **********
    public static Path getAt(Path path, int i){
        if(i < 0)
            return path.getName(i + path.getNameCount());
        else if(i < path.getNameCount())
            return path.getName(i);
        else
            return null;
    }
    
    public static List<Path> getAt(Path path, EmptyRange range){
        return Collections.emptyList();
    }
    
    public static List<Path> getAt(Path path, IntRange range){
        int start = !range.isReverse() ? range.getFromInt() : range.getToInt();
        int end   = !range.isReverse() ? range.getToInt() : range.getFromInt();
        
        start = start >= 0 ? start : start + path.getNameCount();
        end   = end   >= 0 ? end   : end   + path.getNameCount();
        
        if(start == end){
            return Collections.singletonList(path.getName(start));
        }else if(start < end){
            List<Path> result = new ArrayList<>(end - start + 1);
            for(int i = start; i <= end; i++)
                result.add(path.getName(i));
            return result;
        }else{
            List<Path> result = new ArrayList<>(start - end + 1);
            for(int i = start; i >= end; i--)
                result.add(path.getName(i));
            return result;
        }
    }
    
    public static List<Path> getAt(Path path, List<Integer> indices){
        List<Path> result = new ArrayList<>(indices.size());
        for(int i : indices)
            result.add(getAt(path, i));
        return result;
    }
/*
    // these are not operators.
    public static Path setName(Path path, int i, Path value){
        List<Path> pathList = toList(path);
        pathList.set(i, value);
        return PathFactoryExtension.toPath(pathList);
    } 

    public static Path setName(Path path, int i, String value){
       return setName(path, i, Paths.get(value));
    } 
*/
}
