package com.sidchai.music.pojo.vo;

import com.sidchai.music.pojo.Singer;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sidchai
 * @since 2020-06-04
 */
@Data
public class ClassifyVo implements Serializable {

    /**
     *  id
     */
    private Long id;

    /**
     *  分类名
     */
    private String name;

    /**
     *  分类歌手信息
     */
    private Singer singer;
}
