package org.flexdata.nn.initialization;

import java.util.Random;

public class UniformDistribution implements Distribution {
    private final double lowerBound;
    private final double upperBound;

    public UniformDistribution(double lowerBound, double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public void init() {
    }

    @Override
    public float next(Random random) {
        float value = random.nextFloat();
        return (float) (value * upperBound + (1 - value) * lowerBound);
    }
}
