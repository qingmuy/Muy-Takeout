package com.sky.controller.user;


import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController(value = "userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api("C端-订单相关接口")
public class OrderController {

    @Resource
    OrdersService ordersService;

    /**
     * 提交订单
     * @param ordersSubmitDTO 订单信息
     * @return 订单信息
     */
    @PostMapping("/submit")
    @ApiOperation("提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户提交订单，信息为：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = ordersService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }
}