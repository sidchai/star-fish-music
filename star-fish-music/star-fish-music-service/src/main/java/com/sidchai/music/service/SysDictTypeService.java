package com.sidchai.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sidchai.music.pojo.SysDictType;
import com.sidchai.music.pojo.vo.SysDictTypeVo;
import com.sidchai.music.result.R;

import java.util.List;

/**
 * <p>
 * 字典类型表 服务类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-28
 */
public interface SysDictTypeService extends IService<SysDictType> {

    /**
     *  获取字典类型分页信息列表
     * @param sysDictTypeVo
     * @return
     */
    IPage<SysDictType> getPageList(SysDictTypeVo sysDictTypeVo);

    /**
     *  添加字典类型信息
     * @param sysDictTypeVo
     * @return
     */
    R addSysDictType(SysDictTypeVo sysDictTypeVo);

    /**
     *  修改字典类型信息
     * @param sysDictTypeVo
     * @return
     */
    R editSysDictType(SysDictTypeVo sysDictTypeVo);

    /**
     *  删除字典类型信息
     * @param id
     * @return
     */
    R deleteSysDictType(Long id);

    /**
     *  批量删除字典类型信息
     * @param ids
     * @return
     */
    R deleteBatchSysDictType(List<Long> ids);
}
