package com.alex.aliyun.test;

import com.aliyun.odps.data.Record;
import com.aliyun.odps.mapred.MapperBase;
import java.io.IOException;

/**
 * @description: 
 * @author:      alex
 * @createTime:  2020/11/4 13:40 
 * @version:     1.0
 */
public class TestMaxCompute extends MapperBase {

    @Override
    public void setup(TaskContext context) throws IOException {
        super.setup(context);
    }

    @Override
    public void map(long key, Record record, TaskContext context) throws IOException {
        super.map(key, record, context);
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
