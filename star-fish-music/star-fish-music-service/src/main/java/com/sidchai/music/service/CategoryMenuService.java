package com.sidchai.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.CategoryMenu;
import com.sidchai.music.pojo.vo.CategoryMenuVo;
import com.sidchai.music.result.R;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-26
 */
public interface CategoryMenuService extends IService<CategoryMenu> {

    /**
     * 获取全部菜单列表
     * @param keyword 查询关键字
     * @return
     */
    List<CategoryMenu> getAllList(String keyword);

    /**
     *  获取分页菜单列表
     * @param categoryMenuVo
     * @return
     */
    Map<String, Object> getPageList(CategoryMenuVo categoryMenuVo);

    /**
     *  获取所有二级菜单按钮列表
     * @param keyword
     * @return
     */
    List<CategoryMenu> getButtonList(String keyword);

    /**
     *  新增菜单
     * @param categoryMenuVo
     * @return
     */
    R addCategoryMenu(CategoryMenuVo categoryMenuVo);

    /**
     *  修改菜单
     * @param categoryMenuVo
     * @return
     */
    R editCategoryMenu(CategoryMenuVo categoryMenuVo);

    /**
     *  删除菜单
     * @param id
     * @return
     */
    R deleteCategoryMenu(Long id);

    /**
     *  置顶菜单
     * @param categoryMenuVo
     * @return
     */
    R stickCategoryMenu(CategoryMenuVo categoryMenuVo);
}
