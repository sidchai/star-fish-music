package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.MessageConstants;
import com.sidchai.music.constants.RedisConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.mapper.RoleMapper;
import com.sidchai.music.pojo.Admin;
import com.sidchai.music.pojo.Role;
import com.sidchai.music.pojo.vo.RoleVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.AdminService;
import com.sidchai.music.service.RoleService;
import com.sidchai.music.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Role getRoleByName(String roleName) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq(SQLConstants.ROLE_NAME, roleName);
        return roleService.getOne(wrapper);
    }

    @Override
    public IPage<Role> getPageList(RoleVo roleVo) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(roleVo.getKeyword()) && !StringUtils.isEmpty(roleVo.getKeyword().trim())) {
            queryWrapper.like(SQLConstants.ROLE_NAME, roleVo.getKeyword().trim());
        }
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        Page<Role> page = new Page<>();
        page.setCurrent(roleVo.getCurrentPage());
        page.setSize(roleVo.getPageSize());
        IPage<Role> pageList = roleService.page(page, queryWrapper);
        return pageList;
    }

    @Override
    public R addRole(RoleVo roleVo) {
        String roleName = roleVo.getRoleName();
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.ROLE_NAME, roleName);
        Role getRole = roleService.getOne(queryWrapper);
        if (getRole == null) {
            Role role = new Role();
            role.setRoleName(roleVo.getRoleName());
            role.setCategoryMenuIds(roleVo.getCategoryMenuIds());
            role.setIntroduce(roleVo.getIntroduce());
            roleService.save(role);
            return R.ok().message("添加成功!");
        }
        return R.error().message("该实体已存在");
    }

    @Override
    public R editRole(RoleVo roleVo) {
        Long id = roleVo.getId();
        Role getRole = roleService.getById(id);
        if (getRole == null) {
            return R.error().message("参数有误");
        }
        getRole.setRoleName(roleVo.getRoleName());
        getRole.setCategoryMenuIds(roleVo.getCategoryMenuIds());
        getRole.setIntroduce(roleVo.getIntroduce());
        roleService.updateById(getRole);

        // 修改成功后，需要删除redis中所有的admin访问路径
        deleteAdminVisitUrl();

        return R.ok().message("修改成功");
    }

    @Override
    public R deleteRole(Long id) {
        // 判断该角色下是否绑定了管理员
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.in(SQLConstants.ROLE_ID, id);
        Integer adminCount = adminService.count(queryWrapper);
        if(adminCount > 0) {
            return R.error().message(MessageConstants.ADMIN_UNDER_THIS_ROLE);
        }
        roleService.removeById(id);

        // 删除成功后，需要删除redis中所有的admin访问路径
        deleteAdminVisitUrl();

        return R.ok().message(MessageConstants.DELETE_SUCCESS);
    }

    /**
     * 删除Redis中管理员的访问路径
     */
    private void deleteAdminVisitUrl() {
        Set<String> keys = redisUtil.sGet(RedisConstants.ADMIN_VISIT_MENU + "*");
        redisUtil.delete(keys);
    }
}
