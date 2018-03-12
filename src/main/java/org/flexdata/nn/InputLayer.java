package org.flexdata.nn;

import org.flexdata.data.DataRow;
import org.flexdata.data.DataSet;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;

public class InputLayer implements Layer {
    private DataSet dataSet;
    private List<Double> params;

    @Override
    public void setPreviousLayer(Layer previousLayer) {
        throw new UnsupportedOperationException("Input layer cannot have any previous layer.");
    }

    @Override
    public int getNeuronCount() {
        DataRow headDataRow = dataSet.getHeadRecord();
        return headDataRow.getFeatures().count();
    }

    static InputLayer fromDataSet(DataSet dataSet) {
        InputLayer inputLayer = new InputLayer();
        inputLayer.dataSet = dataSet;
        return inputLayer;
    }

    public void setParams(List<Double> params) {
        this.params = params;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    @Override
    public INDArray getOutput() {
        // TODO udelat caching
        return dataSet.getFeatures();
    }
}
