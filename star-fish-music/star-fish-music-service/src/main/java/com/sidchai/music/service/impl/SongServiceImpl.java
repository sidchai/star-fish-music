package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.*;
import com.sidchai.music.mapper.SongMapper;
import com.sidchai.music.pojo.Singer;
import com.sidchai.music.pojo.Song;
import com.sidchai.music.pojo.vo.SongVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SingerService;
import com.sidchai.music.service.SongService;
import com.sidchai.music.utils.OssUtil;
import com.sidchai.music.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 歌曲表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {

    @Autowired
    private SongService songService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OssUtil ossUtil;

    @Override
    public R addSong(SongVo songVo) {
        // 判断歌曲信息是否存在
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.eq(SQLConstants.SONG_NAME, songVo.getSongName());
        queryWrapper.eq(SQLConstants.SINGER_ID, songVo.getSingerId());
        Song item = songService.getOne(queryWrapper);

        if(item != null) {
            return R.error().message(MessageConstants.ENTITY_EXIST);
        }

        Song song = new Song();
        song.setSongName(songVo.getSongName());
        song.setIntroduction(songVo.getIntroduction());
        song.setLyric(songVo.getLyric());
        song.setSingerId(songVo.getSingerId());

        deleteSongInfo();
        deleteSongInfoApi();

        if(songService.save(song)) {
            // 添加成功，删除redis中歌曲信息
            deleteSongInfo();
            deleteSongInfoApi();

            return R.ok().message(MessageConstants.INSERT_SUCCESS);
        } else {
            return R.error().message(MessageConstants.INSERT_FAIL);
        }
    }

    @Override
    public R editSong(SongVo songVo) {
        Song song = songService.getById(songVo.getId());
        // 判断该信息是否已存在
        if(!song.getSongName().equals(songVo.getSongName())) {
            QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConstants.SONG_NAME, songVo.getSongName());
            queryWrapper.eq(SQLConstants.SINGER_ID, songVo.getSingerId());
            queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
            Song resultSong = songService.getOne(queryWrapper);

            if(resultSong != null) {
                return R.error().message(MessageConstants.ENTITY_EXIST);
            }
        }
        song.setPic(songVo.getPic());
        song.setSongName(songVo.getSongName());
        song.setIntroduction(songVo.getIntroduction());
        song.setLyric(songVo.getLyric());
        song.setSingerId(songVo.getSingerId());

        deleteSongInfo();
        deleteSongInfoApi();

        if(songService.updateById(song)) {
            // 修改成功，删除redis中歌曲信息
            deleteSongInfo();
            deleteSongInfoApi();

            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @Override
    public R deleteSong(Long id) {

        if(StringUtils.isEmpty(id)) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        deleteSongInfo();
        deleteSongInfoApi();

        if(songService.removeById(id)) {

            // 删除成功，删除redis中歌曲信息
            deleteSongInfo();
            deleteSongInfoApi();

            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @Override
    public R deleteBatchSong(List<Long> ids) {

        if(StringUtils.isEmpty(ids)) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        deleteSongInfo();
        deleteSongInfoApi();

        if(songService.removeByIds(ids)) {

            // 删除成功，删除redis中歌曲信息
            deleteSongInfo();
            deleteSongInfoApi();

            return R.ok().message(MessageConstants.DELETE_BATCH_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_BATCH_FAIL);
        }
    }

    @Override
    public boolean removeAvatarById(Long id) {

        // 根据id 获得歌曲专辑图片的url
        Song song = songService.getById(id);
        if(song != null) {
            String url = song.getPic();
            if(!StringUtils.isEmpty(url)) {
                ossUtil.removeFile(url);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean removeSongById(Long id) {
        // 根据id 获得歌曲文件的url
        Song song = songService.getById(id);
        if(song != null) {
            String url = song.getSongUrl();
            if(!StringUtils.isEmpty(url)) {
                ossUtil.removeFile(url);
                return true;
            }
        }
        return false;
    }

    @Override
    public R changeSongStatus(Song song) {
        if(StringUtils.isEmpty(song.getId())) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        deleteSongInfo();
        deleteSongInfoApi();

        if(songService.updateById(song)) {
            if(song.getStatus() == 0) {

                // 修改成功，删除redis中歌曲信息
                deleteSongInfo();
                deleteSongInfoApi();

                return R.ok().message(BaseConstants.SOLD_OUT_SUCCESS);
            } else {

                // 修改成功，删除redis中歌曲信息
                deleteSongInfo();
                deleteSongInfoApi();

                return R.ok().message(BaseConstants.PUT_AWAY_SUCCESS);
            }
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @Override
    public List<SongVo> getSongBySingerId(Long id) {

        Singer singer = singerService.getById(id);

        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.SINGER_ID, id);
        queryWrapper.eq(SQLConstants.STATUS,StatusConstants.ENABLE);
        List<Song> songs = songService.list(queryWrapper);

        List<SongVo> songVos = new ArrayList<>();

        songs.forEach(item  -> {
            SongVo songVo = new SongVo();
            songVo.setId(item.getId());
            songVo.setSongName(item.getSongName());
            songVo.setSingerName(singer.getSingerName());
            songVo.setIntroduction(item.getIntroduction());
            songVo.setSongUrl(item.getSongUrl());
            songVo.setPic(item.getPic());
            songVo.setLyric(item.getLyric());
            songVo.setSingerId(item.getSingerId());
            songVos.add(songVo);
        });
        return songVos;
    }


    /**
     *  删除redis中歌曲信息
     */
    private void deleteSongInfo() {
        // 添加成功，删除redis中歌曲信息
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_SONG_LIST + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);
    }

    /**
     *  删除redis中歌曲 api 信息
     */
    private void deleteSongInfoApi() {
        // 添加成功，删除redis中歌曲信息
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_FRONT_SONG_LIST + RedisConstants.SEGMENTATION + "front");
        redisUtil.delete(keys);
    }

}
