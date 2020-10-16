package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.MessageConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.mapper.MySongMapper;
import com.sidchai.music.pojo.MySong;
import com.sidchai.music.pojo.Singer;
import com.sidchai.music.pojo.Song;
import com.sidchai.music.pojo.vo.SongVo;
import com.sidchai.music.result.R;
import com.sidchai.music.result.ResultCodeEnum;
import com.sidchai.music.service.MySongService;
import com.sidchai.music.service.SingerService;
import com.sidchai.music.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 我的音乐表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class MySongServiceImpl extends ServiceImpl<MySongMapper, MySong> implements MySongService {

    @Autowired
    private MySongService mySongService;

    @Autowired
    private SongService songService;

    @Autowired
    private SingerService singerService;

    @Override
    public List<SongVo> getMySongById(Long id) {

        QueryWrapper<MySong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.USER_ID, id);
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<MySong> mySongs = mySongService.list(queryWrapper);

        List<SongVo> songVos = new ArrayList<>();

        mySongs.forEach(item -> {

            Song song = songService.getById(item.getSongId());

            Singer singer = singerService.getById(song.getSingerId());

            SongVo songVo = new SongVo();
            songVo.setId(song.getId());
            songVo.setLyric(song.getLyric());
            songVo.setPic(song.getPic());
            songVo.setSongUrl(song.getSongUrl());
            songVo.setSingerName(singer.getSingerName());
            songVo.setSongName(song.getSongName());
            songVo.setSingerId(song.getSingerId());
            songVo.setIntroduction(song.getIntroduction());

            songVos.add(songVo);
        });

        return songVos;
    }

    @Override
    public R addCollection(MySong mySong) {

        QueryWrapper<MySong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.USER_ID, mySong.getUserId());
        queryWrapper.eq(SQLConstants.SONG_ID, mySong.getSongId());
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        MySong temp = mySongService.getOne(queryWrapper);

        if (temp != null) {
            return R.setResult(ResultCodeEnum.SONG_EXIST);
        }

        if(mySongService.save(mySong)) {
            return R.ok().message(MessageConstants.SONG_SUCCESS);
        } else {
            return R.error().message(MessageConstants.SONG_FAIL);
        }
    }
}
