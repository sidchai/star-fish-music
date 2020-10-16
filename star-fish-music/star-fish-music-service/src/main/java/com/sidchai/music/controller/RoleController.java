package com.sidchai.music.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sidchai.music.annotion.avoidRepeatableCommit.AvoidRepeatableCommit;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.pojo.Role;
import com.sidchai.music.pojo.vo.RoleVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Api(description = "角色管理")
@RestController
@RequestMapping("/music/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiOperation("获取所有角色列表分页信息")
    @GetMapping("/get-role-list/{currentPage}/{pageSize}")
    public R getRoleList(@ApiParam(name = "currentPage", value = "当前页数", required = true) @PathVariable("currentPage") Long currentPage,
                         @ApiParam(name = "pageSize", value = "每页显示数目", required = true) @PathVariable("pageSize") Long pageSize) {
        Page<Role> page = new Page<>();
        // 设置当前页数
        page.setCurrent(currentPage);
        // 设置每页显示的数目
        page.setSize(pageSize);
        IPage<Role> pageList = roleService.page(page, null);
        return R.ok().data(BaseConstants.ROLE_LIST, pageList);
    }

    @ApiOperation("根据关键字获取所有角色列表分页信息")
    @GetMapping("/get-role-list/{currentPage}/{pageSize}/{keyword}")
    public R getRoleListByKeyword(@ApiParam(name = "currentPage", value = "当前页数", required = true) @PathVariable("currentPage") Long currentPage,
                                  @ApiParam(name = "pageSize", value = "每页显示数目", required = true) @PathVariable("pageSize") Long pageSize,
                                  @ApiParam(name = "keyword", value = "搜索关键字", required = true) @PathVariable("keyword") String keyword) {

        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(keyword)) {
            queryWrapper.like(SQLConstants.ROLE_NAME, keyword).or().like(SQLConstants.ROLE_NAME, keyword.trim());
        }
        Page<Role> page = new Page<>();
        // 设置当前页数
        page.setCurrent(currentPage);
        // 设置每页显示的数目
        page.setSize(pageSize);
        IPage<Role> pageList = roleService.page(page, queryWrapper);
        return R.ok().data(BaseConstants.ROLE_LIST, pageList);
    }

    @AvoidRepeatableCommit
    @ApiOperation("新增角色信息")
    @PostMapping("/add-role")
    public R addRole(@RequestBody RoleVo roleVo) {
        return roleService.addRole(roleVo);
    }

    @ApiOperation("修改角色信息")
    @PutMapping("/edit-role")
    public R editRole(@RequestBody RoleVo roleVo) {
        return roleService.editRole(roleVo);
    }

    @ApiOperation("删除角色信息")
    @DeleteMapping("/delete-role/{id}")
    public R deleteRole(@ApiParam(name = "id", value = "要删除角色的id") @PathVariable Long id) {
        return roleService.deleteRole(id);
    }
}

