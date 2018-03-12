package org.flexdata.nn.activation;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

public class Relu implements ActivationFunction {
    @Override
    public INDArray call(INDArray values) {
        return Transforms.relu(values);
    }

    @Override
    public INDArray derivation(INDArray values) {
        throw new UnsupportedOperationException();
    }
}
