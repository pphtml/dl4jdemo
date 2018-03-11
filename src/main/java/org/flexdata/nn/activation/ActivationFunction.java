package org.flexdata.nn.activation;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface ActivationFunction {
    INDArray call(INDArray values);

    INDArray derivation(INDArray values);
}
