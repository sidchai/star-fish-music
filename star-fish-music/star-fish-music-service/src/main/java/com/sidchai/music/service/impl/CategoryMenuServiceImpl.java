package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.*;
import com.sidchai.music.mapper.CategoryMenuMapper;
import com.sidchai.music.pojo.CategoryMenu;
import com.sidchai.music.pojo.vo.CategoryMenuVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.CategoryMenuService;
import com.sidchai.music.utils.JsonUtils;
import com.sidchai.music.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-26
 */
@Service
public class CategoryMenuServiceImpl extends ServiceImpl<CategoryMenuMapper, CategoryMenu> implements CategoryMenuService {

    @Autowired
    private CategoryMenuService categoryMenuService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<CategoryMenu> getAllList(String keyword) {

        String jsonResult = (String) redisUtil.get(RedisConstants.REDIS_MENU_LIST_ALL + RedisConstants.SEGMENTATION + keyword);

        if(!StringUtils.isEmpty(jsonResult)) { // 从redis中取出并返回
            List<CategoryMenu> list = JsonUtils.jsonToList(jsonResult, CategoryMenu.class);
            return list;
        }

        // 父wrapper
        QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.MENU_LEVEL, "1"); // 查询菜单等级为1 的信息
        if(!StringUtils.isEmpty(keyword)) {
            queryWrapper.eq(SQLConstants.ID, keyword);
        }
        queryWrapper.orderByDesc(SQLConstants.SORT); // 按排序字段进行降序排序
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE); // 查询状态为1的正常菜单
        queryWrapper.eq(SQLConstants.MENU_TYPE, MenuTypeConstant.MENU); // 查询菜单类型为菜单的，排除按钮
        List<CategoryMenu> list = categoryMenuService.list(queryWrapper); // 得到查询菜单结果

        // 获取所有的ID，去寻找它的子目录
        List<Long> ids = new ArrayList<>();
        list.forEach(item -> {
            if(!StringUtils.isEmpty(item.getId())) { // 如果id不为null || ""
                // 把id添加到链表中
                ids.add(item.getId());
            }
        });

        // 子wrapper
        QueryWrapper<CategoryMenu> childWrapper = new QueryWrapper<>();
        childWrapper.in(SQLConstants.PARENT_ID, ids); // 查询信息在ids之间
        childWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE); // 查询状态为1的正常菜单
        List<CategoryMenu> childList = categoryMenuService.list(childWrapper); // 得到查询菜单结果

        // 获取所有的二级菜单，去寻找它的子目录
        List<Long> secondMenuIds = new ArrayList<>();
        // 遍历循环取出想要的数据添加到集合中
        childList.forEach(item -> {
            if(!StringUtils.isEmpty(item.getId())) {
                secondMenuIds.add(item.getId());
            }
        });

        // 按钮wrapper
        QueryWrapper<CategoryMenu> buttonWrapper = new QueryWrapper<>();
        buttonWrapper.in(SQLConstants.PARENT_ID, secondMenuIds); // 查询信息在ids之间
        childWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE); // 查询状态为1的正常菜单
        List<CategoryMenu> buttonList = categoryMenuService.list(buttonWrapper);

        Map<String, List<CategoryMenu>> map = new HashMap<>();
        buttonList.forEach(item -> {
            if(!StringUtils.isEmpty(item.getParentId())) {
                if(map.get(item.getParentId().toString()) == null) {
                    List<CategoryMenu> tempList = new ArrayList<>();
                    tempList.add(item);
                    map.put(item.getParentId().toString(), tempList);
                } else {
                    List<CategoryMenu> tempList = map.get(item.getParentId().toString());
                    tempList.add(item);
                    map.put(item.getParentId().toString(), tempList);
                }
            }
        });

        // 给二级菜单设置三级按钮
        childList.forEach(item -> {
            if(map.get(item.getId().toString()) != null) {
                List<CategoryMenu> tempList = map.get(item.getId().toString());
                Collections.sort(tempList, new Comparator<CategoryMenu>() {

                    /*
                     * int compare(CategoryMenu p1, CategoryMenu p2) 返回一个基本类型的整型，
                     * 返回负数表示：p1 小于p2，
                     * 返回0 表示：p1和p2相等，
                     * 返回正数表示：p1大于p2
                     */
                    @Override
                    public int compare(CategoryMenu o1, CategoryMenu o2) {
                        //按照CategoryMenu的Sort进行降序排列
                        if (o1.getSort() > o2.getSort()) {
                            return -1;
                        }
                        if (o1.getSort().equals(o2.getSort())) {
                            return 0;
                        }
                        return 1;
                    }
                });
                item.setChildCategoryMenu(tempList);
            }
        });

        // 给一级菜单设置二级菜单
        for (CategoryMenu parentItem : list) {
            List<CategoryMenu> tempList = new ArrayList<>();

            for (CategoryMenu item : childList) {

                if(item.getParentId().equals(parentItem.getId())) {
                    tempList.add(item);
                }
            }
            Collections.sort(tempList);
            parentItem.setChildCategoryMenu(tempList);
        }

        // 把信息存入redis中
        redisUtil.setEx(RedisConstants.REDIS_MENU_LIST_ALL + RedisConstants.SEGMENTATION + keyword, JsonUtils.objectToJson(list), 1, TimeUnit.DAYS);

        return list;
    }

    @Override
    public Map<String, Object> getPageList(CategoryMenuVo categoryMenuVo) {
        Map<String, Object> resultMap = new HashMap<>();
        QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
        // 判断关键字是否为null || "", 不为空就添加到查询条件中
        if(!StringUtils.isEmpty(categoryMenuVo.getKeyword()) && !StringUtils.isEmpty(categoryMenuVo.getKeyword().trim())) {
            queryWrapper.like(SQLConstants.MENU_NAME, categoryMenuVo.getKeyword().trim());
        }

        if(categoryMenuVo.getMenuLevel() != 0) { // 如果菜单等级存在, 就添加到查询条件中
            queryWrapper.eq(SQLConstants.MENU_LEVEL, categoryMenuVo.getMenuLevel());
        }

        // 创建分页对象
        Page<CategoryMenu> page = new Page<>();
        page.setCurrent(categoryMenuVo.getCurrentPage());
        page.setSize(categoryMenuVo.getPageSize());
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.orderByDesc(SQLConstants.SORT);
        IPage<CategoryMenu> pageList = categoryMenuService.page(page, queryWrapper);
        List<CategoryMenu> list = pageList.getRecords();

        List<Long> ids = new ArrayList<>();
        list.forEach(item -> {
            if(!StringUtils.isEmpty(item.getParentId())) { // 如果父id不为null || "" 就添加到ids中
                ids.add(item.getParentId());
            }
        });

        if(ids.size() > 0) {
            // 查询出父级菜单列表
            Collection<CategoryMenu> parentList = categoryMenuService.listByIds(ids);
            Map<String, CategoryMenu> map = new HashMap<>();
            parentList.forEach(item -> {
                map.put(item.getId().toString(), item);
            });

            list.forEach(item -> {
                if(!StringUtils.isEmpty(item.getParentId())) {
                    item.setParentCategoryMenu(map.get(item.getParentId().toString()));
                }
            });

            resultMap.put(BaseConstants.DATA, parentList);
        }
        page.setRecords(list);
        resultMap.put(BaseConstants.DATA, pageList);

        return resultMap;
    }

    @Override
    public List<CategoryMenu> getButtonList(String keyword) {
        // 创建查询条件wrapper
        QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.MENU_LEVEL, BaseConstants.TWO.toString());
        queryWrapper.orderByDesc(SQLConstants.SORT);
        if(!StringUtils.isEmpty(keyword)) { // 关键字不为null || ""
            queryWrapper.eq(SQLConstants.ID, keyword);
        }
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE); // 状态为正常
        queryWrapper.eq(SQLConstants.MENU_TYPE, MenuTypeConstant.MENU); // 类型为菜单
        List<CategoryMenu> list = categoryMenuService.list(queryWrapper);

        // 获取所有id，寻找它的子目录
        List<Long> ids = new ArrayList<>();
        list.forEach(item -> {
            if(!StringUtils.isEmpty(item.getId())) { // 如果id存在就添加进去
                ids.add(item.getId());
            }
        });

        QueryWrapper<CategoryMenu> childWrapper = new QueryWrapper<>();
        childWrapper.in(SQLConstants.PARENT_ID, ids);
        childWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<CategoryMenu> childList = categoryMenuService.list(childWrapper);
        Set<Long> secondIdSet = new HashSet<>();
        Map<String, List<CategoryMenu>> map = new HashMap<>();
        childList.forEach(item -> {
            if(!StringUtils.isEmpty(item.getParentId())) { // 如果父id不为null或""
                secondIdSet.add(item.getParentId());
                // 从map中获取数据
                if(map.get(item.getParentId().toString()) == null) {
                    List<CategoryMenu> tempList = new ArrayList<>();
                    tempList.add(item);
                    map.put(item.getParentId().toString(), tempList);
                } else {
                    List<CategoryMenu> tempList = map.get(item.getParentId().toString());
                    tempList.add(item);
                    map.put(item.getParentId().toString(), tempList);
                }
            }
        });

        // 过滤不在button列表中的二级菜单
        List<CategoryMenu> secondCategoryMenuList = new ArrayList<>();
        for (CategoryMenu secondCategoryMenu : list) {
            for (Long id : secondIdSet) {
                if(secondCategoryMenu.getId().equals(id)) {
                    secondCategoryMenuList.add(secondCategoryMenu);
                    break;
                }
            }
        }

        // 给二级菜单设置三级按钮
        secondCategoryMenuList.forEach(item -> {
            if(map.get(item.getId().toString()) != null) {
                List<CategoryMenu> tempList = map.get(item.getId().toString());
                Collections.sort(tempList);
                item.setChildCategoryMenu(tempList);
            }
        });

        return list;
    }

    @Override
    public R addCategoryMenu(CategoryMenuVo categoryMenuVo) {
        // 如果添加的为一级菜单，就将父id清空
        if(categoryMenuVo.getMenuLevel() == 1) {
            categoryMenuVo.setParentId(null);
        }
        CategoryMenu menu = new CategoryMenu();
        menu.setParentId(categoryMenuVo.getParentId());
        menu.setSort(categoryMenuVo.getSort());
        menu.setIcon(categoryMenuVo.getIcon());
        menu.setIntroduce(categoryMenuVo.getIntroduce());
        menu.setMenuLevel(categoryMenuVo.getMenuLevel());
        menu.setMenuType(categoryMenuVo.getMenuType());
        menu.setMenuName(categoryMenuVo.getMenuName());
        menu.setUrl(categoryMenuVo.getUrl());
        menu.setIsShow(categoryMenuVo.getIsShow());
        categoryMenuService.save(menu);
        return R.ok().message(MessageConstants.INSERT_SUCCESS);
    }

    @Override
    public R editCategoryMenu(CategoryMenuVo categoryMenuVo) {
        CategoryMenu menu = categoryMenuService.getById(categoryMenuVo.getId());
        menu.setParentId(categoryMenuVo.getParentId());
        menu.setSort(categoryMenuVo.getSort());
        menu.setIcon(categoryMenuVo.getIcon());
        menu.setIntroduce(categoryMenuVo.getIntroduce());
        menu.setMenuLevel(categoryMenuVo.getMenuLevel());
        menu.setMenuType(categoryMenuVo.getMenuType());
        menu.setMenuName(categoryMenuVo.getMenuName());
        menu.setUrl(categoryMenuVo.getUrl());
        menu.setIsShow(categoryMenuVo.getIsShow());
        categoryMenuService.updateById(menu);

        // 修改成功后，需要删除redis中所有的admin访问路径
        deleteAdminVisitUrl();

        return R.ok().message(MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public R deleteCategoryMenu(Long id) {
        QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.in(SQLConstants.PARENT_ID, id);
        Integer menuCount = categoryMenuService.count(queryWrapper);

        if(menuCount > 0) {
            return R.error().message(MessageConstants.CHILDREN_MENU_UNDER_THIS_MENU);
        }
        categoryMenuService.removeById(id);

        // 删除成功后，需要删除redis中所有的admin访问路径
        deleteAdminVisitUrl();

        return R.ok().message(MessageConstants.DELETE_SUCCESS);
    }

    @Override
    public R stickCategoryMenu(CategoryMenuVo categoryMenuVo) {
        CategoryMenu categoryMenu = categoryMenuService.getById(categoryMenuVo.getId());

        // 查找最大的那个
        QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();

        // 如果是二级菜单 或者 按钮，就在当前的兄弟中，找出最大的一个
        if(categoryMenu.getMenuLevel() == 2 || categoryMenu.getMenuType() == MenuTypeConstant.BUTTON) {
            queryWrapper.eq(SQLConstants.PARENT_ID, categoryMenu.getParentId());
        }
        queryWrapper.eq(SQLConstants.MENU_LEVEL, categoryMenu.getMenuLevel());
        queryWrapper.orderByDesc(SQLConstants.SORT);
        queryWrapper.last("LIMIT 1");
        CategoryMenu maxSort = categoryMenuService.getOne(queryWrapper);

        if(StringUtils.isEmpty(maxSort.getId())) {
            return R.error().message(MessageConstants.OPERATION_FAIL);
        }

        Integer sortCount = maxSort.getSort() + 1;

        categoryMenu.setSort(sortCount);
        categoryMenuService.updateById(categoryMenu);

        return R.ok().message(MessageConstants.OPERATION_SUCCESS);
    }


    /**
     * 删除Redis中管理员的访问路径
     */
    private void deleteAdminVisitUrl() {
        Set<String> keys = redisUtil.sGet(RedisConstants.ADMIN_VISIT_MENU + "*");
        redisUtil.delete(keys);
    }
}
