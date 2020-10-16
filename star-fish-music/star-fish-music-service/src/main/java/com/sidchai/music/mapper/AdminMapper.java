package com.sidchai.music.mapper;

import com.sidchai.music.pojo.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 管理员表 Mapper 接口
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    // 根据id查询相关管理员
    Admin getAdminById(Long id);
    //
    Boolean updateAdmin(Admin admin);
}
