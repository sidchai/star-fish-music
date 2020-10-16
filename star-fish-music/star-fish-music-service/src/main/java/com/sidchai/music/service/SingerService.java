package com.sidchai.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.Singer;
import com.sidchai.music.pojo.vo.SongVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface SingerService extends IService<Singer> {

    // 根据关键字查询歌手名信息
    List<Map<String, Object>> getNameList(String keyword);

    /**
     *  根据id删除歌手头像
     * @param id
     */
    boolean removePic(Long id);

    /**
     *  根据歌手类型查询歌手信息
     * @param classifyId
     * @return
     */
    List<Singer> getSingerByClassify(Long classifyId);

    /**
     *  根据歌手名查询歌曲信息
     * @param keyword
     * @return
     */
    List<SongVo> getSongBySingerName(String keyword);
}
