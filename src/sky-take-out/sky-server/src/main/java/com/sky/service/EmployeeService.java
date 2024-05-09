package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    Employee getLoginUser(HttpServletRequest request);

    /**
     * 创建员工
     * @param loginUser
     * @param employeeDTO
     */
    void save(Employee loginUser, EmployeeDTO employeeDTO);

    PageResult pagequery(EmployeePageQueryDTO queryDTO);
}
