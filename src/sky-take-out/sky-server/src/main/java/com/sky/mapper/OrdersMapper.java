package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
    /**
     * 根据列表添加数据
     * @param orderDetailList 订单详情列表
     */
    void insertBatch(ArrayList<OrderDetail> orderDetailList);
}
