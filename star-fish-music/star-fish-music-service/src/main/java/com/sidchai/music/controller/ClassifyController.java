package com.sidchai.music.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.pojo.Classify;
import com.sidchai.music.pojo.Singer;
import com.sidchai.music.result.R;
import com.sidchai.music.service.ClassifyService;
import com.sidchai.music.service.SingerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 歌手类型表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Api(description = "歌手类型管理")
@RestController
@RequestMapping("/music/classify")
public class ClassifyController {

    @Autowired
    private ClassifyService classifyService;

    @Autowired
    private SingerService singerService;

    @ApiOperation("所有类型列表")
    @GetMapping("/list")
    public R listAll() {
        QueryWrapper<Classify> cqw = new QueryWrapper<>();
        Map<String, Object> map = classifyService.getMap(cqw);
        return R.ok().data(map);
    }

    @ApiOperation("所有类型列表及包含的歌手信息")
    @GetMapping("/get-classify-by-singer")
    public R getClassifyBySinger() {
        QueryWrapper<Classify> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<Classify> list = classifyService.list(queryWrapper);
        list.forEach(item -> {
            if(item != null) {
                QueryWrapper<Singer> wrapper = new QueryWrapper<>();
                wrapper.eq(SQLConstants.CLASSIFY_ID, item.getId());
                wrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
                List<Singer> singers = singerService.list(wrapper);
                item.setSingers(singers);
            }
        });
        return R.ok().data(BaseConstants.CLASSIFY_SINGER_INFO, list);
    }
}
