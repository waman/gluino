Gluino is an extension module library of Groovy, aim to add utility methods to java.nio.file.Path interface, similar to that GDK add to java.io.File class.

This project is Groovy extension module,so **automatically** adds methods to already existing classes and interfaces (even Java standard API).

#### Path

##### Create Path object
To create a Path object, `Paths#get()` methods are usually used.
Gluino add `toPath()` methods for creating Path object to the following types:
*`java.lang.String`
*`java.lang.String[]`
*`java.util.List` with element type `Path` or `String`
*`java.net.URI`

##### Path Operations

##### File Operations

#### PathMatcher

