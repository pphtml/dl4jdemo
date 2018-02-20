package org.superbiz.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Utils {
    public static String readResourceToString(Class<?> clazz, String resourceName) {
        try {
            Path path = Paths.get(clazz.getResource(resourceName).toURI());
            StringBuilder data = new StringBuilder();
            Stream<String> lines = Files.lines(path);
            lines.forEach(line -> data.append(line).append("\n"));
            lines.close();
            return data.toString();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
