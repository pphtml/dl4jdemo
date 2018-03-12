package org.flexdata.nn.initialization;

import org.apache.commons.math3.util.FastMath;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Random;

public enum WeightInit {
    DISTRIBUTION {
        @Override
        public INDArray execute(int[] shape, int nIn, Distribution distribution, Random random) {
            if (distribution == null) {
                throw new IllegalArgumentException("For DISTRIBUTION Weight Initialization distribution descriptor must be provided.");
            }
            int totalCount = shape[0] * shape[1]; // TODO obecne
            float[] values = new float[totalCount];
            distribution.init();
            for (int index = 0; index < totalCount; index++) {
                values[index] = distribution.next(random);
            }
            return Nd4j.create(values,shape);
        }
    }, ZERO {
        @Override
        public INDArray execute(int[] shape, int nIn, Distribution distribution, Random random) {
            return Nd4j.zeros(shape);
        }
    }, RELU {
        @Override
        public INDArray execute(int[] shape, int nIn, Distribution distribution, Random random) {
            return Nd4j.randn('f', shape).muli(FastMath.sqrt(2.0 / nIn));
        }
    };

    public abstract INDArray execute(int[] shape, int nIn, Distribution distribution, Random random);
}