package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
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

    @Resource
    UserMapper userMapper;

    /**
     * 统计指定时间区间内的营业额数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 统计结果
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        // 日期列表
        List<LocalDate> dateList = new ArrayList<>();

        // 收入列表
        List<Double> turnoverList = new ArrayList<>();

        // 构造查询条件，查询日期在指定时间段内的订单集合
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();

        while (!begin.equals(end)) {
            // 查询当日的营业额
            qw.ge(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MIN))
                    .le(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MAX))
                    .eq(Orders::getStatus, Orders.COMPLETED);

            dateList.add(begin);

            // 获取当日的订单集合，计算总金额
            List<Orders> orders = ordersMapper.selectList(qw);
            double sum = 0.0;
            for (Orders order : orders) {
                sum += order.getAmount().doubleValue();
            }
            turnoverList.add(sum);

            // 清空查询条件
            qw.clear();
            begin = begin.plusDays(1);
        }

        // 处理最后一天
        qw.ge(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MIN))
                .le(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MAX))
                .eq(Orders::getStatus, Orders.COMPLETED);
        dateList.add(end);
        List<Orders> orders = ordersMapper.selectList(qw);
        double sum = 0.0;
        for (Orders order : orders) {
            sum += order.getAmount().doubleValue();
        }
        turnoverList.add(sum);

        return new TurnoverReportVO(StringUtils.join(dateList, ","), StringUtils.join(turnoverList, ","));
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        // 日期列表
        ArrayList<LocalDate> dateList = new ArrayList<>();

        // 当日新增用户数量
        ArrayList<Long> newUserList = new ArrayList<>();

        // 当日用户总量
        ArrayList<Long> totalUserList = new ArrayList<>();

        // 构造查询条件，查询新增用户
        LambdaQueryWrapper<User> qwNew = new LambdaQueryWrapper<>();

        // 构造查询条件，查询用户总量
        LambdaQueryWrapper<User> qwTotal = new LambdaQueryWrapper<>();

        while (!begin.equals(end)) {
            // 当日新增用户查询条件
            qwNew.ge(User::getCreateTime, LocalDateTime.of(begin, LocalTime.MIN))
                    .le(User::getCreateTime, LocalDateTime.of(begin, LocalTime.MAX));
            // 当日用户总量查询条件
            qwTotal.le(User::getCreateTime, LocalDateTime.of(begin, LocalTime.MAX));

            dateList.add(begin);
            newUserList.add(userMapper.selectCount(qwNew));
            totalUserList.add(userMapper.selectCount(qwTotal));
            begin = begin.plusDays(1);

            qwNew.clear();
            qwTotal.clear();
        }

        // 处理最后一天的情况
        dateList.add(end);

        qwNew.ge(User::getCreateTime, LocalDateTime.of(begin, LocalTime.MIN))
                .le(User::getCreateTime, LocalDateTime.of(begin, LocalTime.MAX));
        qwTotal.le(User::getCreateTime, LocalDateTime.of(begin, LocalTime.MAX));

        dateList.add(begin);
        newUserList.add(userMapper.selectCount(qwNew));
        totalUserList.add(userMapper.selectCount(qwTotal));

        return new UserReportVO(StringUtils.join(dateList, ","), StringUtils.join(totalUserList, ","), StringUtils.join(newUserList, ","));
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {

        // 日期列表
        ArrayList<LocalDate> dateList = new ArrayList<>();

        // 每日订单数列表
        ArrayList<Long> orderCountList = new ArrayList<>();

        // 每日有效订单数
        ArrayList<Long> valiOrderCountList = new ArrayList<>();

        LambdaQueryWrapper<Orders> qwTotal = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Orders> qwSuccess = new LambdaQueryWrapper<>();

        // 先查询订单总数和有效订单数
        qwTotal.ge(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MIN))
                .le(Orders::getOrderTime, LocalDateTime.of(end, LocalTime.MAX));
        qwSuccess.ge(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MIN))
                .le(Orders::getOrderTime, LocalDateTime.of(end, LocalTime.MAX))
                .eq(Orders::getStatus, Orders.COMPLETED);

        Integer totalOrderCount = ordersMapper.selectCount(qwTotal).intValue();
        Integer valiOrderCount = ordersMapper.selectCount(qwSuccess).intValue();

        qwTotal.clear();
        qwSuccess.clear();

        while (!begin.equals(end)) {
            // 查询当日的订单总数
            qwTotal.ge(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MIN))
                    .le(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MAX));

            // 查询当日有效订单数
            qwSuccess.ge(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MIN))
                    .le(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MAX))
                    .eq(Orders::getStatus, Orders.COMPLETED);

            // 增添数据
            orderCountList.add(ordersMapper.selectCount(qwTotal));
            valiOrderCountList.add(ordersMapper.selectCount(qwSuccess));
            dateList.add(begin);

            // 清空查询条件
            qwTotal.clear();
            qwSuccess.clear();

            // 日期加一
            begin = begin.plusDays(1);
        }

        // 处理最后一天
        qwTotal.ge(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MIN))
                .le(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MAX));

        qwSuccess.ge(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MIN))
                .le(Orders::getOrderTime, LocalDateTime.of(begin, LocalTime.MAX))
                .eq(Orders::getStatus, Orders.COMPLETED);

        // 增添数据
        orderCountList.add(ordersMapper.selectCount(qwTotal));
        valiOrderCountList.add(ordersMapper.selectCount(qwSuccess));
        dateList.add(begin);

        return new OrderReportVO(StringUtils.join(dateList, ","),
                StringUtils.join(orderCountList, ","),
                StringUtils.join(valiOrderCountList, ","),
                totalOrderCount, valiOrderCount, valiOrderCount.doubleValue() / totalOrderCount);
    }
}
