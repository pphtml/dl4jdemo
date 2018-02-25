package org.flexdata.csv;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CSVReader implements DataReader {
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    private final int skipLines;
    private final char delimiter;
    private final Resource resource;
    private final char quote;

    public CSVReader(int skipLines, char delimiter, Resource resource, char quote) {
        this.skipLines = skipLines;
        this.delimiter = delimiter;
        this.resource = resource;
        this.quote = quote;
    }

    @Override
    public void close() {

    }

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        iterable.iterator(),
                        Spliterator.ORDERED
                ),
                false
        );
    }

    @Override
    public List<DataRecord> readAllRecords() {
//        List<DataRecord> result = new ArrayList<>();
//        for (ResourceLine resourceLine : resource) {
//            List<String> columnData = parseLine(resourceLine.getLine());
//            result.add(new DataRecord(columnData));
//        }
//        return result;
        return stream(resource)
                .skip(skipLines)
                .map(resourceLine -> new DataRecord(parseLine(resourceLine.getLine())))
                .collect(Collectors.toList());
    }

    public List<String> parseLine(String cvsLine) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == this.quote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }
                }
            } else {
                if (ch == this.quote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && this.quote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == this.delimiter) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }
        }

        result.add(curVal.toString());

        return result;
    }

    public static final class CSVReaderBuilder {
        private int skipLines;
        private char delimiter = DEFAULT_SEPARATOR;
        private Resource resource;
        private char quote = DEFAULT_QUOTE;

        private CSVReaderBuilder() {
        }

        public static CSVReaderBuilder createCSVReader() {
            return new CSVReaderBuilder();
        }

        public CSVReaderBuilder withSkipLines(int skipLines) {
            this.skipLines = skipLines;
            return this;
        }

        public CSVReaderBuilder withDelimiter(char delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public CSVReaderBuilder withResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public CSVReaderBuilder withQuote(char quote) {
            this.quote = quote;
            return this;
        }

        public CSVReader build() {
            return new CSVReader(skipLines, delimiter, resource, quote);
        }
    }
}
