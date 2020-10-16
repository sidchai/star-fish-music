package com.sidchai.music.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sidchai
 * @since 2020-4-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtInfo {
    private String id;
    private String username;
    private String avatar;
    //权限、角色等
    //不要存敏感信息
}
