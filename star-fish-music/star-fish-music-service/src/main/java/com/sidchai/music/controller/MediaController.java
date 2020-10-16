package com.sidchai.music.controller;

import com.sidchai.music.exception.MusicException;
import com.sidchai.music.result.R;
import com.sidchai.music.result.ResultCodeEnum;
import com.sidchai.music.utils.ExceptionUtils;
import com.sidchai.music.utils.VodUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author sidchai
 * @since 2020-06-05
 */
@Api(description="阿里云视频点播")
@RestController
@RequestMapping("/music/vod/media")
@Slf4j
public class MediaController {

    @Autowired
    private VodUtils vodUtils;

    @ApiOperation("上传视频至阿里云")
    @PostMapping("/upload")
    public R uploadVideo(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestParam("file") MultipartFile file) {

        try {
            System.out.println(111);
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String sourceId = vodUtils.uploadVideo(inputStream, originalFilename);
            return R.ok().message("视频上传成功").data("sourceId", sourceId);
        } catch (IOException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new MusicException(ResultCodeEnum.VIDEO_UPLOAD_TOMCAT_ERROR);
        }
    }

    @ApiOperation("/删除阿里云视频")
    @DeleteMapping("/remove/{vodId}")
    public R removeVid(
            @ApiParam(value="阿里云视频id", required = true)
            @PathVariable String vodId) {
        try {
            vodUtils.removeVideo(vodId);
            return R.ok().message("视频删除成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new MusicException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }
    }
}
