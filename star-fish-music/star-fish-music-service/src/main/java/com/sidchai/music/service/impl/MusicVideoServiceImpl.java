package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.mapper.MusicVideoMapper;
import com.sidchai.music.pojo.MusicVideo;
import com.sidchai.music.pojo.Song;
import com.sidchai.music.pojo.vo.MusicVideoVo;
import com.sidchai.music.service.MusicVideoService;
import com.sidchai.music.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * mv表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class MusicVideoServiceImpl extends ServiceImpl<MusicVideoMapper, MusicVideo> implements MusicVideoService {

    @Autowired
    private MusicVideoService musicVideoService;

    @Autowired
    private SongService songService;

    @Override
    public List<MusicVideoVo> getAllMvList() {
        QueryWrapper<MusicVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<MusicVideo> list = musicVideoService.list(queryWrapper);

        List<MusicVideoVo> musicVideoVos = new ArrayList<>();

        QueryWrapper<Song> wrapper = new QueryWrapper<>();
        wrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        list.forEach(item -> {
            MusicVideoVo musicVideoVo = new MusicVideoVo();
            musicVideoVo.setId(item.getId());
            musicVideoVo.setSingerName(item.getSingerName());
            musicVideoVo.setSourceId(item.getSourceId());
            musicVideoVo.setTitle(item.getTitle());
            musicVideoVo.setOriginalName(item.getOriginalName());
            Song song = songService.getById(item.getSongId());
            musicVideoVo.setPic(song.getPic());
            musicVideoVos.add(musicVideoVo);
        });

        return musicVideoVos;
    }
}
