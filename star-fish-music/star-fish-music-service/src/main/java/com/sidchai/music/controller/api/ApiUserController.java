package com.sidchai.music.controller.api;

import com.sidchai.music.constants.MessageConstants;
import com.sidchai.music.pojo.User;
import com.sidchai.music.pojo.vo.LoginVo;
import com.sidchai.music.pojo.vo.RegisterVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.UserService;
import com.sidchai.music.utils.JwtInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sidchai
 * @since 2020-06-09
 */
@Api(description = "用户信息")
@RestController
@RequestMapping("/music/api/user")
@Slf4j
public class ApiUserController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public R register(@ApiParam("接收用户注册信息") @RequestBody(required = true) RegisterVo registerVo) {

        userService.register(registerVo);

        return R.ok().message(MessageConstants.REGISTER_SUCCESS);

    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public R login(@ApiParam("接收用户登录信息") @RequestBody(required = true) LoginVo loginVo, HttpServletRequest req) {

        JwtInfo info = userService.login(loginVo, req);

        return R.ok().data("userInfo", info).message(MessageConstants.LOGIN_SUCCESS);
    }

    @ApiOperation("获取登录用户信息")
    @PostMapping("/info/{id}")
    public R getUserInfoById(@ApiParam("登录用户id") @PathVariable Long id) {

        User user = userService.getById(id);

        return R.ok().data("userInfoById", user);
    }

    @ApiOperation("修改用户信息")
    @PutMapping("/edit-user")
    public R editUser(@ApiParam(name = "user", value = "接收的用户信息") @RequestBody(required = true) User user) {

        if(userService.updateById(user)) {
            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @ApiOperation("添加或修改用户头像")
    @PutMapping("/add-or-change-user-avatar")
    public R addOrChangeUserAvatar(@ApiParam(name = "user", value = "接收的用户信息") @RequestBody(required = true) User user) {
        userService.removerAvatar(user.getId());

        if(userService.updateById(user)) {

            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {

            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }
}
