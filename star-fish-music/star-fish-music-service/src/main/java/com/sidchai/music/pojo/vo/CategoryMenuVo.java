package com.sidchai.music.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sidchai
 * @since 2020-06-02
 */
@Data
public class CategoryMenuVo extends PageInfo<CategoryMenuVo> implements Serializable {

    /**
     *  id
     */
    private Long id;
    /**
     *  菜单名称
     */
    private String menuName;
    /**
     *  菜单等级
     */
    private Integer menuLevel;
    /**
     *  菜单类型 （菜单，按钮）
     */
    private Integer menuType;
    /**
     *  菜单介绍
     */
    private String introduce;
    /**
     * Icon图标
     */
    private String icon;
    /**
     * 父ID
     */
    private Long parentId;
    /**
     * URL地址
     */
    private String url;
    /**
     * 排序字段(越大越靠前)
     */
    private Integer sort;
    /**
     * 是否显示  1: 是  0: 否
     */
    private Integer isShow;
}
