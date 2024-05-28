package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
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
    public Result<OrderVO> queryOrders(@PathVariable Long id) {
        return Result.success(orderService.queryOrderDetail(id));
    }

    /**
     * 接单
     *
     * @param ordersConfirmDTO 订单id
     * @return 是否成功接单
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<String> confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("确认订单，{}", ordersConfirmDTO.getId());
        orderService.confirm(ordersConfirmDTO.getId());
        return Result.success();
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO 拒单信息
     * @return 已拒单
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<String> reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        orderService.reject(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO 订单信息
     * @return 取消成功
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result<String> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        orderService.cancal(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     *
     * @param id 订单ID
     * @return 结果
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<String> deliver(@PathVariable Long id) {
        orderService.deliver(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id 订单id
     * @return 完成
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<String> complete(@PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }
}
