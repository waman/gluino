package org.waman.gluino.nio

import java.nio.file.*
import spock.lang.*

/**
 * tested methods : 
 * <ul>
 *   <li>Path#get() : Path</li>
 *   <li>PathMatcher#get() : PathMatcher</li>
 * </ul>
 */
class NioStaticTest extends Specification{
    
    def 'Path#get() : static facotry method of Path'(){
        expect:
        Path.get('src/test/groovy') == Paths.get('src/test/groovy')
        Path.get('src', 'test', 'groovy') == Paths.get('src/test/groovy')
        Path.get(new URI('file:///C:/waman/gluino/')) == Paths.get(new URI('file:///C:/waman/gluino/'))
    }
    
    def 'PathMatcher#get() : static facotry method of PathMatcher'(){
        expect:
        PathMatcher.get('glob:**.groovy').matches('src/test/groovy/NioStaticTest.groovy'.toPath())
        PathMatcher.get(FileSystems.getDefault(), 'glob:**.groovy').matches('src/test/groovy/NioStaticTest.groovy'.toPath())
    }
}
