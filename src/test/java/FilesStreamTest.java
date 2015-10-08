import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.stream.Stream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.util.Arrays.asList;

public class FilesStreamTest {

    public static void main(String... args)throws IOException {
        Path path = Files.createTempFile(null, null);
        Files.write(path, asList("first line.", "second line.", "third line."));

        // Files#lines(Path)
        try(Stream<String> lines = Files.lines(path)){
            lines.forEach(System.out::println);
        }
        System.out.println();

        // Files#lines(Path, Charset)
        try(Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)){
            lines.forEach(System.out::println);
        }
        System.out.println();

        // Files#list(Path)
        try(Stream<Path> paths = Files.list(Paths.get("."))){
            paths.map(Path::toAbsolutePath).forEach(System.out::println);
        }
        System.out.println();

        // Files#find
        try(Stream<Path> paths = Files.find(
                Paths.get("."),
                Integer.MAX_VALUE,
                (p, atts) -> p.getFileName().toString().endsWith(".scala"))){
            paths.map(Path::toAbsolutePath).forEach(System.out::println);
        }
        System.out.println();

        // Files#wald
        try(Stream<Path> paths = Files.walk(Paths.get("."))){
            paths.filter(f -> Files.isDirectory(f))
                    .map(Path::toAbsolutePath)
                    .map(s -> "[DIRECTORY] " + s).forEach(System.out::println);
        }
    }
}
