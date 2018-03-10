package org.flexdata.nn;

import org.flexdata.data.DataSet;

import java.util.List;

public interface Layer {
    static InputLayer fromDataSet(DataSet dataSet) {
        return InputLayer.fromDataSet(dataSet);
    }

    void setPreviousLayer(Layer previousLayer);

    int getNeuronCount();

    double[] evaluate(double[] values);
}
