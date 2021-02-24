package com.alex.mcgp.job;

import com.aliyun.odps.Instance;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.TableSchema;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.data.RecordReader;
import com.aliyun.odps.task.SQLTask;
import com.aliyun.odps.tunnel.TableTunnel;
import com.aliyun.odps.tunnel.TunnelException;

import java.io.IOException;
import java.util.UUID;

public class DownloadMcToGp {

    private static final String accessId = "b4A50gIMQQ9gFsB8";
    private static final String accessKey = "yAPrlxsCLfOgx40NJQgKGTIF9wJM4G";
    private static final String endPoint = "http://service.cn-chongqing-cqdxz-d01.odps.ops.swy.cncqsw.com:80/api";
    private static final String project = "PRJ_201_DW_DEV";
    private static final String sql = "select * from dim_201_org_5mmf where ds = (select max(ds) from dim_201_org_5mmf);";
    private static final String table = "Tmp_" + UUID.randomUUID().toString().replace("-", "_");//此处使用随机字符串作为临时导出存放数据的表的名字。
    private static final Odps odps = getOdps();
    public static void main(String[] args) {
        System.out.println(table);
        runSql();
        tunnel();
    }
    /*
     * 下载SQLTask的结果。
     * */
    private static void tunnel() {
        TableTunnel tunnel = new TableTunnel(odps);
        try {
            TableTunnel.DownloadSession downloadSession = tunnel.createDownloadSession(project, table);
            System.out.println("Session Status is : "+ downloadSession.getStatus().toString());
            long count = downloadSession.getRecordCount();
            System.out.println("RecordCount is: " + count);
            RecordReader recordReader = downloadSession.openRecordReader(0, count);
            Record record;
            while ((record = recordReader.read()) != null) {
                consumeRecord(record, downloadSession.getSchema());
            }
            recordReader.close();
        } catch (TunnelException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    /*
     * 保存这条数据。
     * 如果数据量少，可以直接打印结果后拷贝。实际使用场景下也可以使用JAVA.IO写到本地文件，或在远端服务器上保存数据结果。
     * */
    private static void consumeRecord(Record record, TableSchema schema) {
        System.out.println(record.getString("id"));
    }
    /*
     * 运行SQL ,把查询结果保存成临时表，方便后续用Tunnel下载。
     * 保存数据的lifecycle此处设置为1天，如果删除步骤失败，也不会浪费过多存储空间。
     * */
    private static void runSql() {
        Instance i;
        StringBuilder sb = new StringBuilder("Create Table ").append(table)
                .append(" lifecycle 1 as ").append(sql);
        try {
            System.out.println(sb.toString());
            i = SQLTask.run(getOdps(), sb.toString());
            i.waitForSuccess();
        } catch (OdpsException e) {
            e.printStackTrace();
        }
    }
    /*
     * 初始化MaxCompute的连接信息。
     * */
    private static Odps getOdps() {
        Account account = new AliyunAccount(accessId, accessKey);
        Odps odps = new Odps(account);
        odps.setEndpoint(endPoint);
        odps.setDefaultProject(project);
        return odps;
    }
}
