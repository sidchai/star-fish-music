package com.sidchai.music.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.RedisConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.pojo.Singer;
import com.sidchai.music.pojo.vo.SongVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SingerService;
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
@Api(description = "歌手信息")
@RestController
@RequestMapping("/music/api/singer")
public class ApiSingerController {

    @Autowired
    private SingerService singerService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取歌手所有信息")
    @GetMapping("/get-all-singer")
    public R getAllSinger() {

        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_FRONT_SINGER_LIST + RedisConstants.SEGMENTATION + "front");

        if(!StringUtils.isEmpty(jsonResult)) {
            List<Singer> singers = JsonUtils.jsonToList(jsonResult, Singer.class);
            return R.ok().data(BaseConstants.SINGER_INFO,singers);
        }

        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.orderByAsc("rank");
        List<Singer> list = singerService.list(queryWrapper);

        redisUtil.setEx(RedisConstants.REDIS_FRONT_SINGER_LIST + RedisConstants.SEGMENTATION + "front", JsonUtils.objectToJson(list), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.SINGER_INFO,list);
    }

    @ApiOperation("根据歌手类型获得歌手信息")
    @PostMapping("/get-singer-by-classify/{classifyId}")
    public R getSingerByClassify(@ApiParam(name = "classifyId", value = "歌手类型id") @PathVariable Long classifyId) {

        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_FRONT_SINGER_LIST + RedisConstants.SEGMENTATION + classifyId);

        if(!StringUtils.isEmpty(jsonResult)) {
            List<Singer> singers = JsonUtils.jsonToList(jsonResult, Singer.class);
            return R.ok().data(BaseConstants.CLASSIFY_SINGER_INFO,singers);
        }

        List<Singer> singerList = singerService.getSingerByClassify(classifyId);

        redisUtil.setEx(RedisConstants.REDIS_FRONT_SINGER_LIST + RedisConstants.SEGMENTATION + classifyId, JsonUtils.objectToJson(singerList), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.CLASSIFY_SINGER_INFO, singerList);
    }

    @ApiOperation("根据歌手名查询歌曲信息")
    @GetMapping("/get-song-by-singer-name/{keyword}")
    public R getSongBySingerName(@ApiParam("查询关键字") @PathVariable String keyword) {

        List<SongVo> songVos = singerService.getSongBySingerName(keyword);

        return R.ok().data("songsBySingerName", songVos);
    }
}
