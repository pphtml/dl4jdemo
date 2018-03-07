package org.flexdata.nn;

import org.flexdata.data.DataSet;

public interface Layer {
    static InputLayer fromDataset(DataSet dataSet) {
        return null;
    }
}
