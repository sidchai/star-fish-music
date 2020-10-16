package com.sidchai.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.Comment;
import com.sidchai.music.pojo.vo.CommentVo;
import com.sidchai.music.result.R;

import java.util.List;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface CommentService extends IService<Comment> {

    List<CommentVo> getCommentBySong(Long id);

    List<CommentVo> getCommentBySongList(Long id);

    R addComment(CommentVo commentVo);

    R setUp(CommentVo commentVo);
}
