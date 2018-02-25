package org.flexdata.csv;

import org.datavec.api.util.ClassPathResource;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ClasspathResource implements Resource {
    private String fileInClasspath;

    public static Resource of(String fileInClasspath) {
        ClasspathResource result = new ClasspathResource();
        result.fileInClasspath = fileInClasspath;
        //System.out.println(getURL(fileInClasspath));

        return result;
    }

    private URL getUrl() {
        ClassLoader loader = null;
        try {
            loader = Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {
            // do nothing
        }

        if (loader == null) {
            loader = ClasspathResource.class.getClassLoader();
        }

        URL url = loader.getResource(this.fileInClasspath);
        return url;
    }

    public InputStream getInputStream() {
        URL url = getUrl();
        File file;
        try {
            URI uri = new URI(url.toString().replaceAll(" ", "%20"));
            file = new File(uri.getSchemeSpecificPart());
        } catch (URISyntaxException e) {
            file = new File(url.getFile());
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterator<ResourceLine> iterator() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getInputStream()));
        return new Iterator<ResourceLine>(){
            @Override
            public boolean hasNext() {
                try {
                    return bufferedReader.ready();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public ResourceLine next() {
                try {
                    return new ResourceLine(bufferedReader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public void forEach(Consumer<? super ResourceLine> action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Spliterator<ResourceLine> spliterator() {
        throw new UnsupportedOperationException();
    }
}
