package org.flexdata.transform;

import org.datavec.api.conf.Configuration;
import org.datavec.api.records.Record;
import org.datavec.api.records.listener.RecordListener;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.flexdata.csv.DataReader;
import org.flexdata.csv.DataRecord;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public class Transformator {
    public static DataSetIterator createDataSetIterator(DataReader dataReader, Schema schema) {
        List<DataRecord> inputDataRecords = dataReader.readAllRecords();

        List<Schema.Transformation> instructions = schema.getTransformationList();

        if (schema.isPreprocessingNeeded()) {
            for (DataRecord inputDataRecord : inputDataRecords) {
                preprocess(inputDataRecord, instructions);
            }

            schema.preprocess2();
        }

        List<List<Writable>> outputDataRecords = new ArrayList<>(inputDataRecords.size());
        for (DataRecord inputDataRecord : inputDataRecords) {
            outputDataRecords.add(transformLine(inputDataRecord, instructions));
        }


        RecordReader recordReader = new RecordReader() {
            private Iterator<List<Writable>> iterator;
            private Iterator<List<Writable>> secondaryPsychoIterator = outputDataRecords.iterator();

            @Override
            public void initialize(InputSplit split) throws IOException, InterruptedException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void initialize(Configuration conf, InputSplit split) throws IOException, InterruptedException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean batchesSupported() {
                return false;
            }

            @Override
            public List<Writable> next(int num) {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<Writable> next() {
                return iterator.next();
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public List<String> getLabels() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void reset() {
                this.iterator = outputDataRecords.iterator();
            }

            @Override
            public List<Writable> record(URI uri, DataInputStream dataInputStream) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public Record nextRecord() {
                return new Record() {
                    @Override
                    public List<Writable> getRecord() {
                        return secondaryPsychoIterator.next();
                    }

                    @Override
                    public void setRecord(List<Writable> record) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public RecordMetaData getMetaData() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void setMetaData(RecordMetaData recordMetaData) {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            public Record loadFromMetaData(RecordMetaData recordMetaData) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<Record> loadFromMetaData(List<RecordMetaData> recordMetaDatas) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<RecordListener> getListeners() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setListeners(RecordListener... listeners) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setListeners(Collection<RecordListener> listeners) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void close() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setConf(Configuration conf) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Configuration getConf() {
                throw new UnsupportedOperationException();
            }
        };

        DataSetIterator iterator = new RecordReaderDataSetIterator(
                recordReader, inputDataRecords.size(), schema.getFeaturesCountExpanded(), schema.getClassesCountExpanded());
//                recordReader, inputDataRecords.size(), schema.getFeaturesCount(), schema.getClassesCount());


        return iterator;
    }

    private static void preprocess(DataRecord inputDataRecord, List<Schema.Transformation> instructions) {
        for (Schema.Transformation instruction : instructions) {
            instruction.preprocess(inputDataRecord);
        }
    }

    private static List<Writable> transformLine(DataRecord inputDataRecord, List<Schema.Transformation> instructions) {
        List<Writable> result = new ArrayList<>();
        for (Schema.Transformation instruction : instructions) {
            List<Writable> writable = instruction.process(inputDataRecord);
            result.addAll(writable);
        }
        return result;
    }
}
