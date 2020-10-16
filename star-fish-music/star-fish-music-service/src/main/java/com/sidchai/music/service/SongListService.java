package com.sidchai.music.service;

import com.sidchai.music.pojo.SongList;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.vo.SongListVo;
import com.sidchai.music.pojo.vo.SongVo;
import com.sidchai.music.result.R;

import java.util.List;

/**
 * <p>
 * 歌单表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface SongListService extends IService<SongList> {

    /**
     *  添加歌单信息
     * @param songListVo
     * @return
     */
    R addSongList(SongListVo songListVo);

    /**
     *  修改歌单信息
     * @param songListVo
     * @return
     */
    R editSongList(SongListVo songListVo);

    /**
     *  根据id删除歌单信息
     * @param id
     * @return
     */
    R deleteSongList(Long id);

    /**
     *  批量删除歌单信息
     * @param ids
     * @return
     */
    R deleteBatchSongList(List<Long> ids);

    /**
     * 更改歌单的状态
     * @param songList
     * @return
     */
    R changeSongListStatus(SongList songList);

    /**
     *  删除图片文件
     * @param id
     */
    Boolean removeAvatarById(Long id);

    /**
     *  添加或修改歌单图片
     * @param songList
     * @return
     */
    R addOrChangeSongListAvatar(SongList songList);

    /**
     *  根据歌单id查询歌曲信息
     * @param id
     */
    List<SongVo> getSongByListId(Long id);
}
