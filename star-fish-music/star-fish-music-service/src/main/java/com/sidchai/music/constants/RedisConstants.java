package com.sidchai.music.constants;

/**
 * Redis常量基类
 * @author sidchai
 * @since 2020-05-31
 */
public class RedisConstants {

    /**
     * 请求限制
     */
    public final static String REQUEST_LIMIT = "REQUEST_LIMIT";

    /**
     * Redis分隔符
     */
    public final static String SEGMENTATION = ":";

    /**
     * 登录限制
     */
    public final static String LOGIN_LIMIT = "LOGIN_LIMIT";

    /**
     * 管理员相关信息
     */
    public final static String ADMIN_VISIT_MENU = "ADMIN_VISIT_MENU"; // 管理员访问菜单
    public final static String REDIS_ADMIN_LIST_ALL = "REDIS_ADMIN_LIST_ALL"; // 管理员访问菜单

    /**
     * 表单重复提交
     */
    public final static String AVOID_REPEATABLE_COMMIT = "AVOID_REPEATABLE_COMMIT";

    /**
     *  菜单信息
     */
    public final static String REDIS_ADMIN_MENU_MAP = "REDIS_ADMIN_MENU_MAP"; // 管理员所拥有菜单map
    public final static String REDIS_MENU_LIST_ALL = "REDIS_MENU_LIST_ALL"; // 所有菜单list

    /**
     *  字典相关信息
     */
    public final static String REDIS_DICT_TYPE_PAGE_LIST = "REDIS_DICT_TYPE_PAGE_LIST"; // 字典类型分页信息

    /**
     *  歌手相关信息
     */
    public final static String REDIS_SINGER_LIST = "REDIS_SINGER_LIST"; // 所有歌手信息,后台
    public final static String REDIS_FRONT_SINGER_LIST = "REDIS_FRONT_SINGER_LIST"; // 所有歌手信息,前台

    /**
     *  用户相关信息
     */
    public final static String REDIS_USER_LIST = "REDIS_USER_LIST"; // 所有用户信息

    /**
     *  歌单相关信息
     */
    public final static String REDIS_SONG_LIST_INFO = "REDIS_SONG_LIST_INFO"; // 所有歌单信息,后台
    public final static String REDIS_FRONT_SONG_LIST_INFO = "REDIS_FRONT_SONG_LIST_INFO"; // 所有歌单信息,前台

    /**
     *  歌曲相关信息
     */
    public final static String REDIS_SONG_LIST = "REDIS_SONG_LIST"; // 所有歌曲信息,后台
    public final static String REDIS_FRONT_SONG_LIST = "REDIS_FRONT_SONG_LIST"; // 所有歌曲信息,前台

    /**
     *  mv相关信息
     */
    public final static String REDIS_MV_LIST = "REDIS_MV_LIST"; // 所有mv信息，后台
    public final static String REDIS_FRONT_MV_LIST = "REDIS_FRONT_MV_LIST"; // 所有mv信息,前台

    /**
     *  评论相关信息
     */
    public final static String REDIS_COMMENT_LIST = "REDIS_COMMENT_LIST"; // 所有评论信息
    public final static String REDIS_FRONT_COMMENT_LIST = "REDIS_COMMENT_LIST"; // 所有评论 api 信息

    /**
     *  轮播图相关信息
     */
    public final static String REDIS_CAROUSEL_LIST = "REDIS_CAROUSEL_LIST"; // 所有轮播图信息，后台
    public final static String REDIS_FRONT_CAROUSEL_LIST = "REDIS_FRONT_CAROUSEL_LIST"; // 所有轮播图信息，前台
}
