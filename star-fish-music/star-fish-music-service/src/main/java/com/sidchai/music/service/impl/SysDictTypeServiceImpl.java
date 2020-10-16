package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.MessageConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.constants.StatusConstants;
import com.sidchai.music.mapper.SysDictTypeMapper;
import com.sidchai.music.pojo.SysDictType;
import com.sidchai.music.pojo.vo.SysDictTypeVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SysDictTypeService;
import com.sidchai.music.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 字典类型表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-28
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    @Autowired
    private SysDictTypeService sysDictTypeService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public IPage<SysDictType> getPageList(SysDictTypeVo sysDictTypeVo) {

        QueryWrapper<SysDictType> queryWrapper = new QueryWrapper<>();

        // 字典名称
        if(!StringUtils.isEmpty(sysDictTypeVo.getDictName()) && !StringUtils.isEmpty(sysDictTypeVo.getDictName().trim())) {
            queryWrapper.eq(SQLConstants.DICT_NAME, sysDictTypeVo.getDictName().trim());
        }

        // 字典类型
        if(!StringUtils.isEmpty(sysDictTypeVo.getDictType()) && !StringUtils.isEmpty(sysDictTypeVo.getDictType().trim())) {
            queryWrapper.eq(SQLConstants.DICT_TYPE, sysDictTypeVo.getDictType().trim());
        }

        Page<SysDictType> page = new Page<>();
        page.setCurrent(sysDictTypeVo.getCurrentPage());
        page.setSize(sysDictTypeVo.getPageSize());
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.orderByDesc(SQLConstants.SORT);
        IPage<SysDictType> pageList = sysDictTypeService.page(page, queryWrapper);

        return pageList;
    }

    @Override
    public R addSysDictType(SysDictTypeVo sysDictTypeVo) {

        // 判断添加的字典类型是否存在
        QueryWrapper<SysDictType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.DICT_TYPE, sysDictTypeVo.getDictType());
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.last("LIMIT 1");
        SysDictType temp = sysDictTypeService.getOne(queryWrapper);
        if (temp != null) {
            return R.error().message(MessageConstants.ENTITY_EXIST);
        }
        SysDictType sysDictType = new SysDictType();
        sysDictType.setDictName(sysDictTypeVo.getDictName());
        sysDictType.setDictType(sysDictTypeVo.getDictType());
        sysDictType.setRemake(sysDictTypeVo.getRemake());
        sysDictType.setSort(sysDictTypeVo.getSort());
        sysDictTypeService.save(sysDictType);
        return R.ok().message(MessageConstants.INSERT_SUCCESS);
    }

    @Override
    public R editSysDictType(SysDictTypeVo sysDictTypeVo) {

        SysDictType sysDictType = sysDictTypeService.getById(sysDictTypeVo.getId());

        // 判断编辑的字典类型是否存在
        if(!sysDictType.getDictType().equals(sysDictTypeVo.getDictType())) {
            QueryWrapper<SysDictType> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConstants.DICT_TYPE, sysDictTypeVo.getDictType());
            queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
            queryWrapper.last("LIMIT 1");
            SysDictType temp = sysDictTypeService.getOne(queryWrapper);
            if (temp != null) {
                return R.error().message(MessageConstants.ENTITY_EXIST);
            }
        }
        sysDictType.setDictName(sysDictTypeVo.getDictName());
        sysDictType.setDictType(sysDictTypeVo.getDictType());
        sysDictType.setRemake(sysDictTypeVo.getRemake());
        sysDictType.setSort(sysDictTypeVo.getSort());
        sysDictTypeService.updateById(sysDictType);

        return R.ok().message(MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public R deleteSysDictType(Long id) {

        if(StringUtils.isEmpty(id)) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        if(sysDictTypeService.removeById(id)) {
            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @Override
    public R deleteBatchSysDictType(List<Long> ids) {

        if(StringUtils.isEmpty(ids)) {
            return R.error().message(MessageConstants.ID_IS_NULL);
        }

        if(sysDictTypeService.removeByIds(ids)) {
            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }
}
