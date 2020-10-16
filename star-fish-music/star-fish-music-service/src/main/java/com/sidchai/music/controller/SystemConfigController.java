package com.sidchai.music.controller;


import com.sidchai.music.pojo.SystemConfig;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 网站系统配置 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-26
 */
@Api(description = "网站系统配置管理")
@RestController
@RequestMapping("/music/system-config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @ApiOperation("拉取系统配置表")
    @GetMapping("/get-system-config")
    public R getSystemConfig() {
        SystemConfig systemConfig = systemConfigService.getOne(null);
        return R.ok().data("item", systemConfig);
    }

    @ApiOperation("更新网站系统配置信息")
    @PutMapping("/edit-system-config")
    public R editSystemConfig(@ApiParam(name = "systemConfig", value = "网站系统配置信息") @RequestBody(required = true) SystemConfig systemConfig) {
        if(systemConfigService.updateById(systemConfig)) {
            return R.ok().message("保存成功!");
        } else {
            return R.error().message("保存失败!");
        }
    }
}

