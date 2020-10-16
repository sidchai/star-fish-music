package com.sidchai.music.controller.api;

import com.sidchai.music.exception.MusicException;
import com.sidchai.music.result.R;
import com.sidchai.music.result.ResultCodeEnum;
import com.sidchai.music.utils.ExceptionUtils;
import com.sidchai.music.utils.VodUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sidchai
 * @since 2020-06-08
 */
@Api(description="阿里云视频点播")
@RestController
@RequestMapping("/music/api/vod/media")
@Slf4j
public class ApiMediaController {

    @Autowired
    private VodUtils vodUtils;

    @GetMapping("get-play-auth/{videoSourceId}")
    public R getPlayAuth(
            @ApiParam(value = "阿里云视频文件的id", required = true)
            @PathVariable String videoSourceId){

        try{
            String playAuth = vodUtils.getPlayAuth(videoSourceId);
            return  R.ok().message("获取播放凭证成功").data("playAuth", playAuth);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new MusicException(ResultCodeEnum.FETCH_PLAYAUTH_ERROR);
        }
    }
}
