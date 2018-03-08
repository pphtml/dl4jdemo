package org.flexdata.nn;

import org.flexdata.data.DataSet;
import org.flexdata.data.Labels;

import java.util.List;

public interface Layer {
    static InputLayer fromDataSet(DataSet dataSet) {
        return InputLayer.fromDataSet(dataSet);
    }

    void setPreviousLayer(Layer previousLayer);

    int getNeuronCount();

    List<Double> evaluate(List<Double> values);
}
