package org.flexdata.data;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class InMemoryDataSet<T extends Number, U extends Number> implements DataSet<T, U>, Iterable<DataRow> {
    private final DataRow<T, U>[] records;

    InMemoryDataSet(DataRow<T, U>[] records) {
        this.records = records;
    }

    @Override
    public DataRow getHeadRecord() {
        if (records.length == 0) {
            throw new EmptyDataSetException("DataSet is empty.");
        } else {
            return this.records[0];
        }
    }

    @Override
    public Iterator<DataRow> iterator() {
        return new Iterator<DataRow>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < records.length;
            }

            @Override
            public DataRow next() {
                return records[index++];
            }
        };
    }

    @Override
    public void forEach(Consumer<? super DataRow> action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Spliterator<DataRow> spliterator() {
        throw new UnsupportedOperationException();
    }
}
