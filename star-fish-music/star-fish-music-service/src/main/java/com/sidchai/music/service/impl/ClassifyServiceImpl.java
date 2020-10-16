package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.mapper.ClassifyMapper;
import com.sidchai.music.pojo.Classify;
import com.sidchai.music.service.ClassifyService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌手类型表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class ClassifyServiceImpl extends ServiceImpl<ClassifyMapper, Classify> implements ClassifyService {

}
