package com.sidchai.music.config.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.exception.MusicException;
import com.sidchai.music.pojo.Admin;
import com.sidchai.music.result.ResultCodeEnum;
import com.sidchai.music.service.AdminService;
import com.sidchai.music.service.RoleService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

//自定义的 UserRealm   extends AuthorizingRealm
public class AdminRealm extends AuthorizingRealm {

    @Autowired
    private AdminService adminService;
    @Autowired
    private RoleService roleService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        System.out.println("================");
        System.out.println("授权中............");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        //得到当前用户名
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();

        //设置当前用户的权限
        QueryWrapper<Admin> qw = new QueryWrapper<>();
        qw.eq("username", username);
        Admin currentAdmin = adminService.getOne(qw);
        Long roleId = currentAdmin.getRoleId();
        info.addStringPermission(roleService.getById(roleId).getRoleName());

        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        System.out.println("------------");
        System.out.println("认证中............");

        UsernamePasswordToken userToken = (UsernamePasswordToken) token;

        QueryWrapper<Admin> aw = new QueryWrapper<>();
        aw.eq("username", userToken.getUsername());

        //连接数据库 查询信息
        Admin admin = adminService.getOne(aw);

        if (admin == null) {  //没有找到
            throw new MusicException(ResultCodeEnum.LOGIN_MESSAGE_ERROR);
        }

        if(admin.getStatus() == StatusConstants.FREEZE) { //账号冻结
            throw new MusicException(ResultCodeEnum.LOGIN_FREEZE_ERROR);
        }

        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        session.setAttribute("loginAdmin", admin);
        ByteSource credentialsSalt = ByteSource.Util.bytes(admin.getUsername());
        //密码认证  shiro干
        return new SimpleAuthenticationInfo(admin, admin.getPassword(),credentialsSalt, "");
    }
}
