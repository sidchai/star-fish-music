package com.sidchai.music.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.constants.*;
import com.sidchai.music.pojo.Recycle;
import com.sidchai.music.pojo.User;
import com.sidchai.music.result.R;
import com.sidchai.music.service.RecycleService;
import com.sidchai.music.service.UserService;
import com.sidchai.music.utils.JsonUtils;
import com.sidchai.music.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Api(description = "用户管理")
@RestController
@RequestMapping("/music/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RecycleService recycleService;

    @ApiOperation("获取用户所有信息")
    @GetMapping("/get-all-user")
    public R getAllUser() {

        // 从redis中取数据
        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_USER_LIST + RedisConstants.SEGMENTATION + "*");

        if(!StringUtils.isEmpty(jsonResult)) {
            List<User> users = JsonUtils.jsonToList(jsonResult, User.class);
            return R.ok().data(BaseConstants.USER_INFO, users);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> list = userService.list(queryWrapper);

        // 把查询到的结果存入redis中
        redisUtil.setEx(RedisConstants.REDIS_USER_LIST + RedisConstants.SEGMENTATION + "*", JsonUtils.objectToJson(list), 1 , TimeUnit.DAYS);
        return R.ok().data(BaseConstants.USER_INFO, list);
    }

    @ApiOperation("修改用户信息")
    @PostMapping("/edit-user")
    public R editUser(@ApiParam(name = "user", value = "接收的用户信息") @RequestBody(required = true) User user) {

        if(userService.updateById(user)) {

            deleteUserInfo();

            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @ApiOperation("删除用户信息")
    @DeleteMapping("/delete-user/{id}")
    public R deleteUser(@ApiParam(name = "id", value = "要删除的用户id") @PathVariable(required = true) Long id) {

        // 先删除oss中存储的头像
        userService.removerAvatar(id);

        if(StringUtils.isEmpty(id)) {
           return R.error().message(MessageConstants.ID_IS_NULL);
        }

        QueryWrapper<Recycle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.ADMIN_ID, id);
        if(userService.removeById(id)) {

            deleteUserInfo();

            recycleService.remove(queryWrapper);

            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @ApiOperation("改变用户状态")
    @PutMapping("/change-status")
    public R changeStatus(@ApiParam(name = "user", value = "接收的用户信息") @RequestBody(required = true) User user) {
        if(StringUtils.isEmpty(user.getId())) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        if(userService.updateById(user)) {
            if(user.getStatus() == 0) {
                // 修改成功，删除redis中用户信息
                deleteUserInfo();

                return R.ok().message(BaseConstants.BANNED_FAIL);
            } else {

                // 修改成功，删除redis中用户信息
                deleteUserInfo();

                return R.ok().message(BaseConstants.BANNED_SUCCESS);
            }
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @ApiOperation("改变用户评论状态")
    @PutMapping("/change-comment-status")
    public R changeCommentStatus(@ApiParam(name = "user", value = "接收的用户信息") @RequestBody(required = true) User user) {
        if(StringUtils.isEmpty(user.getId())) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        if(userService.updateById(user)) {
            if(user.getCommentStatus() == 0) {
                // 修改成功，删除redis中用户信息
                deleteUserInfo();

                return R.ok().message(BaseConstants.MUTE_FAIL);
            } else {

                // 修改成功，删除redis中用户信息
                deleteUserInfo();

                return R.ok().message(BaseConstants.BANNED_SUCCESS);
            }
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @ApiOperation("添加或修改用户头像")
    @PutMapping("/add-or-change-user-avatar")
    public R addOrChangeUserAvatar(@ApiParam(name = "user", value = "接收的用户信息") @RequestBody(required = true) User user) {
        userService.removerAvatar(user.getId());

        if(userService.updateById(user)) {

            deleteUserInfo();

            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {

            deleteUserInfo();

            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    /**
     *  删除redis中用户信息
     */
    public void deleteUserInfo() {
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_USER_LIST + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);
    }
}

