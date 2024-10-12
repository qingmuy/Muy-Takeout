package com.qingmuy.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingmuy.constant.MessageConstant;
import com.qingmuy.dto.UserLoginDTO;
import com.qingmuy.entity.User;
import com.qingmuy.exception.LoginFailedException;
import com.qingmuy.mapper.UserMapper;
import com.qingmuy.properties.WeChatProperties;
import com.qingmuy.service.UserService;
import com.qingmuy.utils.HttpClientUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Resource
    WeChatProperties weChatProperties;

    @Resource
    UserMapper userMapper;

    /**
     * 微信登录
     * @param userLoginDTO 微信用户信息
     * @return 当前用户
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 获得当前微信用户的openid
        String openid = this.getOpenid(userLoginDTO.getCode());

        // 检测openid是否为空，如果为空表示登陆失败，抛出业务异常
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);

        // 如果是新用户，自动完成注册
        if (user == null) {
            User newuser = new User();
            newuser.setOpenid(openid);
            newuser.setCreateTime(LocalDateTime.now());
            userMapper.insert(newuser);
        }

        // 返回这个用户对象
        return user;
    }

    /**
     * 根据当前微信用户的code返回其openid
     * @param code 临时登录凭证
     * @return openid
     */
    private String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);

        return jsonObject.getString("openid");
    }
}
