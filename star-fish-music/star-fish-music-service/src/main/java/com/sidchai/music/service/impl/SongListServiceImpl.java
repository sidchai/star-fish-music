package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.*;
import com.sidchai.music.mapper.SongListMapper;
import com.sidchai.music.pojo.Singer;
import com.sidchai.music.pojo.Song;
import com.sidchai.music.pojo.SongList;
import com.sidchai.music.pojo.SongListSong;
import com.sidchai.music.pojo.vo.SongListVo;
import com.sidchai.music.pojo.vo.SongVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SingerService;
import com.sidchai.music.service.SongListService;
import com.sidchai.music.service.SongListSongService;
import com.sidchai.music.service.SongService;
import com.sidchai.music.utils.OssUtil;
import com.sidchai.music.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 歌单表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements SongListService {

    @Autowired
    private SongListService songListService;

    @Autowired
    private SongListSongService songListSongService;

    @Autowired
    private SongService songService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OssUtil ossUtil;

    @Override
    public R addSongList(SongListVo songListVo) {
        // 判断歌单信息是否存在
        QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.TITLE, songListVo.getTitle());
        queryWrapper.eq(SQLConstants.STYLE, songListVo.getStyle());
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        SongList temp = songListService.getOne(queryWrapper);

        if (temp != null) {
            return R.error().message(MessageConstants.ENTITY_EXIST);
        }

        SongList songList = new SongList();
        songList.setTitle(songListVo.getTitle());
        songList.setIntroduction(songListVo.getIntroduction());
        songList.setStyle(songListVo.getStyle());
        songList.setSort(songListVo.getSort());

        if (!StringUtils.isEmpty(songListVo.getUserId())) {
            songList.setUserId(songListVo.getUserId());
        }

        if (!StringUtils.isEmpty(songListVo.getAdminId())) {
            songList.setAdminId(songListVo.getAdminId());
        }

        deleteSongListInfo();
        deleteSongListInfoApi();

        if (songListService.save(songList)) {

            // 添加成功，删除redis中信息
            deleteSongListInfo();
            deleteSongListInfoApi();

            return R.ok().message(MessageConstants.INSERT_SUCCESS);
        } else {
            return R.error().message(MessageConstants.INSERT_FAIL);
        }
    }

    @Override
    public R editSongList(SongListVo songListVo) {

        // 判断该歌曲信息是否存在
        SongList songList = songListService.getById(songListVo.getId());
        if(!songList.getTitle().equals(songListVo.getTitle())) {
            QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConstants.TITLE, songListVo.getTitle());
            queryWrapper.eq(SQLConstants.STYLE, songListVo.getStyle());
            queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
            SongList temp = songListService.getOne(queryWrapper);

            if (temp != null) {
                return R.error().message(MessageConstants.ENTITY_EXIST);
            }
        }

        songList.setTitle(songListVo.getTitle());
        songList.setIntroduction(songListVo.getIntroduction());
        songList.setStyle(songListVo.getStyle());
        songList.setSort(songListVo.getSort());

        if (!StringUtils.isEmpty(songListVo.getUserId())) {
            songList.setUserId(songListVo.getUserId());
        }

        if (!StringUtils.isEmpty(songListVo.getAdminId())) {
            songList.setAdminId(songListVo.getAdminId());
        }

        deleteSongListInfo();
        deleteSongListInfoApi();

        if(songListService.updateById(songList)) {

            // 修改成功，删除redis中信息
            deleteSongListInfo();
            deleteSongListInfoApi();

            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R deleteSongList(Long id) {

        if(StringUtils.isEmpty(id)) {
           return R.error().message(MessageConstants.ID_IS_NULL);
        }

        QueryWrapper<SongListSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.SONG_LIST_ID, id);
        songListSongService.removeById(id);

        deleteSongListInfo();
        deleteSongListInfoApi();

        // 删除歌单表中数据
        if(songListService.removeById(id)) {
            // 删除成功，删除redis中信息
            deleteSongListInfo();
            deleteSongListInfoApi();
            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @Override
    public R deleteBatchSongList(List<Long> ids) {

        if(ids.isEmpty()) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        deleteSongListInfo();
        deleteSongListInfoApi();

        if(songListService.removeByIds(ids)) {
            // 删除成功，删除redis中信息
            deleteSongListInfo();
            deleteSongListInfoApi();
            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @Override
    public R changeSongListStatus(SongList songList) {

        if(StringUtils.isEmpty(songList.getId())) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        deleteSongListInfo();
        deleteSongListInfoApi();

        if(songListService.updateById(songList)) {

            // 修改成功，删除redis中歌曲信息
            deleteSongListInfo();
            deleteSongListInfoApi();

            if(songList.getStatus() == 0) {


                return R.ok().message(BaseConstants.SOLD_OUT_SUCCESS);
            } else {

                return R.ok().message(BaseConstants.PUT_AWAY_SUCCESS);
            }

        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @Override
    public Boolean removeAvatarById(Long id) {
        SongList songList = songListService.getById(id);
        if (songList != null) {
            String url = songList.getPic();
            if (!StringUtils.isEmpty(url)) {
                ossUtil.removeFile(url);
                return true;
            }
        }
        return false;
    }

    @Override
    public R addOrChangeSongListAvatar(SongList songList) {
        // 修改图片删除原图片
        removeAvatarById(songList.getId());

        deleteSongListInfo();
        deleteSongListInfoApi();

        if (songListService.updateById(songList)) {

            // 修改成功, 删除redis中信息
            deleteSongListInfo();
            deleteSongListInfoApi();

            return R.ok().message(MessageConstants.UPDATE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.UPDATE_FAIL);
        }
    }

    @Override
    public List<SongVo> getSongByListId(Long id) {
        QueryWrapper<SongListSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.SONG_LIST_ID, id);
        List<SongListSong> list = songListSongService.list(queryWrapper);

        List<Song> songs = new ArrayList<>();

        list.forEach(item -> {
            if(item.getSongId() != null) {
                Song song = songService.getById(item.getSongId());
                songs.add(song);
            }
        });

        List<SongVo> songVos = new ArrayList<>();

        songs.forEach(item -> {
            Singer singer = singerService.getById(item.getSingerId());
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
     * 删除redis中歌单信息
     */
    private void deleteSongListInfo() {
        // 添加成功，删除redis中歌单信息
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_SONG_LIST_INFO + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);
    }

    /**
     * 删除redis中歌单 api 信息
     */
    private void deleteSongListInfoApi() {
        // 添加成功，删除redis中歌单 api 信息
        Set<String> keys = redisUtil.sGet(RedisConstants.REDIS_FRONT_SONG_LIST_INFO + RedisConstants.SEGMENTATION + "front");
        redisUtil.delete(keys);
    }
}
