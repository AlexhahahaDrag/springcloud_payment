package com.alex.springcloud.authentication;

import com.alex.springcloud.domain.User;
import com.alex.springcloud.utils.SystemUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/11/13 14:52
 * @version:     1.0
 */
public class ShiroRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * @description:  授权模块，获取用户角色和权限
     * @author:       alex
     * @param:        token token
     * @return:       AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection token) {
        String username = JwtUtil.getUsername(token.toString());
        User user = SystemUtils.getUser(username);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        //获取用户角色集
        simpleAuthorizationInfo.setRoles(user.getRole());
        //获取用户权限集
        simpleAuthorizationInfo.setStringPermissions(user.getPermission());
        return simpleAuthorizationInfo;
    }

    /**
     * @description:  用户认证
     * @author:       alex
     * @param:        authenticationToken 身份认证 token
     * @return:       AuthenticationInfo 身份认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        String username = JwtUtil.getUsername(token);
        if (StringUtils.isBlank(username)) {
            throw new AuthenticationException("token校验不通过");
        }
        //通过用户名查询用户信息
        User user = SystemUtils.getUser(username);
        if (user == null) {
            throw new AuthenticationException("用户名和密码错误");
        }
        if (!JwtUtil.verify(token, username, user.getPassword())) {
            throw new AuthenticationException("token校验不通过");
        }
        return new SimpleAuthenticationInfo(token, token, "shiro_realm");
    }
}
