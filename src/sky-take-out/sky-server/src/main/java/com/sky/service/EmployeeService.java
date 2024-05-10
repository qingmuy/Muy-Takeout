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
     * @param employeeLoginDTO 员工的登录信息
     * @return 员工的完整信息
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 获取当前登录用户
     * @param request 当前登陆用户的请求信息
     * @return 该员工的完整信息
     */
    Employee getLoginUser(HttpServletRequest request);

    /**
     * 创建员工
     * @param loginUser 当前的登录用户
     * @param employeeDTO 接收到的员工信息
     */
    void save(Employee loginUser, EmployeeDTO employeeDTO);

    PageResult pagequery(EmployeePageQueryDTO queryDTO);

    void changeStatus(Integer status, Long id);

    Employee queryById(Long id);

    void updateEmployee(EmployeeDTO employeeDTO);
}
