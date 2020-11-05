package com.alex.aliyun.test;


import com.aliyun.odps.Column;
import com.aliyun.odps.counter.Counter;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.data.TableInfo;
import com.aliyun.odps.data.VolumeInfo;
import com.aliyun.odps.mapred.Mapper;
import com.aliyun.odps.mapred.Reducer;
import com.aliyun.odps.mapred.TaskContext;
import com.aliyun.odps.mapred.TaskId;
import com.aliyun.odps.mapred.conf.JobConf;
import com.aliyun.odps.volume.FileSystem;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/11/4 13:48
 * @version:     1.0
 */
public class TestTaskContext implements TaskContext {

    @Override
    public TaskId getTaskID() {
        return null;
    }

    @Override
    public TableInfo[] getOutputTableInfo() throws IOException {
        return new TableInfo[0];
    }

    @Override
    public Record createOutputRecord() throws IOException {
        return null;
    }

    @Override
    public Record createOutputRecord(String s) throws IOException {
        return null;
    }

    @Override
    public Record createOutputKeyRecord() throws IOException {
        return null;
    }

    @Override
    public Record createOutputValueRecord() throws IOException {
        return null;
    }

    @Override
    public Record createMapOutputKeyRecord() throws IOException {
        return null;
    }

    @Override
    public Record createMapOutputValueRecord() throws IOException {
        return null;
    }

    @Override
    public BufferedInputStream readResourceFileAsStream(String s) throws IOException {
        return null;
    }

    @Override
    public Iterable<BufferedInputStream> readResourceArchiveAsStream(String s) throws IOException {
        return null;
    }

    @Override
    public Iterable<BufferedInputStream> readResourceArchiveAsStream(String s, String s1) throws IOException {
        return null;
    }

    @Override
    public Iterator<Record> readResourceTable(String s) throws IOException {
        return null;
    }

    @Override
    public Counter getCounter(Enum<?> anEnum) {
        return null;
    }

    @Override
    public Counter getCounter(String s, String s1) {
        return null;
    }

    @Override
    public void progress() {

    }

    @Override
    public void write(Record record) throws IOException {

    }

    @Override
    public void write(Record record, String s) throws IOException {

    }

    @Override
    public void write(Record record, Record record1) throws IOException {

    }

    @Override
    public VolumeInfo getInputVolumeInfo() throws IOException {
        return null;
    }

    @Override
    public VolumeInfo getInputVolumeInfo(String s) throws IOException {
        return null;
    }

    @Override
    public VolumeInfo getOutputVolumeInfo() throws IOException {
        return null;
    }

    @Override
    public VolumeInfo getOutputVolumeInfo(String s) throws IOException {
        return null;
    }

    @Override
    public FileSystem getInputVolumeFileSystem() throws IOException {
        return null;
    }

    @Override
    public FileSystem getInputVolumeFileSystem(String s) throws IOException {
        return null;
    }

    @Override
    public FileSystem getOutputVolumeFileSystem() throws IOException {
        return null;
    }

    @Override
    public FileSystem getOutputVolumeFileSystem(String s) throws IOException {
        return null;
    }

    @Override
    public JobConf getJobConf() {
        return null;
    }

    @Override
    public int getNumReduceTasks() {
        return 0;
    }

    @Override
    public Column[] getMapOutputKeySchema() {
        return new Column[0];
    }

    @Override
    public Column[] getMapOutputValueSchema() {
        return new Column[0];
    }

    @Override
    public Class<? extends Mapper> getMapperClass() throws ClassNotFoundException {
        return null;
    }

    @Override
    public Class<? extends Reducer> getCombinerClass() throws ClassNotFoundException {
        return null;
    }

    @Override
    public Class<? extends Reducer> getReducerClass() throws ClassNotFoundException {
        return null;
    }

    @Override
    public String[] getGroupingColumns() {
        return new String[0];
    }
}
