package org.waman.gluino.nio

import java.nio.file.*
import spock.lang.*

/**
 * Tested methods : 
 * <ul>
 *   <li>String#toPath() : Path</li>
 *   <li>String[]#toPath() : Path</li>
 *   <li>List&lt;String>#toPath() : Path</li>
 *   <li>URI#toPath() : Path</li>
 * </ul>
 */
class PathFactoryTest extends Specification{

    def 'by String (1 arg)'(){
        when:
        def path = 'src'.toPath()

        then:
        path == Paths.get('src')
    }

    def 'by String (3 args)'(){
        when:
        def path = 'src/test/groovy'.toPath()

        then:
        path == Paths.get('src', 'test', 'groovy')
    }

    def 'by String array (0 args)'(){
        when:
        def path = ([] as String[]).toPath()

        then:
        thrown(IllegalArgumentException)
    }

    def 'by String array (1 args)'(){
        when:
        def path = (['src'] as String[]).toPath()

        then:
        path == Paths.get('src')
    }

    def 'by String array (3 args)'(){
        when:
        def path = (['src', 'test', 'groovy'] as String[]).toPath()

        then:
        path == Paths.get('src', 'test', 'groovy')
    }

    def 'by List of String (0 args)'(){
        when:
        def path = [].toPath()

        then:
        thrown(IllegalArgumentException)
    }

    def 'by List of String (1 args)'(){
        when:
        def path = ['src'].toPath()

        then:
        path == Paths.get('src')
    }

    def 'by List of String (3 args)'(){
        when:
        def path = ['src', 'test', 'groovy'].toPath()

        then:
        path == Paths.get('src', 'test', 'groovy')
    }
    
    def 'by URI'(){
        when:
        def path = new URI('file:///C:/waman/gluino/').toPath()
        
        then:
        path == Paths.get(new URI('file:///C:/waman/gluino/'))
    }
}