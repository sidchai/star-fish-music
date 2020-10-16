package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.MessageConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.mapper.CommentMapper;
import com.sidchai.music.pojo.Comment;
import com.sidchai.music.pojo.User;
import com.sidchai.music.pojo.vo.CommentVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.CommentService;
import com.sidchai.music.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Override
    public List<CommentVo> getCommentBySong(Long id) {

        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.eq(SQLConstants.SONG_ID, id);
        List<Comment> comments = commentService.list(queryWrapper);

        List<CommentVo> commentVos = new ArrayList<>();

        comments.forEach(item -> {
            User user = userService.getById(item.getUserId());

            CommentVo commentVo = new CommentVo();

            commentVo.setId(item.getId());
            commentVo.setAvatar(user.getAvatar());
            commentVo.setContent(item.getContent());
            commentVo.setCreateTime(item.getCreateTime());
            commentVo.setNickName(user.getNickName());
            commentVo.setUp(item.getUp());

            commentVos.add(commentVo);

        });
        return commentVos;
    }

    @Override
    public List<CommentVo> getCommentBySongList(Long id) {

        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.eq(SQLConstants.SONG_LIST_ID, id);
        List<Comment> comments = commentService.list(queryWrapper);

        List<CommentVo> commentVos = new ArrayList<>();

        comments.forEach(item -> {
            User user = userService.getById(item.getUserId());

            CommentVo commentVo = new CommentVo();

            commentVo.setId(item.getId());
            commentVo.setAvatar(user.getAvatar());
            commentVo.setContent(item.getContent());
            commentVo.setCreateTime(item.getCreateTime());
            commentVo.setNickName(user.getNickName());
            commentVo.setUp(item.getUp());

            commentVos.add(commentVo);

        });
        return commentVos;
    }

    @Override
    public R addComment(CommentVo commentVo) {

        Comment comment = new Comment();
        if("0".equals(commentVo.getType())) {
            comment.setSongId(commentVo.getId());
            comment.setSource("SONG");
        } else {
            comment.setSongListId(commentVo.getId());
            comment.setSource("SONG_LIST");
        }
        comment.setContent(commentVo.getContent());
        comment.setUp(commentVo.getUp());
        comment.setUserId(commentVo.getUserId());

        if(commentService.save(comment)) {
            return R.ok().message(MessageConstants.COMMENT_SUCCESS);
        } else {
            return R.error().message(MessageConstants.COMMENT_FAIL);
        }
    }

    @Override
    public R setUp(CommentVo commentVo) {

        Comment comment = new Comment();
        comment.setId(commentVo.getId());
        comment.setUp(commentVo.getUp());

        if(commentService.updateById(comment)) {
            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }

    }

}
