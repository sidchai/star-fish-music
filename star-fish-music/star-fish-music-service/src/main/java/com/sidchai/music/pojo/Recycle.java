package com.sidchai.music.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 回收站表
 * </p>
 *
 * @author sidchai
 * @since 2020-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Recycle对象", description="回收站表")
public class Recycle implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "被删除或封禁的用户id")
    private Long userId;

    @ApiModelProperty(value = "被删除或封禁的管理员id")
    private Long adminId;

    @ApiModelProperty(value = "原因")
    private String cause;

    @ApiModelProperty(value = "状态(0: 封禁，1: 删除)")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     *  以下字段不存入数据库
     */

    /**
     *  回收站中保存的管理员信息
     */
    @TableField(exist = false)
    private Admin admin;

    /**
     *  回收站中保存的用户信息
     */
    @TableField(exist = false)
    private User user;

}
