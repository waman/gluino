package org.waman.gluino.nio

import java.nio.file.*
import spock.lang.*

/**
 * Tested methods : static methods defined in java.nio.file.Files as category. 
 */
class FilesTest extends Specification{
    
    Path path
    File file
    
    def setup(){
        path = Files.createTempFile('nio', 'files')
        file = path.toFile()
        
        file.text = 'abcdefghijklmnopqrstuvwxyz'
    }
    
    //********** File Search **********
    def 'get file size'(){
        expect:
        path.size() == file.size()
    }
}
