package org.flexdata.util;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Utils {
    public static INDArray asINDArray(float[][] records) {
        int rows = records.length;
        if (records.length == 0) {
            throw new IllegalArgumentException("At least one row has to be provided.");
        }
        int columns = records[0].length;

        // //         INDArray inputs = Nd4j.create(new float[]{0,0, 0,1, 1,0, 1,1},new int[]{4, 2});
        float[] flattened = new float[rows * columns];
        for (int row = 0; row < rows; row++) {
            System.arraycopy(records[row], 0, flattened, row * columns, columns);
        }
        return Nd4j.create(flattened, new int[]{rows, columns});
    }
}
