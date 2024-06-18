package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据openid获取用户信息
     * @param openid 要查询的openid
     * @return 具有该openid的用户
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 创建新用户
     * @param newuser 新用户的信息
     */
    @AutoFill(OperationType.INSERT)
    void insertByAutoFill(User newuser);

    /**
     * 查询用户数量
     * @param map 查询条件
     * @return 查询结果
     */
    Integer countByMap(Map map);
}
