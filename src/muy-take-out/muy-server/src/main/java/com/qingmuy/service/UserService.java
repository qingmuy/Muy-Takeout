package com.qingmuy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingmuy.dto.UserLoginDTO;
import com.qingmuy.entity.User;

public interface UserService extends IService<User> {

    /**
     * 微信用户登录
     * @param userLoginDTO 微信用户信息
     * @return 用户信息
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
