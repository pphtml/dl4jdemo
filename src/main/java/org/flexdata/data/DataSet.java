package org.flexdata.data;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface DataSet<T extends Number, U extends Number> extends Iterable<DataRow> {
    static <T extends Number, U extends Number> DataSet<T, U> ofList(DataRow<T, U>... list) {
        DataSet<T, U> dataSet = new InMemoryDataSet<>(list);
        return dataSet;
    }

    DataRow getHeadRecord();

    INDArray getFeatures();

    INDArray getLabels();

//         INDArray inputs = Nd4j.create(new float[]{0,0, 0,1, 1,0, 1,1},new int[]{4, 2});
//        INDArray labels = Nd4j.create(new float[]{1,0, 0,1, 0,1, 1,0},new int[]{4, 2});
//        DataSet ds = new DataSet(inputs, labels);
}
