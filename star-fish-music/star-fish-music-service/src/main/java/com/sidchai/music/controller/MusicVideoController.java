package com.sidchai.music.controller;


import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.constants.*;
import com.sidchai.music.pojo.MusicVideo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.MusicVideoService;
import com.sidchai.music.utils.JsonUtils;
import com.sidchai.music.utils.RedisUtil;
import com.sidchai.music.utils.VodUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * mv表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Api(description = "MV管理")
@Slf4j
@RestController
@RequestMapping("/music/music-video")
public class MusicVideoController {

    @Autowired
    private MusicVideoService musicVideoService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private VodUtils vodUtils;

    @ApiOperation("获取mv全部信息")
    @GetMapping("/get-mv-list")
    public R getMvList() {

        // 从redis中读取信息
        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_MV_LIST + RedisConstants.SEGMENTATION + "*");

        if(!StringUtils.isEmpty(jsonResult)) {
            List<MusicVideo> songList = JsonUtils.jsonToList(jsonResult, MusicVideo.class);
            return R.ok().data(BaseConstants.MV_INFO,songList);
        }
        QueryWrapper<MusicVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<MusicVideo> list = musicVideoService.list(queryWrapper);

        // 把信息存入redis中
        redisUtil.setEx(RedisConstants.REDIS_MV_LIST + RedisConstants.SEGMENTATION + "*", JsonUtils.objectToJson(list), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.MV_INFO,list);
    }

    @ApiOperation("新增MV信息")
    @PostMapping("/add-mv")
    public R addMv(@ApiParam(name = "musicVideo", value = "得到的mv信息") @RequestBody MusicVideo musicVideo) {

        QueryWrapper<MusicVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.TITLE, musicVideo.getTitle());
        queryWrapper.eq(SQLConstants.SINGER_NAME, musicVideo.getSingerName());
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        MusicVideo temp = musicVideoService.getOne(queryWrapper);
        if (temp != null) {
            return R.error().message(MessageConstants.ENTITY_EXIST);
        }

        // 删除redis中mv信息
        deleteMvInfo();
        deleteMvInfoApi();

        if(musicVideoService.save(musicVideo)) {

            return R.ok().message(MessageConstants.INSERT_SUCCESS);
        } else {
            return R.error().message(MessageConstants.INSERT_FAIL);
        }
    }

    @ApiOperation("修改MV信息")
    @PutMapping("/edit-mv")
    public R editMv(@ApiParam(name = "musicVideo", value = "得到的mv信息") @RequestBody MusicVideo musicVideo) {
        // TODO

        if(musicVideo == null) {
            return R.error().message(MessageConstants.ENTITY_NONENTITY);
        }

        // 删除redis中mv信息
        deleteMvInfo();
        deleteMvInfoApi();

        if(musicVideoService.updateById(musicVideo)) {

            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @ApiOperation("删除MV信息")
    @DeleteMapping("/delete-mv")
    public R deleteMv(@ApiParam(name = "id", value = "要删除mv的id") @PathVariable Long id) throws ClientException {

        if(StringUtils.isEmpty(id)) {
           return R.error().message(MessageConstants.ID_IS_NULL);
        }

        MusicVideo musicVideo = musicVideoService.getById(id);

        // 先删除阿里云上的视频
        vodUtils.removeVideo(musicVideo.getSourceId());

        deleteMvInfo();
        deleteMvInfoApi();

        if(musicVideoService.removeById(id)) {

            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @ApiOperation("修改MV状态信息")
    @PutMapping("/change-mv-status")
    public R changeStatus(@ApiParam(name = "musicVideo", value = "得到的mv信息") @RequestBody MusicVideo musicVideo) {

        deleteMvInfo();
        deleteMvInfoApi();

        if(musicVideoService.updateById(musicVideo)) {
            if(musicVideo.getStatus() == 0) {
                return R.ok().message(BaseConstants.SOLD_OUT_SUCCESS);
            } else {
                return R.ok().message(BaseConstants.PUT_AWAY_SUCCESS);
            }
        } else {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }
    }


    @ApiOperation("修改MV信息(删除数据库中保存的视频信息)")
    @PutMapping("/change-video")
    public R changeVideo(@ApiParam(name = "musicVideo", value = "得到的mv信息") @RequestBody MusicVideo musicVideo) {

        deleteMvInfo();
        deleteMvInfoApi();

        if(musicVideoService.updateById(musicVideo)) {
            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    /**
     * 删除redis中mv信息
     */
    private void deleteMvInfo() {
        // 添加成功，删除redis中歌曲信息
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_MV_LIST + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);
    }

    /**
     * 删除redis中mv api信息
     */
    private void deleteMvInfoApi() {
        // 添加成功，删除redis中歌曲信息
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_FRONT_MV_LIST + RedisConstants.SEGMENTATION + "front");
        redisUtil.delete(keys);
    }
}

