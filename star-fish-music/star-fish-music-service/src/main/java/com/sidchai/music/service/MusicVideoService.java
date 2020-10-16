package com.sidchai.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.MusicVideo;
import com.sidchai.music.pojo.vo.MusicVideoVo;

import java.util.List;

/**
 * <p>
 * mv表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface MusicVideoService extends IService<MusicVideo> {
    /**
     * 获取所有mv信息
     * @return
     */
    List<MusicVideoVo> getAllMvList();
}
