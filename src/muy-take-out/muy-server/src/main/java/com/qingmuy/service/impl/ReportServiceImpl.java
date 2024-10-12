package com.qingmuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qingmuy.dto.GoodsSalesDTO;
import com.qingmuy.entity.Orders;
import com.qingmuy.entity.User;
import com.qingmuy.mapper.OrdersMapper;
import com.qingmuy.mapper.UserMapper;
import com.qingmuy.service.ReportService;
import com.qingmuy.service.WorkspaceService;
import com.qingmuy.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Resource
    OrdersMapper ordersMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    WorkspaceService workspaceService;

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

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> saleTop10 = ordersMapper.getSaleTop10(beginTime, endTime);
        List<String> name = saleTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(name, ",");

        List<Integer> numbers= saleTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        return new SalesTop10ReportVO(nameList, numberList);
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // 1. 查询数据库，获取营业数据---查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        // 查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        // 2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            // 基于模板文件创建一个心得Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            XSSFSheet sheet = excel.getSheet("Sheet1");

            // 填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间" + dateBegin + "至" + dateEnd);

            // 获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            // 获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            // 填充明细数据
            for (int i = 0; i < 30; i++) {
                dateBegin = dateBegin.plusDays(1);

                // 查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateBegin, LocalTime.MAX));

                // 获取具体行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(dateBegin.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            // 3. 通过输入流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            // 关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
