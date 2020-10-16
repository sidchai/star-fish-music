package com.sidchai.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.Role;
import com.sidchai.music.pojo.vo.RoleVo;
import com.sidchai.music.result.R;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface RoleService extends IService<Role> {

    // 根据角色名查询角色信息
    Role getRoleByName(String roleName);

    /**
     * 获取角色列表
     * @param roleVo
     * @return
     */
    IPage<Role> getPageList(RoleVo roleVo);

    /**
     *  新增角色
     * @param roleVo
     * @return
     */
    R addRole(RoleVo roleVo);

    /**
     *  编辑角色
     * @param roleVo
     * @return
     */
    R editRole(RoleVo roleVo);

    /**
     *  删除角色
     * @param id
     * @return
     */
    R deleteRole(Long id);
}
