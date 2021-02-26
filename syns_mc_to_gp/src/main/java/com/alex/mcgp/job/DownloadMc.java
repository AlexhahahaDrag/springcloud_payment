package com.alex.mcgp.job;

import com.alex.mcgp.entity.Dim201Org5mmf;
import com.alex.mcgp.utils.InvokeClassUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.List;

@Slf4j
public class DownloadMc {

    private static final String DRIVER_NAME = "com.aliyun.odps.jdbc.OdpsDriver";

    private static final String ACCESS_ID = "b4A50gIMQQ9gFsB8";
    private static final String ACCESS_KEY = "yAPrlxsCLfOgx40NJQgKGTIF9wJM4G";
    private static final String URL = "jdbc:odps:http://service.cn-chongqing-cqdxz-d01.odps.ops.swy.cncqsw.com:80/api?project=PRJ_201_DW_DEV";


    public static void main(String[] args) throws Exception {
        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Connection conn = DriverManager.getConnection(
                URL,
                ACCESS_ID, ACCESS_KEY);
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM dim_201_org_5mmf where ds = (select max(ds) from dim_201_org_5mmf)";
        stmt.executeQuery(sql);
        ResultSet rset = stmt.getResultSet();
        ResultSetMetaData data = rset.getMetaData();
        List<Dim201Org5mmf> list = InvokeClassUtil.invoke(rset, data, Dim201Org5mmf.class);
        System.out.println("search data");
    }
}
