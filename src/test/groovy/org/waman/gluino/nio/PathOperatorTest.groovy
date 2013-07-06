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
    def '/ operator return a child path'(){
        expect:
        parent / child == nioPath
        
        where:
        parent              | child             | nioPath
        'src/test'.toPath() | 'groovy'.toPath() | Paths.get('src/test/groovy') 
        'src/test'.toPath() | 'groovy'          | Paths.get('src/test/groovy') 
    }
    
    def '~ operator return a parent path'(){
        expect:
        ~current == parent
        
        where:
        current                    | parent
        'src/test/groovy'.toPath() | 'src/test'.toPath()
    }
    
    def '-- operator return a parent path'(){
        expect:
        --current == parent
        
        where:
        current                    | parent
        'src/test/groovy'.toPath() | 'src/test'.toPath()
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
