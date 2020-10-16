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
 * 网站基础配置
 * </p>
 *
 * @author sidchai
 * @since 2020-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WebConfig对象", description="网站基础配置")
public class WebConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "logo(url)")
    private String logo;

    @ApiModelProperty(value = "网站名")
    private String siteName;

    @ApiModelProperty(value = "网站介绍")
    private String introduce;

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "支付宝收款码(url)")
    private String aliPay;

    @ApiModelProperty(value = "微信收款码(url)")
    private String weChatPay;

    @ApiModelProperty(value = "GitHub地址")
    private String github;

    @ApiModelProperty(value = "Gitee地址")
    private String gitee;

    @ApiModelProperty(value = "qq号")
    private String qqNumber;

    @ApiModelProperty(value = "微信号")
    private String wechatNumber;

    @ApiModelProperty(value = "邮箱")
    private String email;


}
