package org.waman.gluino.nio

import java.nio.file.*
import spock.lang.*

class ByteArrayAccessor extends Specification{

    def path = Files.createTempFile('gluino', 'byte-io')

    def setup(){
        Files.write(path, 'Groovy'.bytes)
    }

    def 'get bytes from path'(){
        when:
        def bytes = path.bytes

        then:
        bytes == 'Groovy'.bytes
    }
}