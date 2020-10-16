package com.sidchai.music.pojo.vo;

import com.sidchai.music.pojo.SysDictType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sidchai
 * @since 2020-06-03
 */
@Data
public class SysDictTypeVo extends PageInfo<SysDictType> implements Serializable {

    /**
     *  id
     */
    private Long id;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 备注
     */
    private String remake;

    /**
     * 排序字段
     */
    private Integer sort;
}
