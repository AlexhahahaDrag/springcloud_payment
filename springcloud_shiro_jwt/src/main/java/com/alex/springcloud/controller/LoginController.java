package com.alex.springcloud.controller;

import com.alex.springcloud.authentication.JWTUtil;
import com.alex.springcloud.domain.Response;
import com.alex.springcloud.domain.User;
import com.alex.springcloud.exception.SystemException;
import com.alex.springcloud.utils.MD5Util;
import com.alex.springcloud.utils.SystemUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: login controller
 * @author:      alex
 * @createTime:  2020/11/13 16:09
 * @version:     1.0
 */
@RestController
@Validated
public class LoginController {

    /**
     * @description:  登录
     * @author:       alex
     * @param:        username
     * @param:        password
     * @param:        request
     * @return:
     */
    @PostMapping("/login")
    public Response login(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          HttpServletRequest request) throws Exception{
        username = StringUtils.lowerCase(username);
        password = MD5Util.encrypt(username, password);
        User user = SystemUtils.getUser(username);
        final String errorMessage = "用户名或密码错误";
        if (user == null)
            throw new SystemException(errorMessage);
        if (!StringUtils.equals(user.getPassword(), password))
            throw new SystemException(errorMessage);
        //生成token
        String token = JWTUtil.sign(username, password);
        Map<String, Object> userInfo = this.generateUserInfo(token, user);
        return new Response().message("认证成功").data(userInfo);
    }

    /**
     * @description:  生成前端需要的用户信息
     * @author:       alex
     * @param:
     * @return:
     */
    private Map<String, Object> generateUserInfo(String token, User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("token", token);
        user.setPassword("it is a secret");
        userInfo.put("user", user);
        return userInfo;
    }
}
