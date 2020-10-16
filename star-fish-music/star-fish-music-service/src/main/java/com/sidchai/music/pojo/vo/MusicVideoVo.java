package com.sidchai.music.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sidchai
 * @since 2020-06-08
 */
@Data
public class MusicVideoVo implements Serializable {
    private Long id;
    private String title;
    private String singerName;
    private String sourceId;
    private String originalName;
    private String pic;
}
