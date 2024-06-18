package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

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
    UserReportVO userStatistics(LocalDate begin, LocalDate end);
}
