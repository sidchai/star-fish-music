package com.sidchai.music.controller;


import com.sidchai.music.annotion.avoidRepeatableCommit.AvoidRepeatableCommit;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.MessageConstants;
import com.sidchai.music.pojo.SysDict;
import com.sidchai.music.pojo.vo.SysDictVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SysDictService;
import com.sidchai.music.utils.ThrowableUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author sidchai
 * @since 2020-05-28
 */
@Api(description = "字典管理表")
@Slf4j
@RestController
@RequestMapping("/music/sys-dict")
public class SysDictController {

    @Autowired
    private SysDictService sysDictService;

    @ApiOperation("根据字典类型查询字典数据")
    @GetMapping("/get-list-by-dict-type/{dictType}")
    public R getListByDictType(@ApiParam(name = "dictType", value = "字典类型名") @PathVariable("dictType") String dictType) {

        // 如果为null或""，返回错误结果给前端
        if(StringUtils.isEmpty(dictType)) {
            return R.error().message(MessageConstants.OPERATION_FAIL);
        }

        List<SysDict> listByDictType = sysDictService.getListByDictType(dictType);
        if (listByDictType == null) {
            return R.error().message("该字典类型对应数据不存在");
        }

        return  R.ok().data(BaseConstants.DICT_LIST, listByDictType);
    }

    @ApiOperation("根据字典类型数组获取字典数据")
    @PostMapping("/get-list-by-dict-type-list")
    public R getListByDictTypeList(@RequestBody List<String> dictTypeList) {
        if (dictTypeList.size() <= 0) {
            return R.error().message(MessageConstants.OPERATION_FAIL);
        }
        return R.ok().data(BaseConstants.DICT_TYPE_LIST, sysDictService.getListByDictTypeList(dictTypeList));
    }

    @ApiOperation("获取字典数据分页信息")
    @PostMapping("/get-sys-dict-list")
    public R getSysDictList(@ApiParam(name = "sysDictVo", value = "接收分页信息") @RequestBody SysDictVo sysDictVo) {
        log.info("获取字典数据列表......");
        return R.ok().data(BaseConstants.DICT_LIST_ALL, sysDictService.getPageList(sysDictVo));
    }

    @AvoidRepeatableCommit
    @ApiOperation("添加字典数据信息")
    @PostMapping("/add-sys-dict")
    public R addSysDictType(@Validated @ApiParam(name = "sysDictVo", value = "添加的字典数据信息", required = true) @RequestBody SysDictVo sysDictVo,
                            BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictService.addSysDict(sysDictVo);
    }

    @ApiOperation("修改字典数据信息")
    @PutMapping("/edit-sys-dict")
    public R editSysDictType(@Validated @ApiParam(name = "sysDictVo", value = "修改的字典数据信息", required = true) @RequestBody SysDictVo sysDictVo,
                             BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictService.editSysDict(sysDictVo);
    }

    @ApiOperation("删除字典数据信息")
    @DeleteMapping("/delete-sys-dict/{id}")
    public R deleteSysDictType (@ApiParam(name = "id", value = "要删除的id", required = true) @PathVariable("id") Long id) {

        return sysDictService.deleteSysDict(id);
    }

    @ApiOperation("批量删除字典数据信息")
    @DeleteMapping("/delete-batch-sys-dict")
    public R deleteBatchSysDictType (@ApiParam(name = "ids", value = "要删除的id集合", required = true) @RequestParam("ids") List<Long> ids) {

        return sysDictService.deleteBatchSysDict(ids);
    }
}

