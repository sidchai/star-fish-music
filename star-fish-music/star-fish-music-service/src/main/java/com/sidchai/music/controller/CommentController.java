package com.sidchai.music.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.constants.*;
import com.sidchai.music.pojo.Comment;
import com.sidchai.music.result.R;
import com.sidchai.music.service.CommentService;
import com.sidchai.music.service.SongListService;
import com.sidchai.music.service.SongService;
import com.sidchai.music.service.UserService;
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
 * 评论表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Api(description = "评论管理")
@RestController
@RequestMapping("/music/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private SongService songService;

    @Autowired
    private SongListService songListService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取所有评论列表信息")
    @GetMapping("/get-comment-list")
    public R getCommentList() {

        // 从redis中取出信息
        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_COMMENT_LIST + RedisConstants.SEGMENTATION + "*");

        if(!StringUtils.isEmpty(jsonResult)) {
            List<Comment> comments = JsonUtils.jsonToList(jsonResult, Comment.class);
            return R.ok().data(BaseConstants.COMMENT_INFO,comments);
        }

        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<Comment> list = commentService.list(queryWrapper);

        list.forEach(item -> {
            // 保存用户信息
            item.setUser(userService.getById(item.getUserId()));
            if(!StringUtils.isEmpty(item.getSongId())) {
                item.setSong(songService.getById(item.getSongId()));
            }
            if(!StringUtils.isEmpty(item.getSongListId())) {
                item.setSongList(songListService.getById(item.getSongListId()));
            }
        });

        // 把信息存入redis中
        redisUtil.setEx(RedisConstants.REDIS_COMMENT_LIST + RedisConstants.SEGMENTATION + "*", JsonUtils.objectToJson(list), 1, TimeUnit.DAYS);

        return R.ok().data(BaseConstants.COMMENT_INFO,list);
    }

    @ApiOperation("删除评论信息")
    @DeleteMapping("/delete-comment/{id}")
    public R deleteComment(@ApiParam(name ="id", value = "要删除的id") @PathVariable Long id) {

        if(StringUtils.isEmpty(id)) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        if(commentService.removeById(id)) {

            deleteCommentInfo();

            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @ApiOperation("批量删除评论信息")
    @DeleteMapping("/delete-batch-comment")
    public R deleteComment(@ApiParam(name ="ids", value = "要删除的id集合") @RequestParam List<Long> ids) {

        if(ids.isEmpty()) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        if(commentService.removeByIds(ids)) {

            deleteCommentInfo();

            return R.ok().message(MessageConstants.DELETE_BATCH_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_BATCH_FAIL);
        }
    }

    /**
     *  删除redis中评论信息
     */
    public void deleteCommentInfo() {
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_COMMENT_LIST + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);
    }
}

