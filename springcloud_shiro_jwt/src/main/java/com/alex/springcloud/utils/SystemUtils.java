package com.alex.springcloud.utils;

import com.alex.springcloud.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 系统工具类
 *
 * @author MrBird
 */
public class SystemUtils {

    private static Logger log = LoggerFactory.getLogger(SystemUtils.class);

    /**
     * 模拟两个用户
     *
     * @return List<User>
     */
    private static List<User> users() {
        List<User> users = new ArrayList<>();
        // 模拟两个用户：
        // 1. 用户名 admin，密码 123456，角色 admin（管理员），权限 "user:add"，"user:view"
        // 1. 用户名 scott，密码 123456，角色 regist（注册用户），权限 "user:view"
        users.add(new User(
                "admin",
                "baa38bf9dc3ed7699c56253998bdd22d",
                new HashSet<>(Collections.singletonList("admin")),
                new HashSet<>(Arrays.asList("user:add", "user:view"))));
        users.add(new User(
                "scott",
                "7a2c7d2cb3750b8b9a61892eb27690eb",
                new HashSet<>(Collections.singletonList("regist")),
                new HashSet<>(Collections.singletonList("user:view"))));
        return users;
    }

    /**
     * 获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    public static User getUser(String username) {
        List<User> users = SystemUtils.users();
        return users.stream().filter(user -> StringUtils.equalsIgnoreCase(username, user.getUsername())).findFirst().orElse(null);
    }

}
