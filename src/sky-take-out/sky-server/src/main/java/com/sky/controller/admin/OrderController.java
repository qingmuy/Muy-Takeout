package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController(value = "adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "管理端 - 订单相关接口")
@Slf4j
public class OrderController {

    @Resource
    OrderService orderService;

    /**
     * 订单查询 分页查询
     * @param ordersPageQueryDTO 查询条件
     * @return 分页查询结果
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> search(OrdersPageQueryDTO ordersPageQueryDTO) {
        return Result.success(orderService.search(ordersPageQueryDTO));
    }
}
