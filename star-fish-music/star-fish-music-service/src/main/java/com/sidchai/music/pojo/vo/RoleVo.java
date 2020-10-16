package com.sidchai.music.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sidchai
 * @since 2020-06-01
 */
@Data
public class RoleVo extends PageInfo<RoleVo> implements Serializable {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 介绍
     */
    private String introduce;

    /**
     * 该角色所能管辖的区域
     */
    private String categoryMenuIds;

    /**
     *  id
     */
    private Long id;
}
