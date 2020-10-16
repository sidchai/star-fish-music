package com.sidchai.music.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sidchai
 * @since 2020-05-31
 */
@Data
public class LoginVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String phone;
    private String password;
}
