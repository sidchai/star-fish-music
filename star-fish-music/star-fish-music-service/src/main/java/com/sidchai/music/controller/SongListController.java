package com.sidchai.music.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.annotion.avoidRepeatableCommit.AvoidRepeatableCommit;
import com.sidchai.music.constants.*;
import com.sidchai.music.pojo.SongList;
import com.sidchai.music.pojo.vo.SongListVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SongListService;
import com.sidchai.music.utils.JsonUtils;
import com.sidchai.music.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 歌单表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Api(description = "歌单信息管理")
@RestController
@RequestMapping("/music/song-list")
public class SongListController {

    @Autowired
    private SongListService songListService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取所有歌单信息")
    @GetMapping("/get-song-list-info")
    public R getSongListInfo() {

        // 从redis中读取信息
        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_SONG_LIST_INFO + RedisConstants.SEGMENTATION + "*");

        if(!StringUtils.isEmpty(jsonResult)) {
            List<SongList> songList = JsonUtils.jsonToList(jsonResult, SongList.class);
            return R.ok().data(BaseConstants.SONG_LIST_INFO,songList);
        }

        QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.orderByDesc(SQLConstants.SORT);

        List<SongList> list = songListService.list(queryWrapper);

        // 把信息存入redis中
        redisUtil.setEx(RedisConstants.REDIS_SONG_LIST_INFO + RedisConstants.SEGMENTATION + "*", JsonUtils.objectToJson(list), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.SONG_LIST_INFO, list);
    }

    @AvoidRepeatableCommit
    @ApiOperation("添加歌单信息")
    @PostMapping("/add-song-list")
    public R addSongList(@ApiParam(name = "songListVo", value = "添加的歌单信息") @RequestBody(required = true) SongListVo songListVo) {


        return songListService.addSongList(songListVo);
    }

    @ApiOperation("修改歌单信息")
    @PutMapping("/edit-song-list")
    public R editSongList(@ApiParam(name = "songListVo", value = "添加的歌单信息") @RequestBody(required = true) SongListVo songListVo) {

        return songListService.editSongList(songListVo);
    }

    @ApiOperation("删除歌单信息")
    @DeleteMapping("/delete-song-list/{id}")
    public R deleteSongList(@ApiParam(name = "id", value = "要删除的歌曲信息id") @PathVariable Long id) {

        return songListService.deleteSongList(id);
    }

    @ApiOperation("批量删除歌单信息")
    @DeleteMapping("/delete-batch-song-list")
    public R deleteBatchSongList(@ApiParam(name = "ids", value = "歌单id集合") @RequestParam List<Long> ids) {

        return songListService.deleteBatchSongList(ids);
    }

    @ApiOperation("改变歌单状态")
    @PutMapping("/change-song-list-status")
    public R changeSongListStatus(@ApiParam(name = "songList", value = "接收的歌单信息", required = true) @RequestBody(required = true) SongList songList) {

        return songListService.changeSongListStatus(songList);
    }

    @ApiOperation("添加或修改歌单图片")
    @PutMapping("/add-or-change-song-list-avatar")
    public R addOrChangeSongListAvatar(@ApiParam(name = "songList", value = "接收的歌单信息", required = true) @RequestBody(required = true) SongList songList) {
        return songListService.addOrChangeSongListAvatar(songList);
    }

}

