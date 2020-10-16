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
import java.util.List;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author sidchai
 * @since 2020-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CategoryMenu对象", description="菜单表")
public class CategoryMenu implements Serializable,Comparable<CategoryMenu> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "菜单名")
    private String menuName;

    @ApiModelProperty(value = "菜单级别")
    private Integer menuLevel;

    @ApiModelProperty(value = "菜单介绍")
    private String introduce;

    @ApiModelProperty(value = "父级菜单id")
    private Long parentId;

    @ApiModelProperty(value = "菜单类型（0: 菜单，1:按钮）")
    private Integer menuType;

    @ApiModelProperty(value = "菜单路由地址")
    private String url;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "排序")
    private Integer sort;

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

    @ApiModelProperty(value = "是否显示（0: 否，1: 是）")
    private Integer isShow;

    /**
     * 下列字段不存入数据库
     *
     */

    /**
     * 父菜单
     */
    @TableField(exist = false)
    private CategoryMenu parentCategoryMenu;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<CategoryMenu> childCategoryMenu;

    @Override
    public int compareTo(CategoryMenu o) {
        if(this.sort != null) {
            if (this.sort >= o.getSort()) {
                return -1;
            }
        }
        return 1;
    }
}
