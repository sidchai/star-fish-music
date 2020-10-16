package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.MessageConstants;
import com.sidchai.music.mapper.SongListSongMapper;
import com.sidchai.music.pojo.Song;
import com.sidchai.music.pojo.SongListSong;
import com.sidchai.music.pojo.vo.SongListSongVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SongListSongService;
import com.sidchai.music.service.SongService;
import com.sidchai.music.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 歌曲、歌单中间表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class SongListSongServiceImpl extends ServiceImpl<SongListSongMapper, SongListSong> implements SongListSongService {

    @Autowired
    private SongListSongService songListSongService;

    @Autowired
    private SongService songService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public R getSongListSongInfo(Long id) {

        if(StringUtils.isEmpty(id)) {
           return R.error().message(MessageConstants.ID_IS_NULL);
        }
        QueryWrapper<SongListSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("song_list_id", id);
        // 查询该歌单id对应的信息
        List<SongListSong> songListSongs = songListSongService.list(queryWrapper);

        if (songListSongs.isEmpty()) {
            R.error().message(MessageConstants.ENTITY_NONENTITY);
        }

        // 保存歌曲id的链表
        List<Long> ids = new ArrayList<>();

        songListSongs.forEach(item -> {
            if(!StringUtils.isEmpty(item.getSongId())) {
                ids.add(item.getSongId());
            }
        });

        // 根据ids 查询出歌曲信息集合
        List<Song> songs = (List<Song>) songService.listByIds(ids);


        // 创建歌曲 & 歌单 中间表 表现层对象
        SongListSongVo songListSongVo = new SongListSongVo();
        songListSongVo.setSongListId(id);
        songListSongVo.setSongs(songs);

        return R.ok().data(BaseConstants.SONG_LIST_SONG_INFO, songListSongVo);
    }
}
