package org.waman.gluino.nio

import java.nio.file.*
import spock.lang.*

/**
 * Tested methods:
 * <ul>
 *   <li>Path#size() : long</li>
 * </ul>
 */
class FileInfoAccessorTest extends Specification{
    
    Path path
    File file
    
    def setup(){
        path = Files.createTempFile('nio', 'files')
        file = path.toFile()
        
        file.text = ('a'..'z').join()
    }
    
    def 'get file size'(){
        expect:
        path.size() == file.size()
    }
}
