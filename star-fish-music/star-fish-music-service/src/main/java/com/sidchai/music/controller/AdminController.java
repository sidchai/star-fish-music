package com.sidchai.music.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sidchai.music.constants.*;
import com.sidchai.music.pojo.Admin;
import com.sidchai.music.pojo.Recycle;
import com.sidchai.music.pojo.Role;
import com.sidchai.music.result.R;
import com.sidchai.music.service.AdminService;
import com.sidchai.music.service.RecycleService;
import com.sidchai.music.service.RoleService;
import com.sidchai.music.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 管理员表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Api(description = "管理员管理")
@RestController
@RequestMapping("/music/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MailSendUtil mailSendUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RecycleService recycleService;

    @ApiOperation("获取所有管理员信息列表")
    @GetMapping("/get-list")
    public R list() {

        // 从redis中读取信息
        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_ADMIN_LIST_ALL + RedisConstants.SEGMENTATION + "*");
        // 如果取到数据直接返回
        if (!StringUtils.isEmpty(jsonResult)) {
            List<Admin> adminList = JsonUtils.jsonToList(jsonResult, Admin.class);
            return R.ok().data(BaseConstants.ADMIN_LIST, adminList);
        }

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);

        List<Admin> list = adminService.list(queryWrapper);
        for (Admin admin : list) {
            admin.setPassword(null); // 清除密码，防止泄露
            admin.setRole(roleService.getById(admin.getRoleId())); // 设置角色
        }

        // 把信息存入redis中
        redisUtil.setEx(RedisConstants.REDIS_ADMIN_LIST_ALL + RedisConstants.SEGMENTATION + "*", JsonUtils.objectToJson(list), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.ADMIN_LIST, list);
    }

    @ApiOperation("获取管理员列表分页信息")
    @GetMapping("/get-list/{currentPage}/{pageSize}")
    public R getAdminList(@ApiParam(name = "currentPage", value = "当前页数", required = true) @PathVariable("currentPage") Long currentPage,
                          @ApiParam(name = "pageSize", value = "每页显示数目", required = true) @PathVariable("pageSize") Long pageSize) {

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        // 创建分页对象
        Page<Admin> page = new Page<>();
        // 设置当前页数
        page.setCurrent(currentPage);
        // 设置每页显示的数目
        page.setSize(pageSize);

        if (queryWrapper != null) {
            // 清空密码
            queryWrapper.select(Admin.class, item -> !item.getProperty().equals(SQLConstants.PASSWORD));
        }
        IPage<Admin> pageList = adminService.page(page, queryWrapper);
        List<Admin> list = pageList.getRecords(); // 转成list类型
        list.forEach(System.out::println);

        for (Admin admin : list) {
            Role role = roleService.getById(admin.getRoleId());
            admin.setRole(role);
        }
        return R.ok().data(BaseConstants.ADMIN_PAGE_LIST, pageList);
    }

    @ApiOperation("获取当前登录用户的信息")
    @GetMapping("/get-login-admin-info")
    public R list(HttpServletRequest req) {

        // 得到当前登录用户信息
        JwtInfo info = JwtUtils.getMemberIdByJwtToken(req);
        Admin admin = adminService.getAdminByName(info.getUsername());
        return R.ok().data(BaseConstants.ADMIN_LOGIN_INFO, admin);
    }


    @ApiOperation("重置管理员密码")
    @PostMapping("/rest-pwd/{id}")
    public R restPwd(@ApiParam(name = "id", value = "管理员id", required = true) @PathVariable("id") Long id) {

        // 判断id是否为空和 ""
        if (StringUtils.isEmpty(id)) {
            return R.error().message("没有该管理员信息"); // 返回错误信息给前端
        }

        Admin admin = adminService.getById(id);
        // 对密码进行加盐加密
        SimpleHash hash = new SimpleHash(BaseConstants.ALGORITHM_NAME, BaseConstants.DEFAULT_PASSWORD, ByteSource.Util.bytes(admin.getUsername()), BaseConstants.HASH_ITERATIONS);
        admin.setPassword(hash.toString());

        // 存入数据库
        adminService.updateById(admin);

        // 删除redis中管理员信息
        deleteAdminInfo();

        //返回结果
        return R.ok().message("密码重置成功");
    }

    @ApiOperation("删除管理员信息")
    @DeleteMapping("/delete-admin/{id}")
    public R deleteAdmin(@ApiParam(name = "id", value = "管理员id", required = true) @PathVariable("id") Long id) {

        // 判断id是否为空和 ""
        if (StringUtils.isEmpty(id)) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        Admin admin = adminService.getById(id);

        if(admin == null) {
            return R.error().message(MessageConstants.ENTITY_NONENTITY);
        }

        // 如果用户名为admin，就不让删除
        if (BaseConstants.ADMIN.equalsIgnoreCase(admin.getUsername())) {
            return R.error().message("您没有权限删除该管理员");
        }

        QueryWrapper<Recycle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.ADMIN_ID, id);

        // 删除管理员头像文件
        adminService.removerAvatar(id);

        // 删除用户，实际是逻辑删除，可在回收站中查看
        admin.setStatus(StatusConstants.DISABLED); // 设置状态为删除

        if(adminService.removeById(id)) {
            // 删除redis中管理员信息
            deleteAdminInfo();

            // 删除回收站中信息
            recycleService.remove(queryWrapper);

            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @ApiOperation("批量删除管理员信息")
    @DeleteMapping("/delete-admins")
    public R deleteAdmins(@ApiParam(name = "ids", value = "管理员id集合", required = true) @RequestParam("ids") List<Long> ids) {

        // 判断ids 是否为空或""
        if (ids.isEmpty()) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        Collection<Admin> admins = adminService.listByIds(ids);
        for (Admin admin : admins) {
            // 如果用户名为admin，就不让删除
            if (BaseConstants.ADMIN.equalsIgnoreCase(admin.getUsername())) {
                return R.error().message("您没有权限删除该管理员");
            }
            admin.setStatus(StatusConstants.DISABLED); // 设置状态为删除
        }
        QueryWrapper<Recycle> queryWrapper = new QueryWrapper<>();
        // 删除数据库中信息
        if(adminService.removeByIds(ids)) {
            // 删除redis中管理员信息
            deleteAdminInfo();

            ids.forEach(item -> {
                queryWrapper.eq(SQLConstants.ADMIN_ID, item);
                recycleService.remove(queryWrapper);
            });

            return R.ok().message(MessageConstants.DELETE_BATCH_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_BATCH_FAIL);
        }
    }

    @ApiOperation("修改管理员信息")
    @PutMapping("/edit-admin")
    public R editAdmin(@ApiParam(name = "admin", value = "接收的管理员信息", required = true) @RequestBody(required = true) Admin admin,
                       @ApiParam(name = "req", value = "request对象,可以得到token") HttpServletRequest req) {

        // 得到token中保存的当前登录用户信息
        JwtInfo info = JwtUtils.getMemberIdByJwtToken(req);
        assert info != null;
        String username = info.getUsername();

        // 根据token 查询当前登录用户所拥有的角色
        Role role = roleService.getById(adminService.getAdminByName(username).getRoleId());

        // 根据要修改的管理员信息查询到所拥有的角色
        Role adminRole = roleService.getById(admin.getRoleId());

        // 判断要修改的是不是超级管理员
        if (BaseConstants.ROLE_NAME_BEST.equals(adminRole.getRoleName()) && BaseConstants.ROLE_NAME_BEST.equals(role.getRoleName())) {

            adminService.updateById(admin);

            // 删除redis中管理员信息
            deleteAdminInfo();

            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            if (!BaseConstants.ROLE_NAME_BEST.equals(adminRole.getRoleName())) {

                adminService.updateById(admin);

                // 删除redis中管理员信息
                deleteAdminInfo();

                return R.ok().message(MessageConstants.UPDATE_SUCCESS);
            } else {
                return R.error().message("您没有权限修改该用户");
            }
        }
    }

    @ApiOperation("添加管理员信息")
    @PostMapping("/add-admin")
    public R addAdmin(@ApiParam(name = "admin", value = "接收的管理员信息", required = true) @RequestBody(required = true) Admin admin,
                      @ApiParam(name = "req", value = "request对象,可以得到token") HttpServletRequest req) {

        // 得到添加用户名
        String username = admin.getUsername();
        // 得到添加用户邮箱
        String email = admin.getEmail();

        if (StringUtils.isEmpty(username)) {
            return R.error();
        }

        // 得到token中保存的当前登录用户信息
        JwtInfo info = JwtUtils.getMemberIdByJwtToken(req);
        String token = info.getUsername();

        // 根据token 查询当前登录用户所拥有的角色
        Role role = roleService.getById(adminService.getAdminByName(token).getRoleId());

        // 查询超级管理员对应的信息
        Role roleByName = roleService.getRoleByName(BaseConstants.ROLE_NAME_BEST);

        // 当前登录用户拥有最高权限
        if (BaseConstants.ROLE_NAME_BEST.equals(role.getRoleName())) {

            // 判断该管理员信息是否已存在

            // 删除redis中管理员信息
            deleteAdminInfo();

            return detachmentList(admin, username, email);
        } else { // 当前登录为一般管理员
            if (admin.getRoleId().equals(roleByName.getId())) { // 当前添加的为超级管理员
                return R.error().message("您没有权限添加超级管理员");
            } else {

                // 删除redis中管理员信息
                deleteAdminInfo();

                return detachmentList(admin, username, email);
            }
        }
    }

    @ApiOperation("改变管理员状态")
    @PutMapping("/change-status")
    public R changeStatus(@ApiParam(name = "admin", value = "当前管理员状态信息", required = true) @RequestBody Admin admin,
                          @ApiParam(name = "req", value = "request对象,可以得到token") HttpServletRequest req) {

        // 得到token中保存的当前登录用户信息
        JwtInfo info = JwtUtils.getMemberIdByJwtToken(req);
        String username = info.getUsername();

        // 根据token 中 的用户信息 查询当前登录用户所拥有的角色
        Role role = roleService.getById(adminService.getAdminByName(username).getRoleId());

        // 如果登录用户角色为演示角色
        if(BaseConstants.ROLE_NAME_DEMO.equals(role.getRoleName())) {
            return R.error().message("您没有权限对该用户进行状态管理");
        }

        // 根据要修改的管理员信息查询到所拥有的角色
        Role adminRole = roleService.getById(admin.getRoleId());

        QueryWrapper<Recycle> wrapper = new QueryWrapper<>();
        wrapper.eq(SQLConstants.ADMIN_ID, admin.getId());
        // 当前登录用户拥有最高权限，可以操作超级管理员
        if (BaseConstants.ROLE_NAME_BEST.equals(adminRole.getRoleName()) && BaseConstants.ROLE_NAME_BEST.equals(role.getRoleName())) {
            adminService.updateById(admin);
            if (admin.getStatus() == 0) {

                // 删除redis中管理员信息
                deleteAdminInfo();

                return R.ok().message(BaseConstants.BANNED_FAIL);
            } else {
                recycleService.remove(wrapper);

                // 删除redis中管理员信息
                deleteAdminInfo();

                return R.ok().message(BaseConstants.BANNED_SUCCESS);
            }
        } else{// 当前登录用户有增删改查功能，但不能操作超级管理员
            if(!BaseConstants.ROLE_NAME_BEST.equals(adminRole.getRoleName())) {
                adminService.updateById(admin);
                if (admin.getStatus() == 0) {

                    // 删除redis中管理员信息
                    deleteAdminInfo();

                    return R.ok().message(BaseConstants.BANNED_FAIL);
                } else {
                    recycleService.remove(wrapper);

                    // 删除redis中管理员信息
                    deleteAdminInfo();

                    return R.ok().message(BaseConstants.BANNED_SUCCESS);
                }
            } else {
                return R.error().message("您没有权限对该用户进行状态管理");
            }
        }
    }

    @ApiOperation("添加或修改管理员头像")
    @PutMapping("/add-or-change-admin-avatar")
    public R addOrChangeAdminAvatar(@ApiParam(name = "admin", value = "接收的管理员信息", required = true) @RequestBody(required = true) Admin admin) {
        // 修改头像删除原头像
        adminService.removerAvatar(admin.getId());
        if(adminService.updateById(admin)) {
            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @ApiOperation("查询管理员密码是否正确")
    @PostMapping("/verify-pwd")
    public R verifyPwd(@ApiParam(name = "admin", value = "管理员信息") @RequestBody Admin admin) {

        Admin temp = adminService.getById(admin.getId());

        SimpleHash hash = new SimpleHash(BaseConstants.ALGORITHM_NAME, admin.getPassword(), ByteSource.Util.bytes(admin.getUsername()), BaseConstants.HASH_ITERATIONS);

        if(!temp.getPassword().equals(hash.toString())) { // 如果不相等
            return R.ok().message("原密码不正确");
        } else {
            return R.ok();
        }
    }

    @ApiOperation("修改管理员密码")
    @PutMapping("/edit-password")
    public R editPassword(@ApiParam(name = "admin", value = "管理员信息") @RequestBody Admin admin) {
        SimpleHash hash = new SimpleHash(BaseConstants.ALGORITHM_NAME, admin.getPassword(), ByteSource.Util.bytes(admin.getUsername()), BaseConstants.HASH_ITERATIONS);
        admin.setPassword(hash.toString());
        if(adminService.updateById(admin)) {
            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.ok().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @GetMapping("/parse/{keyword}")
    public boolean parse(@PathVariable("keyword") String keyword) throws IOException {
        return adminService.parseContent(keyword);
    }

    /**
     *  添加管理员信息
     * @param admin  当前添加信息
     * @param username  添加信息的用户名
     * @param email  邮箱
     * @return
     */
    public R detachmentList(Admin admin, String username, String email) {
        // 判断该管理员信息是否已存在
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq(SQLConstants.USERNAME, username);
        Admin adminByName = adminService.getOne(wrapper);

        if (adminByName == null) { // 不存在
            // 设置默认密码，对密码进行加盐加密
            SimpleHash hash = new SimpleHash(BaseConstants.ALGORITHM_NAME, BaseConstants.DEFAULT_PASSWORD, ByteSource.Util.bytes(admin.getUsername()), BaseConstants.HASH_ITERATIONS);
            admin.setPassword(hash.toString());
            // 添加到数据库
            adminService.save(admin);
            // 发送邮件告诉默认密码
            mailSendUtil.sendSimpleMail(BaseConstants.REGISTER_SUBJECT,BaseConstants.REGISTER_TEXT, email, BaseConstants.SENDER);
            return R.ok().message(MessageConstants.INSERT_SUCCESS);
        }else {
            return R.error().message(MessageConstants.ENTITY_EXIST);
        }
    }

    /**
     * 删除Redis中管理员的信息
     */
    private void deleteAdminInfo() {
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_ADMIN_LIST_ALL + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);
    }
}

