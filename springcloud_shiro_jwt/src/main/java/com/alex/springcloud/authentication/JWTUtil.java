package com.alex.springcloud.authentication;

import com.alex.springcloud.properties.SystemProperties;
import com.alex.springcloud.utils.SpringContextUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @description: jwt工具类
 * @author:      alex
 * @createTime:  2020/11/13 15:03
 * @version:     1.0
 */
public class JWTUtil {

    private static Logger log = LoggerFactory.getLogger(JWTUtil.class);

    private static final long EXPIRE_TIME = SpringContextUtil.getBean(SystemProperties.class).getJwtTimeOut() * 1000;

    /**
     * @description:  验证token是否正确
     * @author:       alex
     * @param:        token token
     * @param:        username 用户名
     * @param:        secret 用户密码
     * @return:
     */
    public static boolean verify(String token, String username, String secret) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            verifier.verify(token);
            log.info("token is valid");
            return true;
        } catch (Exception e) {
            log.info("token is invalid, error:{}", e.getMessage());
            return false;
        }
    }

    /**
     * @description:  从token中获取用户名
     * @author:       alex
     * @param:        token
     * @return:       username
     */
    public static String getUsername(String token) {
        try{
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (Exception e) {
            log.error("error:{}", e);
            return null;
        }
    }

    /**
     * @description:  生成token
     * @author:       alex
     * @param:
     * @return:
     */
    public static String sign(String username, String secret) {
        try{
            username = StringUtils.lowerCase(username);
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withClaim("username", username)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("error:{}", e);
            return null;
        }
    }
}
