package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取销量前十的商品
     * @param begin 起始时间
     * @param end 结束时间
     * @return 数据列表
     */
    List<GoodsSalesDTO> getSaleTop10(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据条件查询个数
     * @param map 查询条件
     * @return 结果
     */
    Integer countByMap(Map map);

    /**
     * 根据条件查询总和
     * @param map 查询条件
     * @return 查询结果
     */
    Double sumByMap(Map map);
}
