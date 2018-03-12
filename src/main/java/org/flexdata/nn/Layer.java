package org.flexdata.nn;

import org.flexdata.data.DataSet;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface Layer {
    static InputLayer fromDataSet(DataSet dataSet) {
        return InputLayer.fromDataSet(dataSet);
    }

    void setPreviousLayer(Layer previousLayer);

    int getNeuronCount();

    INDArray getOutput();
}
