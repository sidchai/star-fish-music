package com.sidchai.music.controller;


import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.pojo.vo.CategoryMenuVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.CategoryMenuService;
import com.sidchai.music.utils.ThrowableUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-26
 */
@Api(description = "菜单管理")
@RestController
@RequestMapping("/music/category-menu")
public class CategoryMenuController {

    @Autowired
    private CategoryMenuService categoryMenuService;

    @ApiOperation(value = "获取所有菜单列表", notes = "获取所有列表")
    @GetMapping("/get-all")
    public R getAll(@ApiParam(name = "keyword", value = "查询关键字") @RequestParam(value = "keyword", required = false) String keyword) {
        return R.ok().data(BaseConstants.MENU_LIST, categoryMenuService.getAllList(keyword));
    }

    @ApiOperation(value = "获取菜单列表", notes = "获取菜单列表")
    @PostMapping("/get-list")
    public R getList(@Validated @ApiParam(name = "categoryMenu", value = "菜单信息") @RequestBody CategoryMenuVo categoryMenuVo,
                     BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return R.ok().data(BaseConstants.MENU_PAGE_LIST, categoryMenuService.getPageList(categoryMenuVo));
    }

    @ApiOperation(value = "获取所有二级菜单-按钮列表", notes = "获取所有二级菜单-按钮列表", response = String.class)
    @GetMapping("/get-button-all")
    public R getButtonAll(@RequestParam(value = "keyword", required = false) String keyword) {
        return R.ok().data(BaseConstants.BUTTON_ALL_LIST, categoryMenuService.getButtonList(keyword));
    }

    @ApiOperation(value = "增加菜单", notes = "增加菜单")
    @PostMapping("/add-menu")
    public R addMenu(@Validated @RequestBody CategoryMenuVo categoryMenuVo, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return categoryMenuService.addCategoryMenu(categoryMenuVo);
    }

    @ApiOperation(value = "修改菜单", notes = "修改菜单")
    @PutMapping("/edit-menu")
    public R editMenu(@Validated @RequestBody CategoryMenuVo categoryMenuVo, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return categoryMenuService.editCategoryMenu(categoryMenuVo);
    }

    @ApiOperation(value = "删除菜单", notes = "删除菜单")
    @DeleteMapping("/delete-menu/{id}")
    public R deleteMenu(@PathVariable("id") Long id) {

        return categoryMenuService.deleteCategoryMenu(id);
    }

    @ApiOperation(value = "置顶菜单", notes = "指定菜单")
    @DeleteMapping("/stick-menu")
    public R deleteMenu(@Validated @RequestBody CategoryMenuVo categoryMenuVo, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return categoryMenuService.stickCategoryMenu(categoryMenuVo);
    }
}

