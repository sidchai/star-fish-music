package com.sidchai.music.config.shiro;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    //3.ShiroFilterFactoryBean
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManage") DefaultWebSecurityManager securityManage) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        factoryBean.setSecurityManager(securityManage);

        //添加shiro的内置过滤器
        /*
            anon: 无需认证就可以访问
            authc: 必须认证了才能访问
            user: 必须拥有 记住我 功能才能使用
            perms: 拥有对某个资源的权限才能访问
            role: 拥有某个角色权限才能访问
         */

        //登录拦截
        Map<String, String> filterMap = new LinkedHashMap<>();

        //授权操作,正常情况下，没有授权会跳转到未授权页面
        filterMap.put("/static/**", "anon");
        filterMap.put("/music/user/login", "anon");
        filterMap.put("/music/user/register", "anon");
        filterMap.put("/music/my-song/**", "authc");

        factoryBean.setFilterChainDefinitionMap(filterMap);

        //设置登录请求跳转页面
        factoryBean.setLoginUrl("/music/auth/login");
        //设置未授权的请求跳转页面
        factoryBean.setUnauthorizedUrl("/noauth");

        return factoryBean;
    }

    //2.DefaultWebSecurityManager
    @Bean(name = "securityManage")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("adminRealm") AdminRealm adminRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联AdminRealm
        securityManager.setRealm(adminRealm);

        return securityManager;
    }

    //1.创建 realm 对象 , 需要自定义
    @Bean
    public AdminRealm adminRealm(@Qualifier("hashedCredentialsMatcher") CredentialsMatcher matcher) {
        AdminRealm adminRealm = new AdminRealm();
        adminRealm.setCredentialsMatcher(matcher);
        return adminRealm;
    }


    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("MD5"); //指定加密方式
        credentialsMatcher.setHashIterations(1024); //加密次数
        credentialsMatcher.setStoredCredentialsHexEncoded(true); //设置存储十六进制编码的凭证
        return credentialsMatcher;
    }
}
