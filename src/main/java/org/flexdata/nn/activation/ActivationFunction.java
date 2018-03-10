package org.flexdata.nn.activation;

public interface ActivationFunction {
    double call(double value);

    double derivation(double value);
}
