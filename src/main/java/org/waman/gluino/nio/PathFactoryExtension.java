package org.waman.gluino.nio;

import java.nio.file.*;
import java.util.List;
import java.net.URI;

/**
 * Implemented methods : 
 * <ul>
 *   <li>String#toPath() : Path</li>
 *   <li>String[]#toPath() : Path</li>
 *   <li>List&lt;String>#toPath() : Path</li>
 *   <li>URI#toPath() : Path</li>
 * </ul>
 */
public class PathFactoryExtension{

    public static Path toPath(String s){
        return Paths.get(s);
    }

    public static Path toPath(String[] s){
        switch(s.length){
    		case 0:
    		    throw new IllegalArgumentException("Argument array must not be empty :" + s);
    		    
    		case 1:
    		    return Paths.get(s[0]);
    		    
    		default:
    		    int n = s.length - 1;
    		    String[] sub = new String[n];
    		    System.arraycopy(s, 1, sub, 0, n);
    		    return Paths.get(s[0], sub);
    	}
    }

    public static Path toPath(List<String> s){
        switch(s.size()){
    		case 0:
    		    throw new IllegalArgumentException("Argument array must not be empty :" + s);
    		    
    		case 1:
    		    return Paths.get(s.get(0));
    		    
    		default:
    		    int n = s.size() - 1;
    		    String[] sub = new String[n];
    		    for(int i = 0; i < n; i++)sub[i] = s.get(i+1);
    		    return Paths.get(s.get(0), sub);
    	}
    }

    public static Path toPath(URI uri){
        return Paths.get(uri);
    }
}