package com.sidchai.music.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sidchai
 * @since 2020-06-03
 */
@Data
public class SongVo implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     *  歌曲名
     */
    private String songName;

    /**
     *  专辑名
     */
    private String introduction;

    /**
     *  歌曲url
     */
    private String songUrl;

    /**
     *  歌曲专辑图片url
     */
    private String pic;

    /**
     *  歌词内容
     */
    private String lyric;

    /**
     *  播放次数
     */
    private Integer count;

    /**
     *  歌手id
     */
    private Long singerId;

    /**
     *  歌手名
     */
    private String singerName;
}
