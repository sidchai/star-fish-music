package com.sidchai.music.controller.api;

import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.MessageConstants;
import com.sidchai.music.constants.RedisConstants;
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
import java.util.concurrent.TimeUnit;

/**
 * @author sidchai
 * @since 2020-06-06
 */
@Api(description = "歌曲信息")
@RestController
@RequestMapping("/music/api/song")
public class ApiSongController {

    @Autowired
    private SongService songService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("根据歌手id获取歌曲信息")
    @PostMapping("/get-singer-info-by-id/{id}")
    public R getSingerInfoById(@ApiParam(name = "id", value = "歌手id") @PathVariable(required = true) Long id) {

        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_FRONT_SONG_LIST + RedisConstants.SEGMENTATION + id);

        if(!StringUtils.isEmpty(jsonResult)) {
            List<SongVo> songVos = JsonUtils.jsonToList(jsonResult, SongVo.class);
            return R.ok().data(BaseConstants.SINGER_SONG_INFO,songVos);
        }

        List<SongVo> songVoList = songService.getSongBySingerId(id);

        redisUtil.setEx(RedisConstants.REDIS_FRONT_SONG_LIST + RedisConstants.SEGMENTATION + id, JsonUtils.objectToJson(songVoList), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.SINGER_SONG_INFO, songVoList);
    }

    @ApiOperation("修改歌曲播放次数")
    @PutMapping("/change-count/{id}")
    public R changeCount(@ApiParam("歌曲id") @PathVariable Long id) {
        Song songById = songService.getById(id);
        songById.setCount(songById.getCount() + 1);
        if(songService.updateById(songById)) {
            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }
}
