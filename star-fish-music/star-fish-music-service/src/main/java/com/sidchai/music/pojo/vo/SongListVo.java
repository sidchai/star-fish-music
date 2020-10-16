package com.sidchai.music.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sidchai
 * @since 2020-06-04
 */
@Data
public class SongListVo implements Serializable {
    /**
     *  id
     */
    private Long id;
    /**
     *  歌单标题
     */
    private String title;
    /**
     *  歌单图片url
     */
    private String pic;
    /**
     *  歌单介绍
     */
    private String introduction;
    /**
     *  歌单风格
     */
    private String style;
    /**
     *  歌单排序 （越大越靠前）
     */
    private Integer sort;
    /**
     *  创建人id
     */
    private Long userId;
    /**
     *  管理员id
     */
    private Long adminId;
}
