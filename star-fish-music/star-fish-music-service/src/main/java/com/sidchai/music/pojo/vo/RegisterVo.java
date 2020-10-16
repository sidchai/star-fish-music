package com.sidchai.music.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sidchai
 * @since 2020-06-09
 */
@Data
public class RegisterVo implements Serializable {
    private String username;
    private String phone;
    private String password;
    private String code;
}
