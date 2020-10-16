package com.sidchai.music.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.RedisConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.pojo.SongList;
import com.sidchai.music.pojo.vo.SongVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SongListService;
import com.sidchai.music.utils.JsonUtils;
import com.sidchai.music.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author sidchai
 * @since 2020-06-07
 */
@Api(description = "歌单信息")
@RestController
@RequestMapping("/music/api/song-list")
public class ApiSongListController {

    @Autowired
    private SongListService songListService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取所有歌单信息")
    @GetMapping("/get-song-list-info")
    public R getSongListInfo() {

        // 从redis中读取信息
        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_FRONT_SONG_LIST_INFO + RedisConstants.SEGMENTATION + "front");

        if(!StringUtils.isEmpty(jsonResult)) {
            List<SongList> songList = JsonUtils.jsonToList(jsonResult, SongList.class);
            return R.ok().data(BaseConstants.SONG_LIST_INFO,songList);
        }

        QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.orderByDesc(SQLConstants.SORT);

        List<SongList> list = songListService.list(queryWrapper);

        // 把信息存入redis中
        redisUtil.setEx(RedisConstants.REDIS_FRONT_SONG_LIST_INFO + RedisConstants.SEGMENTATION + "front", JsonUtils.objectToJson(list), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.SONG_LIST_INFO, list);
    }

    @ApiOperation("根据歌单id查询歌单信息")
    @GetMapping("/get-song-list-by-id/{id}")
    public R getSongListById(@ApiParam(name = "id", value = "歌单id") @PathVariable Long id) {

        SongList songList = songListService.getById(id);

        return R.ok().data("songListById", songList);
    }

    @ApiOperation("根据歌单id查询包含歌曲信息")
    @GetMapping("/get-song-by-list-id/{id}")
    public R getSongByListId(@ApiParam(name = "id", value = "歌单id") @PathVariable Long id) {
        List<SongVo> songVoList = songListService.getSongByListId(id);
        return R.ok().data(BaseConstants.SONG_LIST_INFO, songVoList);
    }

    @ApiOperation("根据歌单标题进行模糊查询")
    @GetMapping("/get-song-list-by-like-title/{keyword}")
    public R getSongListByLikeTitle(@ApiParam("查询关键字") @PathVariable String keyword) {

        QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(SQLConstants.TITLE, keyword);
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<SongList> list = songListService.list(queryWrapper);

        return R.ok().data("songListByLikeTitle",list);
    }

    @ApiOperation("通过类别获取歌单")
    @GetMapping("/get-song-list-by-style/{style}")
    public R getSongListByStyle(@ApiParam("歌单类别") @PathVariable String style) {
        QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("style", style);
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<SongList> list = songListService.list(queryWrapper);
        return R.ok().data("songListByStyle",list);
    }
}
