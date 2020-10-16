package com.sidchai.music.controller;


import com.sidchai.music.pojo.WebConfig;
import com.sidchai.music.result.R;
import com.sidchai.music.service.WebConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 网站基础配置 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-26
 */
@Api(description = "网站基础配置管理")
@RestController
@RequestMapping("/music/web-config")
public class WebConfigController {

    @Autowired
    private WebConfigService webConfigService;

    @ApiOperation("拉取网站基础配置表")
    @GetMapping("/get-web-config")
    public R getWebConfig() {
        WebConfig webConfig = webConfigService.getOne(null);
        return R.ok().data("item", webConfig);
    }

    @ApiOperation("更新网站基础信息")
    @PutMapping("/edit-web-config")
    public R editWebConfig(@ApiParam(name = "webConfig", value = "网站信息") @RequestBody(required = true) WebConfig webConfig) {
        if(webConfigService.updateById(webConfig)) { // 修改成功
            return R.ok().message("保存成功!");
        }else { //修改失败
            return R.error().message("保存失败!");
        }
    }
}

