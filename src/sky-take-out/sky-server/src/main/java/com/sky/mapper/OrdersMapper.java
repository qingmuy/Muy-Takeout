package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
    /**
     * 根据列表添加数据
     * @param orderDetailList 订单详情列表
     */
    void insertBatch(ArrayList<OrderDetail> orderDetailList);

    /**
     * 插入订单数据
     *
     * @param order
     * @return
     */
    int insert(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);
}
