package org.superbiz.dl4j.ex04;

import org.datavec.api.conf.Configuration;
import org.datavec.api.records.Record;
import org.datavec.api.records.listener.RecordListener;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.InputSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.writable.Writable;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

class MyRecordReader implements RecordReader {
    private final RecordReader parentReader;
    private final TransformProcess process;

    public MyRecordReader(RecordReader parentReader, TransformProcess process) {
        this.parentReader = parentReader;
        this.process = process;
    }

    @Override
    public void setConf(Configuration conf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Configuration getConf() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }

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
        return parentReader.batchesSupported();
    }

    @Override
    public List<Writable> next(int num) {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public List<Writable> next() {
        List<Writable> original = parentReader.next();
        List<Writable> result = this.process.execute(original);
        return result;
    }

    @Override
    public boolean hasNext() {
        return parentReader.hasNext();
    }

    @Override
    public List<String> getLabels() {
        //return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        parentReader.reset();
    }

    @Override
    public List<Writable> record(URI uri, DataInputStream dataInputStream) throws IOException {
        //return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Record nextRecord() {
        return parentReader.nextRecord();
    }

    @Override
    public Record loadFromMetaData(RecordMetaData recordMetaData) throws IOException {
        //return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Record> loadFromMetaData(List<RecordMetaData> recordMetaDatas) throws IOException {
        //return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public List<RecordListener> getListeners() {
        //return null;
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
}
