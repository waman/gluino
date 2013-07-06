package org.waman.gluino.nio

import java.nio.file.*
import spock.lang.*

/**
 * Tested methods : 
 * <ul>
 *   <li>getAt() : Path, []</li>
 *   <li>setAt() : Path, [] = </li>
 * </ul>
 */
class PathAccessorTest extends Specification{
    
    //********** getAt() **********
    def 'getAt(int) method'(){
        expect:
        'src/test/groovy'.toPath()[index] == result.toPath()
        
        where:
        index | result
        0     | 'src'
        1     | 'test'
        2     | 'groovy'
        -1    | 'groovy'
        -2    | 'test'
        -3    | 'src'
    }
    
    def 'getAt(empty) method'(){
        expect:
        'src/test/groovy'.toPath()[0..<0].isEmpty()
        'src/test/groovy'.toPath()[1..<1].isEmpty()
    }
    
    def 'getAt(intRange) method'(){
        expect:
        'src/test/groovy/org/waman/gluino/nio'.toPath()[indexRange].toPath() == 
                result.toPath()
        
        where:
        indexRange | result
         2..4      | 'groovy/org/waman'
         4..2      | 'waman/org/groovy'
         2..-2     | 'groovy/org/waman/gluino'
        -2..2      | 'gluino/waman/org/groovy'
        -4..-2     | 'org/waman/gluino'
        -2..-4     | 'gluino/waman/org'
         2..2      | 'groovy'
        -2..-2     | 'gluino'
         2..<4     | 'groovy/org'
         4..<2     | 'waman/org'
         2..<-2    | 'groovy/org/waman/gluino/nio'    // note!
        -2..<2     | 'gluino/waman/org/groovy/test'    // note!
        -4..<-2    | 'org/waman'
        -2..<-4    | 'gluino/waman'
         2..<3     | 'groovy'
         2..<1     | 'groovy'
        -2..<-1    | 'gluino'
        -2..<-3    | 'gluino'
    }
    
    def 'getAt(List) method'(){
        expect:
        'src/test/groovy/org/waman/gluino/nio'.toPath()[1, 5, 3, 2].toPath() == 
                'test/gluino/org/groovy'.toPath()
    }
    
/*
    //********** set **********
    def 'setName() method'(){
        expect:
        'src/test/groovy'.toPath().setName(1, 'main') == 'src/main/groovy'.toPath()
    }
*/
}
