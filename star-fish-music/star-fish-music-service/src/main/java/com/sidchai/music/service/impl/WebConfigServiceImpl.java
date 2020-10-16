package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.mapper.WebConfigMapper;
import com.sidchai.music.pojo.WebConfig;
import com.sidchai.music.service.WebConfigService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 网站基础配置 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-26
 */
@Service
public class WebConfigServiceImpl extends ServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService {

}
