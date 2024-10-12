package com.qingmuy.controller.user;

import com.qingmuy.service.UserService;
import com.qingmuy.constant.JwtClaimsConstant;
import com.qingmuy.dto.UserLoginDTO;
import com.qingmuy.entity.User;
import com.qingmuy.properties.JwtProperties;
import com.qingmuy.result.Result;
import com.qingmuy.utils.JwtUtil;
import com.qingmuy.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Api(tags = "店铺相关接口")
@Slf4j
public class UserController {

    @Resource
    UserService userService;

    @Resource
    JwtProperties jwtProperties;

    /**
     * 微信用户登录
     * @param userLoginDTO 微信登录的用户数据
     * @return 当前用户的信息
     */
    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信用户登录：{}", userLoginDTO.getCode());

        // 微信登录
        User user = userService.wxLogin(userLoginDTO);

        // 为微信用户生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }
}
