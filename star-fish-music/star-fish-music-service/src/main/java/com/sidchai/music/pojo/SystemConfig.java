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
 * 网站系统配置
 * </p>
 *
 * @author sidchai
 * @since 2020-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemConfig对象", description="网站系统配置")
public class SystemConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "阿里云AccessKeyId")
    private String aliAccessKey;

    @ApiModelProperty(value = "阿里云AccessKeySecret")
    private String aliSecretKey;

    @ApiModelProperty(value = "阿里云上传空间")
    private String aliBucket;

    @ApiModelProperty(value = "阿里云外网访问节点")
    private String aliEndpoint;

    @ApiModelProperty(value = "上传成功后返回的url")
    private String aliSufferUrl;

    @ApiModelProperty(value = "本地文件域名")
    private String localUrl;

    @ApiModelProperty(value = "邮箱账号")
    private String emailNumber;

    @ApiModelProperty(value = "邮箱密码")
    private String emailPassword;

    @ApiModelProperty(value = "SMTP地址")
    private String smtpHost;

    @ApiModelProperty(value = "SMTP端口")
    private String smtpPort;

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


}
