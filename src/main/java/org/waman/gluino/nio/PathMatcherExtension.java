package org.waman.gluino.nio;

import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class PathMatcherExtension{

    public boolean isCase(PathMatcher matcher, Object arg){
        if(arg instanceof Path)
            return matcher.matches((Path)arg);
        else
            return false;
    }
}
