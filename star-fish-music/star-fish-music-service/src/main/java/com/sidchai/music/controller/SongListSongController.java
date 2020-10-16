package com.sidchai.music.controller;


import com.sidchai.music.constants.MessageConstants;
import com.sidchai.music.pojo.SongListSong;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SongListSongService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 歌曲、歌单中间表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Api(description = "歌曲 & 歌单  中间表")
@RestController
@RequestMapping("/music/song-list-song")
public class SongListSongController {

    @Autowired
    private SongListSongService songListSongService;

    @ApiOperation("根据歌单id查询歌单中歌曲信息")
    @GetMapping("/get-song-list-song-info/{id}")
    public R getSongListSongInfo(@ApiParam(name = "id", value = "歌单id") @PathVariable Long id) {
        return songListSongService.getSongListSongInfo(id);
    }

    @ApiOperation("添加歌曲信息到 中间表中")
    @PostMapping("/add-song-list-song-info")
    public R addSongListSongInfo(@ApiParam(name = "songListSong", value = "接收的中间表信息") @RequestBody SongListSong songListSong) {
        if(songListSongService.save(songListSong)) {
            return R.ok().message(MessageConstants.INSERT_SUCCESS);
        } else {
            return R.error().message(MessageConstants.INSERT_FAIL);
        }
    }

    @ApiOperation("根据歌曲id删除中间表中信息")
    @DeleteMapping("/delete-song-list-song-info/{id}")
    public R addSongListSongInfo(@ApiParam(name = "id", value = "接收的歌曲id") @PathVariable Long id) {
        if(songListSongService.removeById(id)) {
            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete-batch-song-list-song-info")
    public R addSongListSongInfo(@ApiParam(name = "ids", value = "接收的歌曲id集合") @RequestParam List<Long> ids) {
        if(songListSongService.removeByIds(ids)) {
            return R.ok().message(MessageConstants.DELETE_BATCH_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_BATCH_FAIL);
        }
    }
}

