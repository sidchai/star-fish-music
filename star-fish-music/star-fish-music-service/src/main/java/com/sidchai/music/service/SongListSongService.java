package com.sidchai.music.service;

import com.sidchai.music.pojo.SongListSong;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.result.R;

/**
 * <p>
 * 歌曲、歌单中间表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface SongListSongService extends IService<SongListSong> {

    /**
     *  根据歌单id查询歌单中歌曲信息
     * @return
     */
    R getSongListSongInfo(Long id);
}
