package com.sidchai.music.service;

import com.sidchai.music.pojo.Carousel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface CarouselService extends IService<Carousel> {

    /**
     *  根据id删除轮播图文件
     * @param id
     * @return
     */
    boolean removeAvatarById(Long id);
}
