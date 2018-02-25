package org.flexdata.transform;

import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;

public enum DataType {
    INTEGER {
        @Override
        public Writable convertToWritable(String value) {
            return new IntWritable(Integer.valueOf(value));
        }
    }, DOUBLE {
        @Override
        public Writable convertToWritable(String value) {
            return new DoubleWritable(Double.valueOf(value));
        }
    };

    public abstract Writable convertToWritable(String value);
}
