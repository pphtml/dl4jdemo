package org.nd4j.examples;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static org.nd4j.linalg.ops.transforms.Transforms.*;

public class Nd4jExSoftmax_Functions {

    public static void main(String[] args) {

        //INDArray errors = Nd4j.create(new float[]{2.0f, 1.0f, 0.1f}, new int[]{3});
        //INDArray errors = Nd4j.create(new float[]{0.1f, 0.3f, 1.0f, 0.1f}, new int[]{4});
        //INDArray errors = Nd4j.create(new float[]{0.37f,  0.08f,  0.22f,  0.39f}, new int[]{4});
        INDArray errors = Nd4j.create(new float[]{0.37f,  0.08f,  0.22f,  0.39f}, new int[]{4});
        errors = errors.mul(4);
        INDArray ndv = softmax(errors);
        System.out.println(ndv);
    }
}
