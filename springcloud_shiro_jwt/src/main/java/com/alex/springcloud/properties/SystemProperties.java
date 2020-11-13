package com.alex.springcloud.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/11/13 14:24
 * @version:     1.0
 */
@ConfigurationProperties(prefix = "system")
public class SystemProperties {

    //免认证url
    private String anonUrl;

    //token默认有效时间 1天
    private Long jwtTimeOut = 86400L;

    public String getAnonUrl() {
        return anonUrl;
    }

    public void setAnonUrl(String anonUrl) {
        this.anonUrl = anonUrl;
    }

    public Long getJwtTimeOut() {
        return jwtTimeOut;
    }

    public void setJwtTimeOut(Long jwtTimeOut) {
        this.jwtTimeOut = jwtTimeOut;
    }
}
