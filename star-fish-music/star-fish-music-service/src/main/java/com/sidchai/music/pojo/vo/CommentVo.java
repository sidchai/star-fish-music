package com.sidchai.music.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author sidchai
 * @since 2020-06-06
 */
@Data
public class CommentVo implements Serializable {

    private Long id;
    private Long userId;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    private String nickName;
    private String avatar;
    private Integer up;
    private String type;
}
