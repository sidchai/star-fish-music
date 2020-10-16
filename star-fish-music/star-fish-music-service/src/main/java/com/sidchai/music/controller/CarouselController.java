package com.sidchai.music.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.MessageConstants;
import com.sidchai.music.constants.RedisConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.pojo.Carousel;
import com.sidchai.music.result.R;
import com.sidchai.music.service.CarouselService;
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
 *  前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Api(description = "轮播图管理")
@RestController
@RequestMapping("/music/carousel")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取所有轮播图信息")
    @GetMapping("/get-carousel-all")
    public R getCarouselAll() {

        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_CAROUSEL_LIST + RedisConstants.SEGMENTATION + "*");

        if(!StringUtils.isEmpty(jsonResult)) {
            List<Carousel> carousels = JsonUtils.jsonToList(jsonResult, Carousel.class);
            return R.ok().data(BaseConstants.CAROUSEL_INFO, carousels);
        }

        QueryWrapper<Carousel> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConstants.SORT);
        List<Carousel> list = carouselService.list(queryWrapper);

        redisUtil.setEx(RedisConstants.REDIS_CAROUSEL_LIST + RedisConstants.SEGMENTATION + "*", JsonUtils.objectToJson(list), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.CAROUSEL_INFO, list);

    }

    @ApiOperation("新增轮播图")
    @PostMapping("/add-carousel")
    public R addCarousel(@ApiParam(name = "carousel", value = "轮播图信息") @RequestBody(required = true) Carousel carousel) {
        QueryWrapper<Carousel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.TITLE, carousel.getTitle());
        Carousel one = carouselService.getOne(queryWrapper);
        if (one != null) {
            return R.error().message(MessageConstants.ENTITY_EXIST);
        }

        if(carouselService.save(carousel)) {

            deleteCarouselInfo();

            return R.ok().message(MessageConstants.INSERT_SUCCESS);
        } else {
            return R.error().message(MessageConstants.INSERT_FAIL);
        }

    }

    @ApiOperation("修改轮播图")
    @PutMapping("/edit-carousel")
    public R editCarousel(@ApiParam(name = "carousel", value = "轮播图信息") @RequestBody(required = true) Carousel carousel) {

        if(carouselService.updateById(carousel)) {

            deleteCarouselInfo();

            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @ApiOperation("删除轮播图")
    @DeleteMapping("/delete-carousel/{id}")
    public R deleteCarousel(@ApiParam(name = "id", value = "轮播图id") @PathVariable Long id) {
        if(StringUtils.isEmpty(id)) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        if(carouselService.removeById(id)) {

            deleteCarouselInfo();

            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @ApiOperation("批量删除轮播图")
    @DeleteMapping("/delete-batch-carousel")
    public R deleteBatchCarousel(@ApiParam(name = "ids", value = "轮播图id集合") @RequestParam List<Long> ids) {
        if(ids.isEmpty()) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        if(carouselService.removeByIds(ids)) {

            deleteCarouselInfo();

            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @ApiOperation("改变轮播图状态")
    @PutMapping("/change-carousel-status")
    public R changeCarouselStatus(@ApiParam(name = "carousel", value = "轮播图信息") @RequestBody(required = true) Carousel carousel) {
        if(StringUtils.isEmpty(carousel.getId())) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        if(carouselService.updateById(carousel)) {
            if(carousel.getStatus() == 0) {

                deleteCarouselInfo();

                return R.ok().message(BaseConstants.SOLD_OUT_SUCCESS);
            } else {
                return R.ok().message(BaseConstants.PUT_AWAY_SUCCESS);
            }
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @ApiOperation("添加或修改轮播图图片")
    @PutMapping("/add-or-change-carousel-avatar")
    public R addOrChangeCarouselAvatar(@ApiParam(name = "carousel", value = "轮播图信息") @RequestBody(required = true) Carousel carousel) {
        carouselService.removeAvatarById(carousel.getId());

        if(carouselService.updateById(carousel)) {

            deleteCarouselInfo();

            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }


    /**
     * 删除redis中轮播图信息
     */
    private void deleteCarouselInfo() {
        // 添加成功，删除redis中歌曲信息
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_CAROUSEL_LIST + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);
    }
}

