package com.sidchai.music.controller.api;


import com.sidchai.music.pojo.MySong;
import com.sidchai.music.pojo.vo.SongVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.MySongService;
import com.sidchai.music.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 我的音乐表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Api(description = "我的音乐信息")
@RestController
@RequestMapping("/music/api/my-song")
public class ApiMySongController {

    @Autowired
    private MySongService mySongService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取当前登录用户收藏信息")
    @GetMapping("/get-my-song-by-id/{id}")
    public R getMySongById(@ApiParam("当前用户id") @PathVariable Long id) {

        List<SongVo> songVos = mySongService.getMySongById(id);
        return R.ok().data("mySongInfo", songVos);
    }

    @ApiOperation("收藏音乐")
    @PostMapping("/add-collection")
    public R addCollection(@RequestBody MySong mySong) {
        return mySongService.addCollection(mySong);
    }
}

