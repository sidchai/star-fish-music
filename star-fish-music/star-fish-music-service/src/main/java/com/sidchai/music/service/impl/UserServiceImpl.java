package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.exception.MusicException;
import com.sidchai.music.mapper.UserMapper;
import com.sidchai.music.pojo.User;
import com.sidchai.music.pojo.vo.LoginVo;
import com.sidchai.music.pojo.vo.RegisterVo;
import com.sidchai.music.result.ResultCodeEnum;
import com.sidchai.music.service.UserService;
import com.sidchai.music.utils.*;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean removerAvatar(Long id) {

        User user = userService.getById(id);

        if (user != null) {
            String url = user.getAvatar();
            if(!StringUtils.isEmpty(url)) {
                ossUtil.removeFile(url);
                return true;
            }
        }

        return false;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    @Override
    public boolean updateUserById(User user) {
        return userMapper.updateUserById(user);
    }

    @Override
    public void register(RegisterVo registerVo) {
        // 校验参数
        String username = registerVo.getUsername();
        String phone = registerVo.getPhone();
        String password = registerVo.getPassword();
        String code = registerVo.getCode();

        if(StringUtils.isEmpty(phone)) {
            throw new MusicException(ResultCodeEnum.LOGIN_PHONE_ERROR);
        }

        if(StringUtils.isEmpty(username)
        || StringUtils.isEmpty(password)
        || StringUtils.isEmpty(code)) {
            throw new MusicException(ResultCodeEnum.PARAM_ERROR);
        }

        // 校验验证码
        String checkCode = (String) redisUtil.get(phone);

        if(!code.equals(checkCode)) {
            throw new MusicException(ResultCodeEnum.CODE_ERROR);
        }

        // 用户是否注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.PHONE, phone);
        Integer count = userService.count(queryWrapper);
        if(count > 0) {
            throw new MusicException(ResultCodeEnum.REGISTER_MOBLE_ERROR);
        }

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq(SQLConstants.USERNAME, username);
        User one = userService.getOne(wrapper);
        if (one != null) {
            throw new MusicException(ResultCodeEnum.REGISTER_USERNAME_ERROR);
        }

        // 对密码进行加密
        SimpleHash hash = new SimpleHash(BaseConstants.ALGORITHM_NAME, BaseConstants.DEFAULT_PASSWORD, ByteSource.Util.bytes(username), BaseConstants.HASH_ITERATIONS);

        // 注册
        User user = new User();
        user.setUsername(username);
        user.setPassword(hash.toString());
        user.setPhone(phone);
        user.setAvatar("https://sidchai.oss-cn-beijing.aliyuncs.com/data/img/userPic/af8b4af8-d88c-46b5-9f5b-16b483d9b5e41ab6c925-5d8b-4361-a40f-2616a8d49198.jpg");
        userService.save(user);
    }

    @Override
    public JwtInfo login(LoginVo loginVo, HttpServletRequest req) {

        // 校验：参数是否合法

        String username = loginVo.getUsername();
        String password = loginVo.getPassword();

        // 校验用户信息是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        boolean isEmail = CheckUtils.checkEmail(username);
        boolean isPhone = CheckUtils.checkPhoneNumber(username);

        if(isEmail) {
            queryWrapper.eq(SQLConstants.EMAIL, username);
        } else if(isPhone) {
            queryWrapper.eq(SQLConstants.PHONE, username);
        } else {
            queryWrapper.eq(SQLConstants.USERNAME, username);
        }

        User user = userService.getOne(queryWrapper);

        if (user == null) {
            throw new MusicException(ResultCodeEnum.LOGIN_USERNAME_ERROR);
        }

        // 校验密码是否正确

        SimpleHash hash = new SimpleHash(BaseConstants.ALGORITHM_NAME, password, ByteSource.Util.bytes(user.getUsername()), BaseConstants.HASH_ITERATIONS);
        if (!hash.toString().equals(user.getPassword())) {
            throw new MusicException(ResultCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        // 校验用户是否被禁用

        if (user.getStatus() == 0) { // 封禁
            throw new MusicException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        // 把信息存入数据库中
        String ip = IpUtils.getIpAddr(req);
        Map<String, String> userMap = IpUtils.getOsAndBrowserInfo(req);
        user.setBrowser(userMap.get(SQLConstants.BROWSER));
        user.setOs(userMap.get(SQLConstants.OS));
        user.setLastLoginIp(ip);
        user.setLastLoginTime(new Date());
        Integer count = user.getLoginCount() + 1;
        user.setLoginCount(count);
        userService.updateById(user);
        // 登录

        // 创建jwt信息对象
        JwtInfo info = new JwtInfo();
        // 设置信息
        info.setUsername(user.getUsername());
        info.setAvatar(user.getAvatar());
        info.setId(user.getId().toString());

        return info;
    }
}
