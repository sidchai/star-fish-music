package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.mapper.SystemConfigMapper;
import com.sidchai.music.pojo.SystemConfig;
import com.sidchai.music.service.SystemConfigService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 网站系统配置 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-26
 */
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

}
