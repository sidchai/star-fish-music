package com.sidchai.music.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.RedisConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.pojo.Carousel;
import com.sidchai.music.result.R;
import com.sidchai.music.service.CarouselService;
import com.sidchai.music.utils.JsonUtils;
import com.sidchai.music.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(description = "轮播图管理")
@RestController
@RequestMapping("/music/api/carousel")
public class ApiCarouselController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取所有轮播图信息")
    @GetMapping("/get-carousel-all")
    public R getCarouselAll() {

        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_FRONT_CAROUSEL_LIST + RedisConstants.SEGMENTATION + "front");

        if(!StringUtils.isEmpty(jsonResult)) {
            List<Carousel> carousels = JsonUtils.jsonToList(jsonResult, Carousel.class);
            return R.ok().data(BaseConstants.CAROUSEL_INFO, carousels);
        }

        QueryWrapper<Carousel> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConstants.SORT);
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<Carousel> list = carouselService.list(queryWrapper);

        redisUtil.setEx(RedisConstants.REDIS_FRONT_CAROUSEL_LIST + RedisConstants.SEGMENTATION + "front", JsonUtils.objectToJson(list), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.CAROUSEL_INFO, list);
    }
}
