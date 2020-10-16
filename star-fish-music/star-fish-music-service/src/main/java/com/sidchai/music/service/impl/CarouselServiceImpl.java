package com.sidchai.music.service.impl;

import com.sidchai.music.pojo.Carousel;
import com.sidchai.music.mapper.CarouselMapper;
import com.sidchai.music.service.CarouselService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements CarouselService {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private OssUtil ossUtil;

    @Override
    public boolean removeAvatarById(Long id) {
        Carousel carousel = carouselService.getById(id);
        if (carousel != null) {
            String url = carousel.getPic();
            if(StringUtils.isEmpty(url)) {
                ossUtil.removeFile(url);
            }
        }
        return false;
    }
}
