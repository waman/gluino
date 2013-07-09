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
    
    public static Path getAt(Path path, EmptyRange range){
        return null;
    }
    
    public static Path getAt(Path path, IntRange range){
        int left  = !range.isReverse() ? range.getFromInt() : range.getToInt();
        int right = !range.isReverse() ? range.getToInt()   : range.getFromInt();
        
        left  = left  >= 0 ? left : left  + path.getNameCount();
        right = right >= 0 ? right: right + path.getNameCount();
        
        if(left == right){
            return path.getName(left);
        }else if(left < right){
            Path result = path.getName(left);
            for(int i = left+1; i <= right; i++)
                result = result.resolve(path.getName(i));
            return result;
        }else{
            Path result = path.getName(left);
            for(int i = left-1; i >= right; i--)
                result = result.resolve(path.getName(i));
            return result;
        }
    }
    
    public static Path getAt(Path path, List<Integer> indices){
        Path result = getAt(path, indices.get(0));
        for(int i = 1, n = indices.size(); i < n; i++)
            result = result.resolve(getAt(path, indices.get(i)));
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
