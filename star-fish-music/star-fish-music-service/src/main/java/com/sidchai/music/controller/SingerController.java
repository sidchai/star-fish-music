package com.sidchai.music.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sidchai.music.annotion.avoidRepeatableCommit.AvoidRepeatableCommit;
import com.sidchai.music.constants.*;
import com.sidchai.music.pojo.Singer;
import com.sidchai.music.pojo.Song;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SingerService;
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
import java.util.Map;
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
@Api(description = "歌手信息管理")
@RestController
@RequestMapping("/music/singer")
public class SingerController {

    @Autowired
    private SingerService singerService;

    @Autowired
    private SongService songService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("根据关键字查询歌手名信息")
    @GetMapping("/get-name-list-by-keyword/{keyword}")
    public R getNameListByKeyword(@ApiParam(name = "keyword", value = "关键字", required = true)
                                  @PathVariable("keyword") String keyword) {

        List<Map<String, Object>> nameList = singerService.getNameList(keyword);

        return R.ok().data(BaseConstants.SINGER_NAME_LIST,nameList);
    }

    @ApiOperation("获取歌手所有信息")
    @GetMapping("/get-all-singer")
    public R getAllSinger() {

        // 从redis中读取数据
        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_SINGER_LIST + RedisConstants.SEGMENTATION + "*");
        // 如果信息存在
        if (!StringUtils.isEmpty(jsonResult)) { // 直接返回信息
            List<Singer> singers = JsonUtils.jsonToList(jsonResult, Singer.class);
            return R.ok().data(BaseConstants.SINGER_INFO,singers);
        }
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.orderByDesc("rank");
        List<Singer> list = singerService.list(queryWrapper);

        // 把信息存入redis中
        redisUtil.setEx(RedisConstants.REDIS_SINGER_LIST + RedisConstants.SEGMENTATION + "*", JsonUtils.objectToJson(list), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.SINGER_INFO,list);
    }

    @ApiOperation("获取歌手列表分页信息")
    @GetMapping("/get-list/{currentPage}/{pageSize}")
    public R getSingerList(@ApiParam(name = "currentPage", value = "当前页数", required = true) @PathVariable("currentPage") Long currentPage,
                          @ApiParam(name = "pageSize", value = "每页显示数目", required = true) @PathVariable("pageSize") Long pageSize) {
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("rank");
        // 创建分页对象
        Page<Singer> page = new Page<>();
        // 设置当前页数
        page.setCurrent(currentPage);
        // 设置每页显示的数目
        page.setSize(pageSize);

        IPage<Singer> pageList = singerService.page(page, queryWrapper);
        List<Singer> list = pageList.getRecords(); // 转成list类型

        return R.ok().data(BaseConstants.SINGER_PAGE_LIST, pageList);
    }

    @ApiOperation("根据关键字所有歌手列表分页信息")
    @GetMapping("/get-list/{currentPage}/{pageSize}/{keyword}")
    public R getSingerList(@ApiParam(name = "currentPage", value = "当前页数", required = true) @PathVariable("currentPage") Long currentPage,
                          @ApiParam(name = "pageSize", value = "每页显示数目", required = true) @PathVariable("pageSize") Long pageSize,
                          @ApiParam(name = "keyword", value = "关键字", required = true) @PathVariable("keyword") String keyword) {
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(keyword)) {
            queryWrapper.like(SQLConstants.SINGER_NAME, keyword).or().like(SQLConstants.SINGER_NAME, keyword.trim());
        }

        // 创建分页对象
        Page<Singer> page = new Page<>();
        // 设置当前页数
        page.setCurrent(currentPage);
        // 设置每页显示的数目
        page.setSize(pageSize);

        IPage<Singer> pageList = singerService.page(page, queryWrapper);

        return R.ok().data(BaseConstants.SINGER_PAGE_LIST_KEYWORD, pageList);
    }

    @ApiOperation("所有歌手信息列表以及包含的歌曲信息")
    @GetMapping("/get-singer-by-song")
    public R getSingerBySong() {
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<Singer> singers = singerService.list(queryWrapper);
        singers.forEach(item -> {
            if (item != null) {
                QueryWrapper<Song> wrapper = new QueryWrapper<>();
                wrapper.eq(SQLConstants.SINGER_ID, item.getId());
                wrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
                List<Song> songs = songService.list(wrapper);
                item.setSongs(songs);
            }
        });
        return R.ok().data(BaseConstants.SINGER_SONG_INFO, singers);
    }

    @ApiOperation("删除歌手信息")
    @DeleteMapping("/delete-singer/{id}")
    public R deleteSinger(@ApiParam(name = "id", value = "歌手id", required = true) @PathVariable("id") Long id) {

        // 根据id删除歌手头像
        singerService.removePic(id);

        // 判断id是否为空和 ""
        if (StringUtils.isEmpty(id)) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        deleteSingerInfo();
        deleteSingerInfoApi();

        if(singerService.removeById(id)) {
            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @ApiOperation("批量删除歌手信息")
    @DeleteMapping("/delete-singers")
    public R deleteSingers(@ApiParam(name = "ids", value = "歌手id集合", required = true) @RequestParam("ids") List<Long> ids) {

        // 判断ids 是否为空或""
        if (ids.isEmpty()) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        deleteSingerInfo();
        deleteSingerInfoApi();

        // 删除数据库中信息
        if(singerService.removeByIds(ids)) {
            return R.ok().message(MessageConstants.DELETE_BATCH_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_BATCH_FAIL);
        }
    }

    @ApiOperation("修改歌手信息")
    @PutMapping("/edit-singer")
    public R editSinger(@ApiParam(name = "singer", value = "接收的歌手信息", required = true) @RequestBody(required = true) Singer singer) {

        if(singer == null) {
            return R.error().message(MessageConstants.SINGER_IS_NULL);
        }

        deleteSingerInfo();
        deleteSingerInfoApi();

        // 修改数据库中信息
        if(singerService.updateById(singer)) {
            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        }else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @AvoidRepeatableCommit
    @ApiOperation("添加歌手信息")
    @PostMapping("/add-singer")
    public R addSinger(@ApiParam(name = "singer", value = "接收的歌手信息", required = true) @RequestBody(required = true) Singer singer) {

        if(singer == null) {
            return R.error().message(MessageConstants.SINGER_IS_NULL);
        }

        deleteSingerInfo();
        deleteSingerInfoApi();

        if(singerService.save(singer)) {
            return R.ok().message(MessageConstants.INSERT_SUCCESS);
        }else {
            return R.error().message(MessageConstants.INSERT_FAIL);
        }
    }

    @ApiOperation("改变歌手状态")
    @PutMapping("/change-status")
    public R changeStatus(@ApiParam(name = "singer", value = "接收的歌手信息", required = true) @RequestBody(required = true) Singer singer) {

        deleteSingerInfo();
        deleteSingerInfoApi();

        singerService.updateById(singer);
        if(singer.getStatus() == 0) {
            return R.ok().message(BaseConstants.SOLD_OUT_SUCCESS);
        }else {
            return R.ok().message(BaseConstants.PUT_AWAY_SUCCESS);
        }
    }

    @ApiOperation("添加或修改歌手图片")
    @PutMapping("/add-or-change-singer-pic")
    public R addOrChangeSingerPic(@ApiParam(name = "singer", value = "接收的歌手信息", required = true) @RequestBody(required = true) Singer singer) {
        // 修改头像删除原头像
        singerService.removePic(singer.getId());

        deleteSingerInfo();
        deleteSingerInfoApi();

        if(singerService.updateById(singer)) {
            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    /**
     * 删除redis中singer信息
     */
    private void deleteSingerInfo() {
        // 添加/修改/删除成功，删除redis中歌手信息
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_SINGER_LIST + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);
    }

    /**
     * 删除redis中singer api信息
     */
    private void deleteSingerInfoApi() {
        // 添加/修改/删除成功，删除redis中歌手api信息
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_FRONT_SINGER_LIST + RedisConstants.SEGMENTATION + "front");
        redisUtil.delete(keys);
    }
}

