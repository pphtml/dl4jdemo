package org.superbiz.dl4j.ex03.functions;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface MathFunction {

    INDArray getFunctionValues(INDArray x);

    String getName();
}
