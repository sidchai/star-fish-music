package com.sidchai.music.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 歌单表
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SongList对象", description="歌单表")
public class SongList implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "歌单表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "歌单标题")
    private String title;

    @ApiModelProperty(value = "歌单图片url")
    private String pic;

    @ApiModelProperty(value = "歌单介绍")
    private String introduction;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "歌单类型(如华语、轻音乐、BGM)")
    private String style;

    @ApiModelProperty(value = "歌单排序(越大越靠前)")
    private Integer sort;

    @ApiModelProperty(value = "外键(指向用户表)")
    private Long userId;

    @ApiModelProperty(value = "管理员id")
    private Long adminId;

}
