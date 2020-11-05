package com.alex.aliyun.test;

import com.aliyun.odps.Instance;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.task.SQLTask;

import java.util.List;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/11/5 9:44
 * @version:     1.0
 */
public class TestSql {

    private static final String accessId = "";
    private static final String accessKey = "";
    private static final String endPoint = "http://service.odps.aliyun.com/api";
    private static final String project = "";
    private static final String sql = "select category from iris;";
    public static void main(String[] args) {
        Account account = new AliyunAccount(accessId, accessKey);
        Odps odps = new Odps(account);
        odps.setEndpoint(endPoint);
        odps.setDefaultProject(project);
        Instance i;
        try {
            i = SQLTask.run(odps, sql);
            i.waitForSuccess();
            List<Record> records = SQLTask.getResult(i);
            for(Record r:records){
                System.out.println(r.get(0).toString());
            }
        } catch (OdpsException e) {
            e.printStackTrace();
        }
    }
}
