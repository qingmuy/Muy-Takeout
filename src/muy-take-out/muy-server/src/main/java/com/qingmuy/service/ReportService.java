package com.qingmuy.service;

import com.qingmuy.vo.OrderReportVO;
import com.qingmuy.vo.SalesTop10ReportVO;
import com.qingmuy.vo.TurnoverReportVO;
import com.qingmuy.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {

    /**
     * 获取营业额信息
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额统计信息
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间内的用户数量
     * @param begin 起始时间
     * @param end 结束时间
     * @return 童虎数量
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 获取订单数量统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单数量统计
     */
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计销量前十
     * @param begin 开始时间
     * @param end 结束时间
     * @return 销量数据
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    void exportBusinessData(HttpServletResponse response);
}
