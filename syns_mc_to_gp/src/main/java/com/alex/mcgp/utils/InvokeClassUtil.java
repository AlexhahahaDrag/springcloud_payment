package com.alex.mcgp.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class InvokeClassUtil {

    public static <T> List<T> invoke(ResultSet rset, ResultSetMetaData data,  Class<T> cls) throws Exception {
        List<T> list=new ArrayList<>();
        int count = data.getColumnCount();
        T t = null;
        while (rset.next()) {
            t = cls.newInstance();
            for (int i = 1; i <= count ; i++) {
                String name = data.getColumnName(i);
                Field field = cls.getDeclaredField(name);
                field.setAccessible(true);
                field.set(t, rset.getObject(name));
            }
            list.add(t);
        }
        return list;
    }
}
