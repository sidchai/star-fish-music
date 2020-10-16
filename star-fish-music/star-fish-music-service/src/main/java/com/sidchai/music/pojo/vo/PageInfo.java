package com.sidchai.music.pojo.vo;

import lombok.Data;

/**
 * PageVo 用于分页
 * @author sidchai
 * @since 2020-06-02
 */
@Data
public class PageInfo<T> {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 当前页
     */
    private Long currentPage;

    /**
     * 页大小
     */
    private Long pageSize;
}
