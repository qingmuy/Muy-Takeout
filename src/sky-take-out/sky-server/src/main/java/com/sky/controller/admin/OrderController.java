package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
     *
     * @param ordersPageQueryDTO 查询条件
     * @return 分页查询结果
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> search(OrdersPageQueryDTO ordersPageQueryDTO) {
        return Result.success(orderService.search(ordersPageQueryDTO));
    }

    /**
     * 查询各个状态订单的数量
     *
     * @return VO返回类
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态订单的数量统计")
    public Result<OrderStatisticsVO> countOrders() {
        return Result.success(orderService.countOrdersByStatus());
    }

    /**
     * 查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<Orders> queryOrders(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    /**
     * 接单
     * @param id 订单id
     * @return 是否成功接单
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<String> confirm(@RequestBody Long id) {
        orderService.confirm(id);
        return Result.success();
    }
}
