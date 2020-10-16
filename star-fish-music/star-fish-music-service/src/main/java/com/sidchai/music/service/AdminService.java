package com.sidchai.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.Admin;
import com.sidchai.music.pojo.vo.LoginVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 管理员表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface AdminService extends IService<Admin> {
    // 根据name查询相关管理员
    Admin getAdminByName(String username);
    // 解析结果
    Boolean parseContent(String keyword) throws IOException;
    //
    List<Map<String, Object>> searchPage(String keyword, Integer currentPage, Integer pageSize) throws IOException;
    // 根据id查询相关管理员
    Admin getAdminById(Long id);
    // 自定义修改管理员信息
    Boolean updateAdmin(Admin admin);
    // 管理员登录
    String login(LoginVo loginVo, String ip);
    // 根据id删除管理员头像
    boolean removerAvatar(Long id);
}
