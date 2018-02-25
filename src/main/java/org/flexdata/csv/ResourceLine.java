package org.flexdata.csv;

public class ResourceLine {
    private final String line;

    public ResourceLine(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "ResourceLine{" +
                "line='" + line + '\'' +
                '}';
    }
}
