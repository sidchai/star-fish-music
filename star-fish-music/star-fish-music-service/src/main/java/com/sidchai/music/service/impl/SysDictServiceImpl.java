package com.sidchai.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.*;
import com.sidchai.music.mapper.SysDictMapper;
import com.sidchai.music.pojo.SysDict;
import com.sidchai.music.pojo.SysDictType;
import com.sidchai.music.pojo.vo.SysDictVo;
import com.sidchai.music.result.R;
import com.sidchai.music.service.SysDictService;
import com.sidchai.music.service.SysDictTypeService;
import com.sidchai.music.utils.JsonUtils;
import com.sidchai.music.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.sidchai.music.constants.BaseConstants.REDIS_DICT_TYPE;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-28
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    @Autowired
    private SysDictService sysDictService;

    @Autowired
    private SysDictTypeService sysDictTypeService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<SysDict> getListByDictType(String dictType) {
        // 从Redis中获取内容
        String jsonResult = (String) redisUtil.get(REDIS_DICT_TYPE + RedisConstants.SEGMENTATION + dictType);
        // 判断redis中是否有字典
        if (!StringUtils.isEmpty(jsonResult)) {
            ArrayList<SysDict> sysDicts = JsonUtils.jsonArrayToArrayList(jsonResult);
            return sysDicts;
        }
        QueryWrapper<SysDictType> wrapper = new QueryWrapper<>();
        wrapper.eq(SQLConstants.DICT_TYPE, dictType);
        wrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        wrapper.last("LIMIT 1");
        SysDictType sysDictType = sysDictTypeService.getOne(wrapper);
        if (sysDictType == null) {
            return null;
        }
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.eq(SQLConstants.DICT_TYPE_ID, sysDictType.getId());
        queryWrapper.orderByDesc(SQLConstants.SORT);
        List<SysDict> lists = sysDictService.list(queryWrapper);

        for (SysDict list : lists) {
            // 获取默认值
            if (list.getDictValue() == BaseConstants.ONE.toString()) {
                list.setDictValue(list.getDictValue());
            }
        }

        redisUtil.setEx(REDIS_DICT_TYPE + RedisConstants.SEGMENTATION + dictType, JsonUtils.objectToJson(lists).toString(), 1, TimeUnit.DAYS);

        return lists;
    }

    @Override
    public Map<String, Map<String, Object>> getListByDictTypeList(List<String> dictTypeList) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        List<String> tempTypeList = new ArrayList<>();
        dictTypeList.forEach(item -> {
            //从Redis中获取内容
            String jsonResult = (String) redisUtil.get(REDIS_DICT_TYPE + RedisConstants.SEGMENTATION + item);

            //判断redis中是否有字典
            if (!StringUtils.isEmpty(jsonResult)) {

                Map<String, Object> tempMap = JsonUtils.jsonToMap(jsonResult);
                map.put(item, tempMap);

            } else {
                // 如果redis中没有该字典，那么从数据库中查询
                tempTypeList.add(item);
            }
        });

        // 表示数据全部从redis中获取到了，直接返回即可
        if (tempTypeList.size() <= 0) {
            return map;
        }

        //在 tempTypeList中查询 dict_type
        QueryWrapper<SysDictType> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(SQLConstants.DICT_TYPE, tempTypeList);
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        List<SysDictType> sysDictTypeList = sysDictTypeService.list(queryWrapper);
        sysDictTypeList.forEach(item -> {
            QueryWrapper<SysDict> sysDictQueryWrapper = new QueryWrapper<>();
            sysDictQueryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
            sysDictQueryWrapper.eq(SQLConstants.DICT_TYPE_ID, item.getId());
            sysDictQueryWrapper.orderByDesc(SQLConstants.SORT, SQLConstants.CREATE_TIME);
            List<SysDict> sysDictList = sysDictService.list(sysDictQueryWrapper);

            String defaultValue = null;
            for (SysDict sysDict : sysDictList) {
                //获取值
                if (sysDict.getIsDefault() == BaseConstants.ONE) {
                    defaultValue = sysDict.getDictValue();
                    break;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put(BaseConstants.DEFAULT_VALUE, defaultValue);
            result.put(BaseConstants.LIST, sysDictList);

            map.put(item.getDictType(), result);

            redisUtil.setEx(REDIS_DICT_TYPE + RedisConstants.SEGMENTATION + item.getDictType(),JsonUtils.objectToJson(result).toString(), 1, TimeUnit.DAYS);
        });
        return map;
    }

    @Override
    public IPage<SysDict> getPageList(SysDictVo sysDictVo) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        // 判断字典类型id是否存在
        if(!StringUtils.isEmpty(sysDictVo.getDictTypeId())) {
            queryWrapper.eq(SQLConstants.DICT_TYPE_ID, sysDictVo.getDictTypeId());
        }
        // 判断字典标签是否存在
        if(!StringUtils.isEmpty(sysDictVo.getDictLabel()) && !StringUtils.isEmpty(sysDictVo.getDictLabel().trim())) {
            queryWrapper.like(SQLConstants.DICT_LABEL, sysDictVo.getDictLabel().trim());
        }
        // 创建分页对象
        Page<SysDict> page = new Page<>();
        // 设置属性值
        page.setCurrent(sysDictVo.getCurrentPage());
        page.setSize(sysDictVo.getPageSize());
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.orderByDesc(SQLConstants.SORT);
        // 得到分页数据
        IPage<SysDict> pageList = sysDictService.page(page, queryWrapper);
        List<SysDict> sysDictList = pageList.getRecords();
        // 创建字典类型id set
        Set<Long> dictTypeIdList = new HashSet<>();
        // 遍历字典数据信息,把字典类型id 添加到 set 中
        sysDictList.forEach(item -> {
            dictTypeIdList.add(item.getDictTypeId());
        });
        Collection<SysDictType> dictTypeList = new ArrayList<>();
        if(dictTypeIdList.size() > 0) {
            dictTypeList = sysDictTypeService.listByIds(dictTypeIdList);
        }
        Map<String, SysDictType> dictTypeMap = new HashMap<>();
        dictTypeList.forEach(item -> {
            dictTypeMap.put(item.getId().toString(), item);
        });

        sysDictList.forEach(item -> {
            item.setSysDictType(dictTypeMap.get(item.getDictTypeId().toString()));
        });
        pageList.setRecords(sysDictList);
        return pageList;
    }

    @Override
    public R addSysDict(SysDictVo sysDictVo) {

        // 判断字典数据是否存在
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.DICT_LABEL, sysDictVo.getDictLabel());
        queryWrapper.eq(SQLConstants.DICT_TYPE_ID, sysDictVo.getDictTypeId());
        queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
        queryWrapper.last("LIMIT 1");
        SysDict item = sysDictService.getOne(queryWrapper);
        if (item != null) {
            return R.error().message(MessageConstants.ENTITY_EXIST);
        }
        SysDict sysDict = new SysDict();
        sysDict.setDictLabel(sysDictVo.getDictLabel());
        sysDict.setDictTypeId(sysDictVo.getDictTypeId());
        sysDict.setDictValue(sysDictVo.getDictValue());
        sysDict.setIsDefault(sysDictVo.getIsDefault());
        sysDict.setRemake(sysDictVo.getRemake());
        sysDict.setListClass(sysDictVo.getListClass());
        sysDict.setSort(sysDictVo.getSort());
        sysDictService.save(sysDict);
        return R.ok().message(MessageConstants.INSERT_SUCCESS);
    }

    @Override
    public R editSysDict(SysDictVo sysDictVo) {
        SysDict sysDict = sysDictService.getById(sysDictVo.getId());
        // 判断字典数据是否存在
        if(!sysDict.getDictLabel().equals(sysDictVo.getDictLabel())) {
            QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConstants.DICT_LABEL, sysDictVo.getDictLabel());
            queryWrapper.eq(SQLConstants.DICT_TYPE_ID, sysDictVo.getDictTypeId());
            queryWrapper.eq(SQLConstants.STATUS, StatusConstants.ENABLE);
            queryWrapper.last("LIMIT 1");
            SysDict item = sysDictService.getOne(queryWrapper);
            if (item != null) {
                return R.error().message(MessageConstants.ENTITY_EXIST);
            }
        }
        sysDict.setDictLabel(sysDictVo.getDictLabel());
        sysDict.setDictTypeId(sysDictVo.getDictTypeId());
        sysDict.setDictValue(sysDictVo.getDictValue());
        sysDict.setIsDefault(sysDictVo.getIsDefault());
        sysDict.setRemake(sysDictVo.getRemake());
        sysDict.setListClass(sysDictVo.getListClass());
        sysDict.setSort(sysDictVo.getSort());
        sysDictService.updateById(sysDict);

        // 获取Redis中特定前缀
        Set<String> keys = redisUtil.sGet(REDIS_DICT_TYPE + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);

        return R.ok().message(MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public R deleteSysDict(Long id) {

        if(StringUtils.isEmpty(id)) {
            R.ok().message(MessageConstants.ID_IS_NULL);
        }
        boolean flag = sysDictService.removeById(id);

        if(flag) {
            return R.ok().message(MessageConstants.DELETE_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_FAIL);
        }
    }

    @Override
    public R deleteBatchSysDict(List<Long> ids) {

        if(StringUtils.isEmpty(ids)) {
            R.ok().message(MessageConstants.ID_IS_NULL);
        }

        boolean flag = sysDictService.removeByIds(ids);

        // 获取Redis中特定前缀
        Set<String> keys = redisUtil.sGet(REDIS_DICT_TYPE + RedisConstants.SEGMENTATION + "*");
        redisUtil.delete(keys);

        if(flag) {
            return R.ok().message(MessageConstants.DELETE_BATCH_SUCCESS);
        } else {
            return R.error().message(MessageConstants.DELETE_BATCH_FAIL);
        }
    }
}
