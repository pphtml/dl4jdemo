package org.flexdata.nn.initialization;

import com.google.common.primitives.Doubles;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SequenceDistribution implements Distribution {
    private final List<Double> sequenceValues;
    private Iterator<Double> currentIterator;

    public SequenceDistribution(List<Double> sequenceValues) {
        this.sequenceValues = sequenceValues;
    }

    @Override
    public void init() {
        this.currentIterator = sequenceValues.iterator();
    }

    @Override
    public float next(Random random) {
        return currentIterator.next().floatValue();  // TODO cyclic iterator
    }

    public static SequenceDistribution of(double ...sequence) {
        return new SequenceDistribution(Doubles.asList(sequence));
    }
}
