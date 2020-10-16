package com.sidchai.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.User;
import com.sidchai.music.pojo.vo.LoginVo;
import com.sidchai.music.pojo.vo.RegisterVo;
import com.sidchai.music.utils.JwtInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface UserService extends IService<User> {

    /**
     *  根据id删除用户头像
     * @param id
     * @return
     */
    boolean removerAvatar(Long id);

    /**
     *  自定义根据id查询相关管理员
     * @param id
     * @return
     */
    User getUserById(Long id);

    /**
     *  自定义修改用户信息
     * @param user
     * @return
     */
    boolean updateUserById(User user);

    /**
     *  用户注册
     * @param registerVo
     */
    void register(RegisterVo registerVo);

    /**
     *  用户登录
     * @param loginVo
     * @return
     */
    JwtInfo login(LoginVo loginVo, HttpServletRequest req);
}
