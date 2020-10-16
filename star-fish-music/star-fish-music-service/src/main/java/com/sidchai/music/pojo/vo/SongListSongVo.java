package com.sidchai.music.pojo.vo;

import com.sidchai.music.pojo.Song;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author sidchai
 * @since 2020-06-04
 */
@Data
public class SongListSongVo implements Serializable {

    /**
     *  歌单id
     */
    private Long songListId;

    /**
     *  歌曲表现层对象
     */
    private List<Song> songs;
}
