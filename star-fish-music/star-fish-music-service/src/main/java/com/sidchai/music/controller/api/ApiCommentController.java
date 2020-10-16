package com.sidchai.music.controller.api;

import com.sidchai.music.pojo.vo.CommentVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidchai
 * @since 2020-06-10
 */
@Api(description = "评论信息")
@RestController
@RequestMapping("/music/api/comment")
public class ApiCommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("根据歌曲id获取评论信息")
    @GetMapping("/get-song-comment/{id}")
    public R getCommentBySong(@PathVariable Long id) {
        List<CommentVo> commentBySong = commentService.getCommentBySong(id);
        return R.ok().data("commentInfo", commentBySong);
    }

    @ApiOperation("根据歌单id获取评论信息")
    @GetMapping("/get-song-list-comment/{id}")
    public R getCommentBySongList(@PathVariable Long id) {
        List<CommentVo> commentBySongList = commentService.getCommentBySongList(id);
        return R.ok().data("commentInfo", commentBySongList);
    }

    @ApiOperation("添加评论信息")
    @PostMapping("/add-comment")
    public R addComment(@RequestBody CommentVo commentVo) {

        return commentService.addComment(commentVo);
    }

    @ApiOperation("评论点赞")
    @PutMapping("/set-up")
    public R setUp(@RequestBody CommentVo commentVo) {

        return commentService.setUp(commentVo);

    }
}
