package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.mapper.SingerMapper;
import com.sidchai.music.pojo.Singer;
import com.sidchai.music.pojo.Song;
import com.sidchai.music.pojo.vo.SongVo;
import com.sidchai.music.service.SingerService;
import com.sidchai.music.service.SongService;
import com.sidchai.music.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class SingerServiceImpl extends ServiceImpl<SingerMapper, Singer> implements SingerService {

    @Autowired
    private SingerMapper singerMapper;

    @Autowired
    private SingerService singerService;

    @Autowired
    private SongService songService;

    @Autowired
    private OssUtil ossUtil;

    @Override
    public List<Map<String, Object>> getNameList(String keyword) {
        QueryWrapper<Singer> wrapper = new QueryWrapper<>();
        wrapper.select(SQLConstants.SINGER_NAME);
        wrapper.likeRight(SQLConstants.SINGER_NAME, keyword);
        List<Map<String, Object>> list = singerMapper.selectMaps(wrapper);
        return list;
    }

    @Override
    public boolean removePic(Long id) {

        Singer singer = singerService.getById(id);

        if (singer != null) {
            String url = singer.getPic();
            if(!StringUtils.isEmpty(url)) {
                ossUtil.removeFile(url);
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Singer> getSingerByClassify(Long classifyId) {

        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();

        if(classifyId == 0) {
            List<Singer> singers = singerService.list(queryWrapper);
            return singers;
        }

        queryWrapper.eq(SQLConstants.CLASSIFY_ID, classifyId);
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.orderByAsc("rank");
        List<Singer> list = singerService.list(queryWrapper);
        return list;
    }

    @Override
    public List<SongVo> getSongBySingerName(String keyword) {

        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight(SQLConstants.SINGER_NAME, keyword);
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<Singer> singers = singerService.list(queryWrapper);

        List<SongVo> songVos = new ArrayList<>();

        singers.forEach(item -> {
            QueryWrapper<Song> wrapper = new QueryWrapper<>();
            wrapper.eq(SQLConstants.SINGER_ID, item.getId());
            wrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
            List<Song> song = songService.list(wrapper);

            song.forEach(temp -> {
                SongVo songVo = new SongVo();
                songVo.setId(temp.getId());
                songVo.setLyric(temp.getLyric());
                songVo.setPic(temp.getPic());
                songVo.setSongUrl(temp.getSongUrl());
                songVo.setSingerName(item.getSingerName());
                songVo.setSongName(temp.getSongName());
                songVo.setSingerId(temp.getSingerId());
                songVo.setIntroduction(temp.getIntroduction());
                songVos.add(songVo);
            });
        });
        return songVos;
    }

}
