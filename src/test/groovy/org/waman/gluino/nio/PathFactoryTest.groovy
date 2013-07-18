package org.waman.gluino.nio

import java.nio.file.*
import spock.lang.*

/**
 * Tested methods : 
 * <ul>
 *   <li>String#toPath() : Path</li>
 *   <li>String[]#toPath() : Path</li>
 *   <li>Path[]#toPath() : Path</li>
 *   <li>List&lt;?>#toPath() : Path</li>
 *   <li>URI#toPath() : Path</li>
 * </ul>
 */
class PathFactoryTest extends Specification{

    def 'by String'(){
        expect:
        s.toPath() == nioPath
        
        where:
        s                 | nioPath
        'src'             | Paths.get('src')
        'src/test/groovy' | Paths.get('src/test/groovy')
    }

    def 'by String array (0 args)'(){
        when:
        def path = ([] as String[]).toPath()

        then:
        thrown(IllegalArgumentException)
    }

    def 'by String array'(){
        expect:
        (sa as String[]).toPath() == result
        
        where:
        sa                        | result
        ['src']                   | Paths.get('src')
        ['src', 'test', 'groovy'] | Paths.get('src/test/groovy')
    }

    def 'by Path array (0 args)'(){
        when:
        def path = ([] as Path[]).toPath()

        then:
        thrown(IllegalArgumentException)
    }

    def 'by Path array'(){
        expect:
        (pa as Path[]).toPath() == result
        
        where:
        pa                                                | result
        ['src'].collect{ it.toPath() }                   | Paths.get('src')
        ['src', 'test', 'groovy'].collect{ it.toPath() } | Paths.get('src/test/groovy')
    }

    def 'by empty List of String'(){
        when:
        def path = [].toPath()

        then:
        thrown(IllegalArgumentException)
    }

    def 'by List of String'(){
        expect:
        slist.toPath() == result
        
        where:
        slist                     | result
        ['src']                   | Paths.get('src')
        ['src', 'test', 'groovy'] | Paths.get('src/test/groovy')
    }

    def 'by List of Path'(){
        expect:
        plist.toPath() == result
        
        where:
        plist                                            | result
        ['src'].collect{ it.toPath() }                  | Paths.get('src')
        ['src', 'test', 'groovy'].collect{ it.toPath()} | Paths.get('src/test/groovy')
    }
    
    def 'by URI'(){
        when:
        def path = new URI('file:///C:/waman/gluino/').toPath()
        
        then:
        path == Paths.get(new URI('file:///C:/waman/gluino/'))
    }
}