package org.waman.gluino.nio

import java.nio.file.*
import spock.lang.*

/**
 * tested methods : 
 * <ul>
 *   <li>getBytes() : byte[]</li>
 *   <li>eachByte(Closure) : void</li>
 *   <li>eachByte(int, Closure) : void</li>
 *   <li>setBytes(byte[]) : void</li>
 *   <li>append(byte) : void</li>
 *   <li>leftShift(byte) : Path</li>
 * </ul>
 */
class ByteArrayAccessor extends Specification{

    def testDir = Files.createTempDirectory('gluino')
    def path_get

    def setup(){
        path_get = Files.createTempFile(testDir, '', 'byte-get')
        Files.write(path_get, 'Groovy'.bytes)
    }

    def 'get bytes from path'(){
        when:
        def bytes = path_get.bytes

        then:
        bytes == 'Groovy'.bytes
    }
/*
    def 'set bytes to path'(){
        when:
        def pathSet = createTempFile('byte-set')

        then:
        
    }

    def cleanup(){

    }*/
}