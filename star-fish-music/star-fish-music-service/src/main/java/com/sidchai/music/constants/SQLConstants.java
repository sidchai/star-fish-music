package com.sidchai.music.constants;

/**
 * SQL 字段常量
 * @author sidchai
 * @since 2020-05-27
 */
public class SQLConstants {

    /**
     * 通用常量
     */
    public static final String STATUS = "status"; // 状态
    public static final String SORT = "sort"; // 排序
    public static final String CREATE_TIME = "create_time"; // 创建时间

    /**
     *  用户表&管理员表
     */
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String NICK_NAME = "nick_name";
    public static final String AVATAR = "avatar";
    public static final String ID = "id";
    public static final String ROLE_ID = "role_id";

    /**
     *  ip相关
     */
    public static final String IP_SOURCE = "IP_SOURCE";
    public static final String BROWSER = "BROWSER";

    /**
     *  回收站表
     */
    public static final String ADMIN_ID = "admin_id";
    public static final String USER_ID = "USER_ID";
    public static final String OS = "OS";

    /**
     *  字典表
     */
    public static final String DICT_TYPE = "dict_type"; // 字典类型
    public static final String DICT_TYPE_ID = "dict_type_id"; // 字典类型id
    public static final String DICT_NAME = "dict_name"; // 字典类型名
    public static final String DICT_LABEL = "dict_label"; // 字典标签名

    /**
     *  歌手表
     */
    public static final String SINGER_NAME = "singer_name"; // 歌手名字段
    public static final String CLASSIFY_ID = "classify_id"; //歌手类型表id

    /**
     *  歌曲表
     */
    public static final String SONG_NAME = "song_name"; // 歌曲名
    public static final String SINGER_ID = "singer_id"; // 歌手id名字段

    /**
     *  歌单表
     */
    public static final String TITLE = "title"; // 歌单标题
    public static final String STYLE = "style"; // 歌单风格
    public static final String SONG_LIST_ID = "song_list_id"; // 中间表歌单id
    public static final String SONG_ID = "song_id"; // 中间表歌曲id

    /**
     *  菜单表
     */
    public static final String MENU_NAME = "menu_name";
    public static final String MENU_LEVEL = "menu_level"; // 菜单等级
    public static final String MENU_TYPE = "menu_type"; // 菜单类型
    public static final String PARENT_ID = "parent_id"; // 菜单等级

    /**
     *  角色表
     */
    public static final String ROLE_NAME = "role_name";
}
