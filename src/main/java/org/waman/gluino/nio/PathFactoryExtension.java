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

    public static Path toPath(List<?> list){
        switch(list.size()){
    		case 0:
    		    throw new IllegalArgumentException("Argument array must not be empty.");
    		    
    		case 1:
    		    Object o0 = list.get(0);
    		    return (o0 instanceof String) ? Paths.get((String)o0) : (Path)o0;
    		    
    		default:
    		    int n = list.size() - 1;
    		    Object obj0 = list.get(0);
    		    
    		    if(obj0 instanceof String){
    		        Path result = Paths.get((String)obj0);
    		        for(int i = 1; i <= n; i++)
    		            result = result.resolve((String)list.get(i));
    		        return result;
    		        
    		    }else{
    		        Path result = (Path)list.get(0);
    		        for(int i = 1; i <= n; i++)
    		            result = result.resolve((Path)list.get(i));
    		        return result;
    		    }
    	}
    }

    public static Path toPath(URI uri){
        return Paths.get(uri);
    }
}