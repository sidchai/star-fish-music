package com.sidchai.music.pojo.vo;

import com.sidchai.music.pojo.SysDict;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sidchai
 * @since 2020-06-03
 */
@Data
public class SysDictVo extends PageInfo<SysDict> implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     *  字典键值
     */
    private String dictValue;

    /**
     *  字典类型id
     */
    private Long dictTypeId;

    /**
     *  表格回显样式
     */
    private String listClass;

    /**
     *  是否默认 (0:否，1:是)  默认为0
     */
    private Integer isDefault;

    /**
     *  备注
     */
    private String remake;

    /**
     *  排序字段 (越大越靠前)
     */
    private Integer sort;
}
