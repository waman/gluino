package org.waman.gluino.nio

import java.nio.file.*
import spock.lang.*
import java.net.URI

/**
 * Tested methods : 
 * <ul>
 *   <li>div() : Path, /</li>
 
 *   <li>bitwiseNegate() : Path, ~</li>
 *   <li>previous() : Path, --</li>
 
 *   <li>plus() : Path, +</li>
 *   <li>minus() : Path, -</li>
 *   <li>multiply() : Path, *</li>
 *   <li>power() : Path, **</li>
 *   <li>getAt() : Path, []</li>
 *   <li>setAt() : Path, [] = </li>
 *   <li>isCase() : Path, in</li>
 
 *   <li>asType() : Path, as</li>
 * </ul>
 */
class PathOperatorTest extends Specification{
    
    //********** File Search **********
    def '/ operator returns a child path'(){
        expect:
        parent.toPath() / child == nioPath.toPath()
        
        where:
        parent     | child             | nioPath
        'src/test' | 'groovy'.toPath() | 'src/test/groovy'
        'src/test' | 'groovy'          | 'src/test/groovy'
    }
    
    def '+ operator returns a child path, too'(){
        expect:
        parent.toPath() + child == nioPath.toPath()
        
        where:
        parent     | child             | nioPath
        'src/test' | 'groovy'.toPath() | 'src/test/groovy'
        'src/test' | 'groovy'          | 'src/test/groovy'
    }
    
    def '~ operator returns a parent path'(){
        expect:
        ~(current.toPath()) == parent.toPath()
        
        where:
        current           | parent
        'src/test/groovy' | 'src/test'
    }
    
    def '-- operator returns a parent path, too'(){
        expect:
        --(current.toPath()) == parent.toPath()
        
        where:
        current           | parent
        'src/test/groovy' | 'src/test'
    }
    
    //********** Path Transformation **********
    def '- operator removes a specified path fragment(s)'(){
        expect:
        current.toPath() - sub == result.toPath()
        
        where:
        current                       | sub             | result
        'src/test/groovy'             | 'test'.toPath() | 'src/groovy'
        'src/test/groovy'             | 'test'          | 'src/groovy'
        'src/test/groovy/test/gluino' | 'test'.toPath() | 'src/groovy/gluino'
        'src/test/groovy/test/gluino' | 'test'          | 'src/groovy/gluino'
        'test/src/groovy/gluino'      | 'test'.toPath() | 'src/groovy/gluino'
        'test/src/groovy/gluino'      | 'test'          | 'src/groovy/gluino'
        'src/groovy/gluino'           | 'test'.toPath() | 'src/groovy/gluino'
        'src/groovy/gluino'           | 'test'          | 'src/groovy/gluino'
    }
    
    def '- operator can remove all path fragments (then return null)'(){
        expect:
        current.toPath() - sub == null
        
        where:
        current     | sub
        'test'      | 'test'.toPath()
        'test'      | 'test'
        'test/test' | 'test'.toPath()
        'test/test' | 'test'
    }
    
    //********** Type Transformation **********
    def 'as operator transforms Path into proper type'(){
        expect:
        'src/test/groovy'.toPath().asType(type) == result
        
        where:
        type | result
        String | Paths.get('src/test/groovy').toString()
        File | new File('src/test/groovy')
        //URI | new URI('src/test/groovy')
        String[] | (['src', 'test', 'groovy'] as String[])
        Path[] | (['src', 'test', 'groovy'].collect{ it.toPath() } as Path[])
        List | ['src', 'test', 'groovy'].collect{ it.toPath() }
    }
    
    def 'as operator transforms Path into URI'(){
        expect:
        'src/test/groovy'.toPath().asType(URI) in URI
    }
}
