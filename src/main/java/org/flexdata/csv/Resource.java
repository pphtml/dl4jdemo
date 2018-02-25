package org.flexdata.csv;

public interface Resource extends Iterable<ResourceLine> {
    static Resource fromClasspath(String fileInClasspath) {
        return ClasspathResource.of(fileInClasspath);
    }
}
