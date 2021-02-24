package com.alex.mcgp.job;

import com.aliyun.odps.*;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.data.ArrayRecord;
import com.aliyun.odps.data.RecordWriter;
import com.aliyun.odps.task.SQLTask;
import com.aliyun.odps.tunnel.TableTunnel;

import java.io.IOException;

public class Test {
    private static final String accessId = "b4A50gIMQQ9gFsB8";
    private static final String accessKey = "yAPrlxsCLfOgx40NJQgKGTIF9wJM4G";
    private static final String endPoint = "http://service.cn-chongqing-cqdxz-d01.odps.ops.swy.cncqsw.com:80/api";
    private static final String project = "PRJ_201_DW_DEV";

    private static String table = "gaoju_table_sdk_mc";
    private static String partition = "dt='20210106'";


//    public static void main(String[] args) {
//        Account account = new AliyunAccount(accessId, accessKey);
//        Odps odps = new Odps(account);
//        odps.setEndpoint(endPoint);
//        odps.setDefaultProject(project);
//
//        try {
//            SQLTask.run(odps,"CREATE TABLE IF NOT EXISTS gaoju_table_sdk_mc(key STRING, value BIGINT) partitioned by (dt STRING);");
//            Table t = odps.tables().get(table);
//            PartitionSpec partitionSpec = new PartitionSpec(partition);
//            t.createPartition(partitionSpec,true);
//
//            TableTunnel tunnel = new TableTunnel(odps);
//            // ---------- Upload Data ---------------
//            // create upload session for table
//            // the table schema is {"col0": ARRAY<BIGINT>, "col1": MAP<STRING, BIGINT>, "col2": STRUCT<name:STRING,age:BIGINT>}
//            TableTunnel.UploadSession uploadSession = tunnel.createUploadSession(project, table, partitionSpec);
//            //TableTunnel.UploadSession uploadSession = tunnel.createUploadSession(project, table);
//            // get table schema
//            TableSchema schema = uploadSession.getSchema();
//
//            // open record writer
//            RecordWriter recordWriter = uploadSession.openRecordWriter(0);
//            ArrayRecord record = (ArrayRecord) uploadSession.newRecord();
//
//            // prepare data
//            record.setString("key","Lily");
//            record.setBigint("value",18L);
//
//            // write the record
//            recordWriter.write(record);
//
//            record.setString(0,"Lucy");
//            record.setBigint(1,19L);
//
//            // write the record
//            recordWriter.write(record);
//
//            // close writer
//            recordWriter.close();
//
//            // commit uploadSession, the upload finish
//            uploadSession.commit(new Long[]{0L});
//            System.out.println("upload success!");
//
//        } catch (OdpsException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
}
