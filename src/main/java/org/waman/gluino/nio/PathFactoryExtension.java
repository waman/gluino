package org.waman.gluino.nio;

import java.nio.file.*;
import java.util.List;
import java.net.URI;

/**
 * Implemented methods : 
 * <ul>
 *   <li>String#toPath() : Path</li>
 *   <li>String[]#toPath() : Path</li>
 *   <li>Path[]#toPath() : Path</li>
 *   <li>List&lt;?>#toPath() : Path</li>
 *   <li>URI#toPath() : Path</li>
 * </ul>
 */
public class PathFactoryExtension{

    public static Path toPath(String s){
        return Paths.get(s);
    }

    public static Path toPath(String[] sa){
        switch(sa.length){
    		case 0:
    		    throw new IllegalArgumentException("Argument array must not be empty :" + sa);
    		    
    		case 1:
    		    return Paths.get(sa[0]);
    		    
    		default:
    		    Path result = Paths.get(sa[0]);
    		    for(int i = 1, n = sa.length; i < n; i++)
    		        result = result.resolve(sa[i]);
    		    return result;
    	}
    }

    public static Path toPath(Path[] pa){
        switch(pa.length){
    		case 0:
    		    throw new IllegalArgumentException("Argument array must not be empty :" + pa);
    		    
    		case 1:
    		    return pa[0];
    		    
    		default:
    		    Path result = pa[0];
    		    for(int i = 1, n = pa.length; i < n; i++)
    		        result = result.resolve(pa[i]);
    		    return result;
    	}
    }

    public static Path toPath(List<?> list){
        switch(list.size()){
    		case 0:
    		    throw new IllegalArgumentException("Argument array must not be empty.");
    		    
    		case 1:
    		    Object o0 = list.get(0);
    		    return (o0 instanceof Path) ? (Path)o0 : Paths.get(o0.toString());
    		    
    		default:
    		    int n = list.size() - 1;
    		    Object obj0 = list.get(0);
    		    
    		    if(obj0 instanceof Path){
    		        Path result = (Path)list.get(0);
    		        for(int i = 1; i <= n; i++)
    		            result = result.resolve((Path)list.get(i));
    		        return result;
    		        
    		    }else{
    		        Path result = Paths.get(obj0.toString());
    		        for(int i = 1; i <= n; i++)
    		            result = result.resolve(list.get(i).toString());
    		        return result;
    		    }
    	}
    }

    public static Path toPath(URI uri){
        return Paths.get(uri);
    }
}