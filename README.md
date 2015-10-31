Gluino is an extension module library of Scala,
aim to add utility methods to
* java.io,
* nio,
* Date and Calender
* Java8 Date and Time API, and
* java.util.regex
similar to that of GDK (Groovy JDK).

## java.io

## nio
#### Standard Usage

```
import org.waman.gluino.nio.GluinoPath

class MyApp extends App with GluinoPath {
  path("path/to/some/file.txt").withWriter{ writer =>
    writer.write("first line.")
    writer.write("second line.")
    writer.write("third line.")
  }
}
```

## Date and Calender

## Java8 Date and Time API

## java.util.regex

