package com.sidchai.music.controller;


import com.sidchai.music.annotion.avoidRepeatableCommit.AvoidRepeatableCommit;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.pojo.vo.SysDictTypeVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SysDictTypeService;
import com.sidchai.music.utils.ThrowableUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 字典类型表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-28
 */
@Api(description = "字典类型管理")
@RestController
@RequestMapping("/music/sys-dict-type")
public class SysDictTypeController {

    @Autowired
    private SysDictTypeService sysDictTypeService;

    @ApiOperation("获取所有字典类型信息")
    @PostMapping("/get-sys-dict-type-list")
    public R getSysDictTypeList(@Validated @ApiParam(name = "sysDictTypeVo", value = "添加的字典类型信息", required = true) @RequestBody SysDictTypeVo sysDictTypeVo,
                                BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);

        return R.ok().data(BaseConstants.DICT_TYPE_LIST_ALL, sysDictTypeService.getPageList(sysDictTypeVo));
    }

    @AvoidRepeatableCommit
    @ApiOperation("添加字典类型信息")
    @PostMapping("/add-sys-dict-type")
    public R addSysDictType(@Validated @ApiParam(name = "sysDictTypeVo", value = "添加的字典类型信息", required = true) @RequestBody SysDictTypeVo sysDictTypeVo,
                            BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictTypeService.addSysDictType(sysDictTypeVo);
    }

    @ApiOperation("修改字典类型信息")
    @PutMapping("/edit-sys-dict-type")
    public R editSysDictType(@Validated @ApiParam(name = "sysDictTypeVo", value = "修改的字典类型信息", required = true) @RequestBody SysDictTypeVo sysDictTypeVo,
                             BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictTypeService.editSysDictType(sysDictTypeVo);
    }

    @ApiOperation("删除字典类型信息")
    @DeleteMapping("/delete-sys-dict-type/{id}")
    public R deleteSysDictType (@ApiParam(name = "id", value = "要删除的id", required = true) @PathVariable("id") Long id) {

        return sysDictTypeService.deleteSysDictType(id);
    }

    @ApiOperation("批量删除字典类型信息")
    @DeleteMapping("/delete-batch-sys-dict-type")
    public R deleteBatchSysDictType (@ApiParam(name = "ids", value = "要删除的id集合", required = true) @RequestParam("ids") List<Long> ids) {

        return sysDictTypeService.deleteBatchSysDictType(ids);
    }
}

