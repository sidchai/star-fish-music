package com.sidchai.music.controller;

import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.MenuTypeConstant;
import com.sidchai.music.constants.RedisConstants;
import com.sidchai.music.exception.MusicException;
import com.sidchai.music.pojo.Admin;
import com.sidchai.music.pojo.CategoryMenu;
import com.sidchai.music.pojo.Role;
import com.sidchai.music.pojo.vo.LoginVo;
import com.sidchai.music.result.R;
import com.sidchai.music.result.ResultCodeEnum;
import com.sidchai.music.service.AdminService;
import com.sidchai.music.service.CategoryMenuService;
import com.sidchai.music.service.RoleService;
import com.sidchai.music.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author sidchai
 * @since 2020-05-26
 */
@Api(description = "登录管理")
@Slf4j
@RestController
@RequestMapping("/music/auth")
public class LoginController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CategoryMenuService categoryMenuService;

    @Autowired
    private RedisUtil redisUtil;


    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public R login(@ApiParam(name = "req", value = "request对象")HttpServletRequest req,
                   @ApiParam(name = "loginVo", value = "当前登录信息")@RequestBody LoginVo loginVo) {
        //得到当前登录ip
        String ip = IpUtils.getIpAddr(req);
        // 得到当前ip登录次数
        String limitCount = (String) redisUtil.get(RedisConstants.LOGIN_LIMIT + RedisConstants.SEGMENTATION + ip);
        if(!StringUtils.isEmpty(limitCount)) { // 如果次数不为null
            Integer tempLimitCount = Integer.valueOf(limitCount);
            if(tempLimitCount > 5) {
                return R.error().message("密码输错次数过多,已被锁定30分钟");
            }
        }
        String token = adminService.login(loginVo, ip);
        return R.ok().data(BaseConstants.TOKEN, token).message("登录成功");
    }

    @ApiOperation("返回管理员信息")
    @GetMapping("/info")
    public R info(@ApiParam(name = "req", value = "request对象,可以得到token") HttpServletRequest req) {

        if(!JwtUtils.checkJwtTToken(req)) {
            return R.setResult(ResultCodeEnum.TOKEN_ERROR);
        }

        try {
            // 得到token中保存的信息
            JwtInfo info = JwtUtils.getMemberIdByJwtToken(req);
            String avatar = info.getAvatar();
            if(StringUtils.isEmpty(avatar)) { // 如果头像为空，给用户一个默认显示头像
                avatar = "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif";
                info.setAvatar(avatar);
            }
            // 获取当前登录管理员所拥有角色
            List<Long> roleId = new ArrayList<>();
            Admin admin = adminService.getById(info.getId());
            roleId.add(admin.getRoleId());
            Collection<Role> roleList = roleService.listByIds(roleId);
            List<Role> roles = new ArrayList<>(roleList);
            return R.ok().data(BaseConstants.ADMIN_INFO, info).data(BaseConstants.ROLES, roleList);
        } catch (Exception e) {
            log.error("解析用户信息失败:" + e.getMessage());
            throw new MusicException(ResultCodeEnum.FETCH_USERINFO_ERROR);
        }
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public R logout() {
        return R.ok();
    }

    @ApiOperation("获取菜单列表")
    @GetMapping("/get-menu")
    public R getMenu(HttpServletRequest req) {
        // 得到token中保存的信息
        JwtInfo info = JwtUtils.getMemberIdByJwtToken(req);
        String username = info.getUsername();
        // 查出当前管理员
        Admin admin = adminService.getAdminByName(username);

        // 从redis中取出数据
        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_ADMIN_MENU_MAP + RedisConstants.SEGMENTATION + admin.getId());
        if(StringOpeUtils.isNotEmpty(jsonResult)) { // 如果jsonResult不为空，就把redis中取到的数据直接返回
            Map<String, Object> menuMap = JsonUtils.jsonToMap(jsonResult);
            return R.ok().data(menuMap);
        }

        // 得到当前管理员所拥有的角色
        Role role = roleService.getById(admin.getRoleId());

        List<String> categoryMenuIds = new ArrayList<>();
        // 得到当前角色所管辖的区域id
        String menuIds = role.getCategoryMenuIds();
        //对menuIds进行格式化
        String[] ids = menuIds.replace("[", "").replace("]", "").replace("\"", "").split(",");
        for (int i = 0; i < ids.length; i++) {
            categoryMenuIds.add(ids[i]);
        }

        // 得到菜单链表
        Collection<CategoryMenu> categoryMenuList = categoryMenuService.listByIds(categoryMenuIds);
        // 从三级分类中查询出二级分类
        List<CategoryMenu> buttonList = new ArrayList<>();
        // 二级分类链表
        Set<Long> secondMenuIdList = new HashSet<>();

        categoryMenuList.forEach(item -> {
            // 查询二级分类
            if(item.getMenuType() == MenuTypeConstant.MENU && item.getMenuLevel() == BaseConstants.TWO) {
                secondMenuIdList.add(item.getId());
            }
            // 从三级分类中，得到二级分类
            if(item.getMenuType() == MenuTypeConstant.BUTTON && !StringUtils.isEmpty(item.getParentId())) {
                // 找出二级菜单
                secondMenuIdList.add(item.getParentId());
                // 找出全部按钮
                buttonList.add(item);
            }
        });
        // 子菜单
        Collection<CategoryMenu> childCategoryMenuList = new ArrayList<>();
        // 父菜单
        Collection<CategoryMenu> parentCategoryMenuList = new ArrayList<>();
        List<Long> parentCategoryMenuIds = new ArrayList<>();

        if(secondMenuIdList.size() > 0) {
            childCategoryMenuList = categoryMenuService.listByIds(secondMenuIdList);
        }

        childCategoryMenuList.forEach(item -> {
            // 选出所有二级分类
            if(item.getMenuLevel() == BaseConstants.TWO) {
                if(!StringUtils.isEmpty(item.getParentId())) {
                    parentCategoryMenuIds.add(item.getParentId());
                }
            }
        });

        if(parentCategoryMenuIds.size() > 0) {
            parentCategoryMenuList = categoryMenuService.listByIds(parentCategoryMenuIds);
        }

        // 得到所有父菜单
        List<CategoryMenu> list = new ArrayList<>(parentCategoryMenuList);

        // 对parent进行排序
        Collections.sort(list);

        Map<String, Object> map = new HashMap<>();
        map.put(BaseConstants.PARENT_LIST, list);
        map.put(BaseConstants.SON_LIST,childCategoryMenuList);
        map.put(BaseConstants.BUTTON_LIST,buttonList);

        // 把信息存入redis中
        redisUtil.setEx(RedisConstants.REDIS_ADMIN_MENU_MAP + RedisConstants.SEGMENTATION + admin.getId(), JsonUtils.objectToJson(map), 1, TimeUnit.DAYS);

        return R.ok().data(map);
    }
}
