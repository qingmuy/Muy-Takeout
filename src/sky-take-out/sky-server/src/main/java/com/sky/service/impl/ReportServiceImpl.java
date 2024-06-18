package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Resource
    OrdersMapper ordersMapper;

    /**
     * 统计指定时间区间内的营业额数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 将日期拓展为时间，数据库进行比对
        LocalDateTime begintime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endtime = LocalDateTime.of(end, LocalTime.MAX);

        // 构造查询条件，查询日期在指定时间段内的订单集合
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        qw.ge(Orders::getOrderTime, begintime)
                .le(Orders::getOrderTime, endtime)
                .eq(Orders::getStatus, Orders.COMPLETED);
        List<Orders> orders = ordersMapper.selectList(qw);

        // 收入列表
        List<Double> turnoverlist = new ArrayList<>();

        for (Orders order : orders) {
            turnoverlist.add(order.getAmount().doubleValue());
        }

        // 日期列表
        List<LocalDate> datelist = new ArrayList<>();

        while (!begin.equals(end)) {
            datelist.add(begin);
            begin = begin.plusDays(1);
        }

        datelist.add(end);

        return new TurnoverReportVO(StringUtils.join(datelist, ","), StringUtils.join(turnoverlist, ","));
    }
}
