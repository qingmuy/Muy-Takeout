package com.qingmuy.controller.user;


import com.qingmuy.service.OrderService;
import com.qingmuy.dto.OrdersPaymentDTO;
import com.qingmuy.dto.OrdersSubmitDTO;
import com.qingmuy.entity.Orders;
import com.qingmuy.result.PageResult;
import com.qingmuy.result.Result;
import com.qingmuy.vo.OrderPaymentVO;
import com.qingmuy.vo.OrderSubmitVO;
import com.qingmuy.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController(value = "userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api("C端-订单相关接口")
public class OrderController {

    @Resource
    OrderService orderService;

    /**
     * 提交订单
     *
     * @param ordersSubmitDTO 订单信息
     * @return 订单信息
     */
    @PostMapping("/submit")
    @ApiOperation("提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户提交订单，信息为：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO 订单数据
     * @return 支付信息
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        return Result.success(orderPaymentVO);
    }

    /**
     * 查询历史订单
     *
     * @param page     当前页
     * @param pageSize 页容量
     * @param status   状态
     * @return 页结果
     */
    @GetMapping("/historyOrders")
    @ApiOperation("查询历史订单")
    public Result<PageResult> queryHistoryOrders(Integer page, Integer pageSize, Integer status) {
        return Result.success(orderService.queryHistoryOrders(page, pageSize, status));
    }

    /**
     * 查询订单详情
     *
     * @param id 订单id
     * @return 订单详情
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> queryOrders(@PathVariable Long id) {
        return Result.success(orderService.queryOrderDetail(id));
    }

    /**
     * 取消订单
     *
     * @param id 订单id
     * @return 取消成功
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result<String> cancel(@PathVariable Long id) {
        Orders order = orderService.getById(id);
        order.setStatus(Orders.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason("用户主动取消订单");
        orderService.updateById(order);
        return Result.success();
    }

    /**
     * 再来一单
     *
     * @param id 订单id
     * @return OK
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result<String> again(@PathVariable Long id) {
        orderService.again(id);
        return Result.success();
    }

    /**
     * 催单
     * @param id 订单id
     * @return 催单成功
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("催单")
    public Result<String> reminder(@PathVariable Long id) {
        orderService.reminder(id);
        return Result.success();
    }
}