Gluino is an extension module library of Scala,
aim to add utility methods to
* java.io,
* nio,
* Date and Calender
* Java8 Date and Time API, and
* java.util.regex
similar to that of GDK (Groovy JDK).

## java.io

## java.nio
#### Standard Usage
Standard usage of Gluino Path extension is to mixin `GluinoPath` or
to import its member:
```
import org.waman.gluino.nio.GluinoPath
// or
// import org.waman.gluino.nio.Gluino._
// without mixining GluinoPath

class MyApp extends App with GluinoPath {

  path("path/to/some/file.txt").withWriter{ writer =>
    writer.write("first line.")
    writer.write("second line.")
    writer.write("third line.")
  }
}
```

## More for java.io and java.nio

- `GluinoIO` (`InputStream`/`OutputStream`, `Reader`/`Writer`)
  - `AppendableConverter` (`Outputtable`, `Writable`)
    - `GluinoFile` (`java.io.File`)
    - `GluinoPath` (`java.nio.file.Path`)

In addition, `GluinoPath` extends `AttributeConverter`
which implicitly converts file-attribute-specific object
such as `FileTime` or `PosixFilePermission`

## Date and Calender

## Java8 Date and Time API

## java.util.regex

