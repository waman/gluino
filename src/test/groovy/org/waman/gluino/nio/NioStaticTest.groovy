package org.waman.gluino.nio

import java.nio.file.*
import spock.lang.*

/**
 * tested methods : 
 * <ul>
 *   <li>Path#get() : Path</li>
 *   <li>Path#createTempFile() : Path</li>
 *   <li>Path#createTempDir()  : Path</li>
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
    
    def 'Path#getUserHome()'(){
        expect:
        Path.getUserHome() == Path.get(System.getProperty('user.home'))
    }
    
    def 'Path#createTempFile()'(){
        when:
        def path = Path.createTempFile()
        
        then:
        path.fileName.toString() ==~ /(gluino-generated-).*(-temp)/
    }
    
    def 'Path#createTempFile(String, String)'(){
        when:
        def path = Path.createTempFile('pre-', '-post')
        
        then:
        path.fileName.toString() ==~ /(pre-).*(-post)/
    }
    
    def 'Path#createTempDir()'(){
        when:
        def path = Path.createTempDir()
        
        then:
        path[-1].toString() ==~ /(gluino-generated-).*/
    }
    
    def 'Path#createTempDir(String)'(){
        when:
        def path = Path.createTempDir('pre-')
        
        then:
        path[-1].toString() ==~ /(pre-).*/
    }
    
    //********** PathMatcher **********
    def 'PathMatcher#get() : static facotry method of PathMatcher'(){
        expect:
        PathMatcher.get('glob:**.groovy').matches('src/test/groovy/NioStaticTest.groovy'.toPath())
        PathMatcher.get(FileSystems.getDefault(), 'glob:**.groovy').matches('src/test/groovy/NioStaticTest.groovy'.toPath())
    }
}
