package com.sidchai.music.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.annotion.avoidRepeatableCommit.AvoidRepeatableCommit;
import com.sidchai.music.constants.*;
import com.sidchai.music.pojo.Song;
import com.sidchai.music.pojo.vo.SongVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SongService;
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
 * 歌曲表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Api(description = "歌曲管理")
@RestController
@RequestMapping("/music/song")
public class SongController {

    @Autowired
    private SongService songService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取歌曲所有信息")
    @GetMapping("/get-song-list")
    public R getSongList() {

        // 从redis中读取信息
        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_SONG_LIST + RedisConstants.SEGMENTATION + "*");

        if(!StringUtils.isEmpty(jsonResult)) {
            List<Song> songList = JsonUtils.jsonToList(jsonResult, Song.class);
            return R.ok().data(BaseConstants.SONG_INFO,songList);
        }

        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<Song> list = songService.list(queryWrapper);

        // 存入信息到redis中
        redisUtil.setEx(RedisConstants.REDIS_SONG_LIST + RedisConstants.SEGMENTATION + "*", JsonUtils.objectToJson(list), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.SONG_INFO,list);
    }

    @AvoidRepeatableCommit
    @ApiOperation("添加歌曲信息")
    @PostMapping("/add-song")
    public R addSong(@ApiParam(name = "songVo", value = "添加的歌曲信息") @RequestBody(required = true) SongVo songVo) {


        return songService.addSong(songVo);
    }

    @ApiOperation("修改歌曲信息")
    @PutMapping("/edit-song")
    public R editSong(@ApiParam(name = "songVo", value = "修改的歌曲信息") @RequestBody(required = true) SongVo songVo) {

        return songService.editSong(songVo);
    }

    @ApiOperation("删除歌曲信息")
    @DeleteMapping("/delete-song/{id}")
    public R deleteSong(@ApiParam(name = "id", value = "要删除的歌曲信息id") @PathVariable Long id) {

        return songService.deleteSong(id);
    }

    @ApiOperation("批量删除歌曲信息")
    @DeleteMapping("/delete-batch-song")
    public R deleteBatchSong(@ApiParam(name = "ids", value = "歌曲id集合") @RequestParam List<Long> ids) {

        return songService.deleteBatchSong(ids);
    }

    @ApiOperation("改变歌曲状态")
    @PutMapping("/change-song-status")
    public R changeSongStatus(@ApiParam(name = "song", value = "接收的歌曲信息") @RequestBody Song song) {

        return songService.changeSongStatus(song);
    }

    @ApiOperation("添加或修改歌曲专辑图片")
    @PutMapping("/add-or-change-song-avatar")
    public R addOrChangeSongAvatar(@ApiParam(name = "song", value = "接收的歌曲信息", required = true) @RequestBody(required = true) Song song) {
        // 修改头像删除原头像
        songService.removeAvatarById(song.getId());

        deleteSongInfo();
        deleteSongInfoApi();

        if(songService.updateById(song)) {
            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @ApiOperation("添加或修改歌曲文件")
    @PutMapping("/add-or-change-song-url")
    public R addOrChangeSongUrl(@ApiParam(name = "song", value = "接收的歌曲信息", required = true) @RequestBody(required = true) Song song) {
        // 修改头像删除原头像
        songService.removeSongById(song.getId());

        deleteSongInfo();
        deleteSongInfoApi();

        if(songService.updateById(song)) {
            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    /**
     *  删除redis中歌曲信息
     */
    private void deleteSongInfo() {
        // 添加成功，删除redis中歌曲信息
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_SONG_LIST + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);
    }

    /**
     *  删除redis中歌曲 api 信息
     */
    private void deleteSongInfoApi() {
        // 添加成功，删除redis中歌曲信息
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_FRONT_SONG_LIST + RedisConstants.SEGMENTATION + "front");
        redisUtil.delete(keys);
    }
}
