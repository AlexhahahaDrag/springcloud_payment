package com.alex.springcloud.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * @description: md5工具类
 * @author:      alex
 * @createTime:  2020/11/13 14:39
 * @version:     1.0
 */
public class MD5Util {

    protected MD5Util() {

    }

    private static final String ALGORITHM_NAME = "md5";

    private static final int HASH_ITERATIONS = 1;

    public static String encrypt(String password) {
        return new SimpleHash(ALGORITHM_NAME, password, ByteSource.Util.bytes(password), HASH_ITERATIONS).toHex();
    }

    public static String encrypt(String username, String password) {
        return new SimpleHash(ALGORITHM_NAME, password, ByteSource.Util.bytes(username.toLowerCase() + password), HASH_ITERATIONS).toHex();
    }

    public static void main(String[] args) {
        System.out.println(MD5Util.encrypt("alex", "123456"));
    }
}
