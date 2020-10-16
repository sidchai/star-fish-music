package com.sidchai.music.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.RedisConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.pojo.Admin;
import com.sidchai.music.pojo.Recycle;
import com.sidchai.music.pojo.User;
import com.sidchai.music.result.R;
import com.sidchai.music.service.AdminService;
import com.sidchai.music.service.RecycleService;
import com.sidchai.music.service.RoleService;
import com.sidchai.music.service.UserService;
import com.sidchai.music.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 回收站表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-28
 */
@Api(description = "回收站管理")
@RestController
@RequestMapping("/music/recycle")
public class RecycleController {

    @Autowired
    private RecycleService recycleService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("把删除的用户信息与管理员信息加入回收站")
    @PostMapping("/add-recycle")
    public R addRecycle(@ApiParam(name = "recycle", value = "已删除的用户(管理员)信息", required = true) @RequestBody Recycle recycle) {

        recycle.setStatus(StatusConstants.DISABLED);
        recycleService.save(recycle);
        return R.ok();
    }

    @ApiOperation("获取回收站中所有用户和管理员信息")
    @GetMapping("/get-list/{type}")
    public R getRecycleList(@ApiParam(name = "type", value = "回收站类型（管理员/用户）") @PathVariable Long type) {
        QueryWrapper<Recycle> wrapper = new QueryWrapper<>();
        if(type == 0) {
            wrapper.isNull(SQLConstants.USER_ID);
        } else {
            wrapper.isNull(SQLConstants.ADMIN_ID);
        }
        List<Recycle> recycles = recycleService.list(wrapper);
        for (Recycle recycle : recycles) {
            if (!StringUtils.isEmpty(recycle.getAdminId())) {
                // 通过当前管理员id 查询 管理员信息 并存入进去
                Admin adminById = adminService.getAdminById(recycle.getAdminId());
                recycle.setAdmin(adminById);
                // 通过roleId 查询 角色信息 并存入进去
                adminById.setRole(roleService.getById(adminById.getRoleId()));
            }
            if (!StringUtils.isEmpty(recycle.getUserId())) {
                User userById = userService.getUserById(recycle.getUserId());
                recycle.setUser(userById);
            }
        }
        return R.ok().data(BaseConstants.RECYCLE_LIST,recycles);
    }

    @ApiOperation("恢复 把信息从回收站中移除")
    @DeleteMapping("/restore-message")
    public R restoreMessage(@ApiParam(name = "recycle", value = "已删除的用户(管理员)信息", required = true)
                            @RequestBody Recycle recycle) {

        Admin admin = recycle.getAdmin();
        // 删除数据库中信息
        recycleService.removeById(recycle.getId());
        // 更改用户状态
        admin.setDeleted(StatusConstants.DISABLED);
        admin.setStatus(StatusConstants.ENABLE);
        adminService.updateAdmin(admin);

        User user = recycle.getUser();
        // 删除数据库中信息
        recycleService.removeById(recycle.getId());

        user.setDeleted(StatusConstants.DISABLED);
        user.setStatus(StatusConstants.ENABLE);
        userService.updateUserById(user);

        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_ADMIN_LIST_ALL + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);

        return R.ok().message("恢复成功");
    }
}

