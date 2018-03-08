package org.flexdata.data;

public interface DataSet<T extends Number, U> {
    static <T extends Number, U> DataSet<T, U> ofList(DataRow<T, U>... list) {
        DataSet<T, U> dataSet = new InMemoryDataSet<>(list);
        return dataSet;
    }

    DataRow getHeadRecord();
}
