package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService extends IService<Orders> {
    /**
     * 提交订单
     * @param ordersSubmitDTO 订单信息
     * @return 订单信息
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO 订单信息
     * @return 支付信息
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo 订单号
     */
    void paySuccess(String outTradeNo);

    /**
     * 订单查询 分页查询
     * @param ordersPageQueryDTO 查询条件
     * @return 查询结果
     */
    PageResult search(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询各个状态订单的数量
     * @return 返回类
     */
    OrderStatisticsVO countOrdersByStatus();

    /**
     * 接单
     * @param id 订单id
     */
    void confirm(Long id);

    /**
     * 拒单
     * @param ordersRejectionDTO 拒单的信息
     */
    void reject(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单的详情
     */
    OrderVO queryOrderDetail(Long id);

    /**
     * 取消订单
     * @param ordersCancelDTO 订单信息
     */
    void cancal(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id 订单id
     */
    void deliver(Long id);

    /**
     * 完成订单
     * @param id 订单id
     */
    void complete(Long id);

    /**
     * 分页查询 历史数据
     * @param page 当前页
     * @param pageSize 页大小
     * @param status 状态
     * @return 分页结果
     */
    PageResult queryHistoryOrders(Integer page, Integer pageSize, Integer status);

    /**
     * 再来一单
     * @param id 订单id
     */
    void again(Long id);

    /**
     * 催单
     * @param id 订单id
     */
    void reminder(Long id);
}
