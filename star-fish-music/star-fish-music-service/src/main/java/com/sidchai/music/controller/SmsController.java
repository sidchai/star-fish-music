package com.sidchai.music.controller;

import com.aliyuncs.exceptions.ClientException;
import com.sidchai.music.result.R;
import com.sidchai.music.utils.RandomUtils;
import com.sidchai.music.utils.RedisUtil;
import com.sidchai.music.utils.SmsUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 *  短信发送控制器
 * @author sidchai
 * @since 2020-05-31
 */
@Api(description = "阿里云短信发送管理")
@RestController
@RequestMapping("/music/sms")
public class SmsController {

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("验证码")
    @GetMapping("/send/{phone}")
    public R getCode(@PathVariable String phone) throws ClientException {

        // 生成验证码
        String checkCode = RandomUtils.getFourBitRandom();

        // 发送验证码
        smsUtils.send(phone, checkCode);

        // 存储验证码到redis
        redisUtil.setEx(phone, checkCode, 5, TimeUnit.MINUTES); // 5分钟有效

        return R.ok().message("短信发送成功");
    }
}
