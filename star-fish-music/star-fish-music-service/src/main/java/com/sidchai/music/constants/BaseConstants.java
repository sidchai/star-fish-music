package com.sidchai.music.constants;

/**
 * 常量基类
 * @author sidchai
 * @since 2020-05-24
 */
public class BaseConstants {
    /**
     *  默认的hash迭代次数
     */
    public static final Integer HASH_ITERATIONS = 1024;
    public static final String ALGORITHM_NAME = "MD5"; // 加密算法名称

    /**
     *  数字常量
     */
    public static final Integer ZERO = 0;
    public static final Integer ONE = 1;
    public static final Integer TWO = 2;
    public static final Integer THREE = 3;

    // 字典类型
    public static final String REDIS_DICT_TYPE = "REDIS_DICT_TYPE";
    public final static String LIST = "list";
    public static final String DEFAULT_VALUE = "defaultValue";

    /**
     * 返回数据名
     */
    public final static String PARENT_LIST = "parentList"; // 父菜单
    public final static String SON_LIST = "sonList";// 子菜单
    public final static String BUTTON_LIST = "buttonList";// 按钮菜单
    public final static String ADMIN_PAGE_LIST = "adminPageList"; // 管理员分页信息
    public final static String ADMIN_LIST = "adminList"; // 管理员所有信息
    public final static String ADMIN_LOGIN_INFO = "adminLoginInfo"; // 当前登录管理员信息
    public final static String ADMIN_INFO = "adminInfo"; // 登录返回信息
    public final static String ROLE_LIST = "roleList"; // 角色分页信息
    public final static String RECYCLE_LIST = "recycleList"; // 回收站中所有信息
    public final static String SINGER_NAME_LIST = "singerNameList"; // 歌手名所有信息
    public final static String SINGER_INFO = "singerInfo"; // 歌手所有信息
    public final static String SINGER_PAGE_LIST = "singerPageList"; // 歌手分页信息
    public final static String SINGER_PAGE_LIST_KEYWORD = "singerPageListKeyword"; // 根据关键词查询歌手分页信息
    public final static String SONG_INFO = "songInfo"; // 所有歌曲信息
    public final static String MV_INFO = "mvInfo"; // 所有mv信息
    public final static String SONG_LIST_INFO = "songListInfo"; // 所有歌单信息
    public final static String ROLES = "roles"; // 角色列表
    public final static String MENU_LIST = "menuList"; // 所有菜单列表
    public final static String MENU_PAGE_LIST = "menuPageList"; // 菜单分页列表
    public final static String DICT_LIST = "dictList"; // 根据字典类型查询字典数据列表
    public final static String DICT_TYPE_LIST = "dictTypeList"; // 根据字典类型数组查询字典类型列表
    public final static String DICT_LIST_ALL = "dictListAll"; //  查询字典数据分页信息
    public final static String BUTTON_ALL_LIST = "buttonAllList"; // 所有二级菜单按扭列表
    public final static String DICT_TYPE_LIST_ALL = "dictTypeListAll"; // 所有字典类型信息
    public final static String CLASSIFY_SINGER_INFO = "classifySingerInfo"; // 所有歌手分类信息,包含歌手信息
    public final static String SINGER_SONG_INFO = "singerSongInfo"; // 所有歌手信息,包含歌曲信息
    public final static String SONG_LIST_SONG_INFO = "songListSongInfo"; // 歌单中所有歌曲内容
    public final static String USER_INFO = "userInfo"; // 所有用户信息
    public final static String COMMENT_INFO = "commentInfo"; // 所有评论信息
    public final static String CAROUSEL_INFO = "carouselInfo"; // 所有轮播图信息

    /**
     *  es库
     */
    public final static String ADMIN_LISTS = "admin_lists";
    public final static String USER_LISTS = "user_lists";
    public final static String SINGER_LISTS = "singer_lists";
    public final static String SONG_LISTS = "song_lists";
    public final static String SONG_LIST_LISTS = "song_list_lists";
    public final static String MV_LISTS = "mv_lists";

    /**
     *  默认管理员密码
     */
    public final static String DEFAULT_PASSWORD = "123456";
    public final static String ADMIN = "admin"; // 超级管理员用户名
    public final static String ROLE_NAME_BEST = "超级管理员"; // 超级管理员
    public final static String ROLE_NAME_COMMON = "一般管理员"; // 一般管理员
    public final static String ROLE_NAME_DEMO = "演示角色"; // 一般管理员
    public final static String TOKEN = "token"; // token令牌

    /**
     *  注册信息
     */
    public final static String REGISTER_SUBJECT = "注册信息"; // 默认邮件主题
    public final static String REGISTER_TEXT = "恭喜您注册成功，您的初始密码为123456，请尽快登录账户进行改密，以免引起不必要的麻烦"; // 默认邮件内容
    public final static String SENDER = "sidchai@qq.com"; // 默认发件人

    public final static String DATA = "data"; // 菜单分页所有信息
    public final static String OTHER_DATA = "otherData"; // parent信息

    /**
     * 异常类型
     */
    public static final String DELIMITER_TO = "@";
    public static final String DELIMITER_COLON = ":";

    /**
     *  状态信息
     */
    public static final String BANNED_SUCCESS = "解禁成功";
    public static final String BANNED_FAIL = "封禁成功";
    public static final String MUTE_FAIL = "禁言成功";
    public static final String PUT_AWAY_SUCCESS = "上架成功";
    public static final String SOLD_OUT_SUCCESS = "下架成功";
}
