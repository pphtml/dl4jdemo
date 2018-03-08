package org.flexdata.data;

public class InMemoryDataSet<T extends Number, U> implements DataSet<T, U> {
    private final DataRow<T, U>[] records;

    InMemoryDataSet(DataRow<T, U>[] records) {
        this.records = records;
    }

    @Override
    public DataRow getHeadRecord() {
        if (records.length == 0) {
            throw new EmptyDataSetException("DataSet is empty.");
        } else {
            return this.records[0];
        }
    }
}
