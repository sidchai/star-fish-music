package com.sidchai.music.service;

import com.sidchai.music.pojo.MySong;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.vo.SongVo;
import com.sidchai.music.result.R;

import java.util.List;

/**
 * <p>
 * 我的音乐表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface MySongService extends IService<MySong> {
    /**
     *  根据用户id获取用户收藏信息
     * @param id
     * @return
     */
    List<SongVo> getMySongById(Long id);

    R addCollection(MySong mySong);
}
