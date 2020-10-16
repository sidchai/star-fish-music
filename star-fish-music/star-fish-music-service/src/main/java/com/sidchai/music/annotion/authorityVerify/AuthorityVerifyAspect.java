package com.sidchai.music.annotion.authorityVerify;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sidchai.music.constants.MenuTypeConstant;
import com.sidchai.music.constants.RedisConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.pojo.Admin;
import com.sidchai.music.pojo.CategoryMenu;
import com.sidchai.music.pojo.Role;
import com.sidchai.music.result.R;
import com.sidchai.music.result.ResultCodeEnum;
import com.sidchai.music.service.AdminService;
import com.sidchai.music.service.CategoryMenuService;
import com.sidchai.music.service.RoleService;
import com.sidchai.music.utils.JsonUtils;
import com.sidchai.music.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *  权限校验 切面实现
 * @author sidchai
 * @since 2020-06-02
 */
@Aspect
@Component
@Slf4j
public class AuthorityVerifyAspect {

    @Autowired
    private CategoryMenuService categoryMenuService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisUtil redisUtil;

    @Pointcut(value = "@annotation(authorityVerify)")
    public void pointcut(AuthorityVerify authorityVerify) {

    }

    @Around(value = "pointcut(authorityVerify)")
    public Object doAround(ProceedingJoinPoint joinPoint, AuthorityVerify authorityVerify) throws Throwable {
        ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attribute.getRequest();

        //获取请求路径
        String url = request.getRequestURI();
        String adminId = request.getAttribute(SQLConstants.ID).toString();

        String visitUrl = (String) redisUtil.get(RedisConstants.ADMIN_VISIT_MENU + RedisConstants.SEGMENTATION + adminId);

        List<String> urlList = new ArrayList<>();

        if(!StringUtils.isEmpty(visitUrl)) { // 如果从redis中获得得路径不为null || ""
            // 直接从redis中取
            urlList = JsonUtils.jsonToList(visitUrl, String.class);
        } else { // 路径为空
            // 从数据库中查询
            // 得到当前访问用户信息
            Admin admin = adminService.getById(adminId);

            Long roleId = admin.getRoleId();

            Role role = roleService.getById(roleId);

            String categoryMenuIds = role.getCategoryMenuIds();

            String[] ids = categoryMenuIds.replace("[", "").replace("]", "").replace("\"", "").split(",");

            List<String> categoryMenuIdList = new ArrayList<>(Arrays.asList(ids));

            // 查询需要访问的按钮
            QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(SQLConstants.ID, categoryMenuIdList);
            queryWrapper.eq(SQLConstants.MENU_TYPE, MenuTypeConstant.BUTTON);
            queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
            List<CategoryMenu> buttonList = categoryMenuService.list(queryWrapper);

            for (CategoryMenu item : buttonList) {
                if(!StringUtils.isEmpty(item.getUrl())) {
                    urlList.add(item.getUrl());
                }
            }

            // 将访问url存储到redis中
            redisUtil.setEx(RedisConstants.ADMIN_VISIT_MENU + RedisConstants.SEGMENTATION + adminId, JsonUtils.objectToJson(urlList).toString(),1, TimeUnit.HOURS);
        }

        // 判断该角色是否能够访问该接口
        boolean flag = false;
        for (String item : urlList) {
            if(url.equals(item)) {
                flag = true;
                log.info("用户拥有操作权限，访问的路径: {}，拥有的权限接口：{}", url, item);
                break;
            }
        }
        if(!flag) {
            log.info("用户不具有操作权限，访问的路径: {}", url);
            return R.setResult(ResultCodeEnum.LOGIN_ACL);
        }

        // 执行业务
        return joinPoint.proceed();
    }
}
