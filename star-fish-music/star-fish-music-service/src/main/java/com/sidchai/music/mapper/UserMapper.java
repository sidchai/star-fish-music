package com.sidchai.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sidchai.music.pojo.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT gender, COUNT(gender) as count FROM  user GROUP BY gender")
    List<Map<String, Object>> getUserByGender();

    /**
     * 自定义根据id查询相关用户
     * @param id
     * @return
     */
    User getUserById(Long id);

    /**
     *  自定义修改用户信息
     * @param user
     * @return
     */
    boolean updateUserById(User user);
}
