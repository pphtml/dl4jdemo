package org.superbiz.dl4j.ex04;

import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.datavec.api.writable.WritableType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ColumnWrapper extends ArrayList<Writable> {
    private Schema schema;
    private List<Writable> record;
//    @Override
//    public void write(DataOutput out) throws IOException {
//
//    }
//
//    @Override
//    public void readFields(DataInput in) throws IOException {
//
//    }
//
//    @Override
//    public void writeType(DataOutput out) throws IOException {
//
//    }
//
//    @Override
//    public double toDouble() {
//        return 0;
//    }
//
//    @Override
//    public float toFloat() {
//        return 0;
//    }
//
//    @Override
//    public int toInt() {
//        return 0;
//    }
//
//    @Override
//    public long toLong() {
//        return 0;
//    }
//
//    @Override
//    public WritableType getType() {
//        return null;
//    }

    public static List<Writable> one(Schema schema, List<Writable> record) {
        ColumnWrapper result = new ColumnWrapper();
        result.schema = schema;
        result.record = record;
        result.addColumns();
        return result;
    }

    private void addColumns() {
        //for (String columnName : schema.getColumnNames()) {
        for (int index = 0; index < schema.getColumnNames().size(); index++) {
            final Writable data = this.record.get(index);
            this.add(new Writable() {
                @Override
                public void write(DataOutput out) throws IOException {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void readFields(DataInput in) throws IOException {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void writeType(DataOutput out) throws IOException {
                    throw new UnsupportedOperationException();
                }

                @Override
                public double toDouble() {
                    throw new UnsupportedOperationException();
                    //return 0;
                }

                @Override
                public float toFloat() {
                    throw new UnsupportedOperationException();
                    //return 0;
                }

                @Override
                public int toInt() {
                    throw new UnsupportedOperationException();
                    //return 0;
                }

                @Override
                public long toLong() {
                    throw new UnsupportedOperationException();
                    //return 0;
                }

                @Override
                public WritableType getType() {
                    throw new UnsupportedOperationException();
                    //return null;
                }
            });
        }
    }
}
