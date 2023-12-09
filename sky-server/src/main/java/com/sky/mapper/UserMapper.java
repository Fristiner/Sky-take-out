package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/* *
 * @packing com.sky.mapper
 * @author mtc
 * @date 10:36 11 29 10:36
 *
 */
@Mapper
public interface UserMapper {


    /**
     * 根据openId来查用户
     *
     * @param openId
     * @return
     */
    @Select("select * from user where openid = #{openId}")
    User getByOpenId(String openId);

    
    void Insert(User user);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);
}
