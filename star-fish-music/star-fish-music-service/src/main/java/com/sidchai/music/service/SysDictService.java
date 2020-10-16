package com.sidchai.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.SysDict;
import com.sidchai.music.pojo.vo.SysDictVo;
import com.sidchai.music.result.R;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-28
 */
public interface SysDictService extends IService<SysDict> {

    /**
     *  根据字典类型查询字典信息
     */
    List<SysDict> getListByDictType(String dictType);
    /**
     *  根据字典类型数组获取字典数据
     */
    Map<String, Map<String, Object>> getListByDictTypeList(List<String> dictTypeList);

    /**
     *  获取字典数据分页信息
     * @param sysDictVo
     * @return
     */
    IPage<SysDict> getPageList(SysDictVo sysDictVo);

    /**
     *  添加字典数据信息
     * @param sysDictVo
     * @return
     */
    R addSysDict(SysDictVo sysDictVo);

    /**
     *  修改字典数据信息
     * @param sysDictVo
     * @return
     */
    R editSysDict(SysDictVo sysDictVo);

    /**
     *  删除字典数据信息
     * @param id
     * @return
     */
    R deleteSysDict(Long id);

    /**
     *  批量删除字典数据信息
     * @param ids
     * @return
     */
    R deleteBatchSysDict(List<Long> ids);
}
