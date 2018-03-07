package org.flexdata.data;

public interface DataSet<T, U> {
    static <T, U> DataSet<T, U> ofList(DataRow<T, U>... list) {
        DataSet<T, U> dataSet = new InMemoryDataSet<>(list);
        return dataSet;
    }

}
