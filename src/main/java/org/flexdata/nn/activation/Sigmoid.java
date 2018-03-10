package org.flexdata.nn.activation;

public class Sigmoid implements ActivationFunction {
    @Override
    public double call(double value) {
        return 1 / (1 + Math.exp(-value));
    }

    @Override
    public double derivation(double value) {
        return value * (1 - value);
    }
}
