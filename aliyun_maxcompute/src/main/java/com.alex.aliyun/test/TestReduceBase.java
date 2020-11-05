package com.alex.aliyun.test;

import com.aliyun.odps.data.Record;
import com.aliyun.odps.mapred.ReducerBase;

import java.io.IOException;
import java.util.Iterator;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/11/4 13:40
 * @version:     1.0
 */
public class TestReduceBase extends ReducerBase {

    public TestReduceBase() {
    }

    @Override
    public void setup(TaskContext context) throws IOException {
        super.setup(context);
    }

    @Override
    public void reduce(Record key, Iterator<Record> values, TaskContext context) throws IOException {
        super.reduce(key, values, context);
    }

    @Override
    public void cleanup(TaskContext context) throws IOException {
        super.cleanup(context);
    }

    @Override
    public void run(TaskContext context) throws IOException {
        super.run(context);
    }
}
