package com.sidchai.music.service;

import com.sidchai.music.pojo.Song;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.vo.SongVo;
import com.sidchai.music.result.R;

import java.util.List;

/**
 * <p>
 * 歌曲表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface SongService extends IService<Song> {

    /**
     *  添加歌曲信息
     * @param songVo
     * @return
     */
    R addSong(SongVo songVo);

    /**
     *  修改歌曲信息
     * @param songVo
     * @return
     */
    R editSong(SongVo songVo);

    /**
     *  删除歌曲信息
     * @param id
     * @return
     */
    R deleteSong(Long id);

    /**
     *  批量删除歌曲信息
     * @param ids
     * @return
     */
    R deleteBatchSong(List<Long> ids);

    /**
     *  删除歌曲专辑图片
     * @param id
     * @return
     */
    boolean removeAvatarById(Long id);

    /**
     *  删除歌曲文件
     * @param id
     * @return
     */
    boolean removeSongById(Long id);

    /**
     *  改变歌曲信息状态
     * @param song
     * @return
     */
    R changeSongStatus(Song song);

    /**
     *  根据歌手id查询歌曲信息
     * @param id
     */
    List<SongVo> getSongBySingerId(Long id);
}
