package com.sidchai.music.controller.api;

import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.RedisConstants;
import com.sidchai.music.pojo.vo.MusicVideoVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.MusicVideoService;
import com.sidchai.music.utils.JsonUtils;
import com.sidchai.music.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author sidchai
 * @since 2020-06-08
 */
@Api(description = "MV信息")
@Slf4j
@RestController
@RequestMapping("/music/api/music-video")
public class ApiMusicVideoController {

    @Autowired
    private MusicVideoService musicVideoService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取mv全部信息")
    @GetMapping("/get-mv-list")
    public R getMvList() {

        // 从redis中读取信息
        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_FRONT_MV_LIST + RedisConstants.SEGMENTATION + "front" );

        if(!StringUtils.isEmpty(jsonResult)) {
            List<MusicVideoVo> musicVideoVos = JsonUtils.jsonToList(jsonResult, MusicVideoVo.class);
            return R.ok().data(BaseConstants.MV_INFO,musicVideoVos);
        }

        List<MusicVideoVo> videoVoList = musicVideoService.getAllMvList();

        redisUtil.setEx(RedisConstants.REDIS_FRONT_MV_LIST + RedisConstants.SEGMENTATION + "front" , JsonUtils.objectToJson(videoVoList), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.MV_INFO, videoVoList);
    }
}
