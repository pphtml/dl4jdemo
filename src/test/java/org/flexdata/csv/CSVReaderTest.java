package org.flexdata.csv;

import org.junit.Test;

import static org.junit.Assert.*;

public class CSVReaderTest {

    @Test
    public void parseLine() {
        String csvString = "10,AU,Australia\n" +
                "11,AU,Aus\"\"tralia\n" +
                "\"12\",\"AU\",\"Australia\"\n" +
                "\"13\",\"AU\",\"Aus\"\"tralia\"\n" +
                "\"14\",\"AU\",\"Aus,tralia\"";
    }
}