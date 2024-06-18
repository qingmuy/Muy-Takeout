package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 获取营业额信息
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额统计信息
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
