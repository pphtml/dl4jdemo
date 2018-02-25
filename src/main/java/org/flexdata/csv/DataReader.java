package org.flexdata.csv;

import java.util.List;

public interface DataReader extends AutoCloseable {
    void close();

    List<DataRecord> readAllRecords();
}
