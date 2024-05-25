package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderSubmitVO;

public interface OrdersService extends IService<Orders> {
    /**
     * 提交订单
     * @param ordersSubmitDTO 订单信息
     * @return 订单信息
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);
}
