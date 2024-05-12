package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 员工的登录信息
     * @return 返回员工的具体信息
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对
        // 对前端传过来的明文密码进行md5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public Employee getLoginUser(HttpServletRequest request) {
        return this.getById(BaseContext.getCurrentId());
    }


    /**
     * 新增员工
     * @param loginUser 当前登录的用户
     * @param employeeDTO 新增的员工的信息
     */
    @Override
    public void save(Employee loginUser, EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        // 设置账号状态，默认为启用：1
        employee.setStatus(StatusConstant.ENABLE);

        // 设置密码，默认密码为123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        employeeMapper.inserte(employee);
    }

    /**
     * 分页查询
     * @param queryDTO 分页查询的条件
     * @return 分页查询的结果
     */
    @Override
    public PageResult pagequery(EmployeePageQueryDTO queryDTO) {
        Page<Employee> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());

        // 传入的查询条件“name”为空的情况下
        if (queryDTO.getName() == null || queryDTO.getName().isEmpty()){
            // 查询条件为空，直接查询
            Page<Employee> employeePage = employeeMapper.selectPage(page, null);
            return new PageResult(employeePage.getTotal(), employeePage.getRecords());
        }

        // 包含“name”的情况下
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.like(Employee::getName, queryDTO.getName());
        Page<Employee> employeePage = employeeMapper.selectPage(page, qw);
        return new PageResult(employeePage.getTotal(), employeePage.getRecords());
    }

    /**
     * 通过LambdaUpdateWrapper更新账号状态
     * @param status 状态
     * @param id 账号的id
     */
    @Override
    public void changeStatus(Integer status, Long id) {
        LambdaUpdateWrapper<Employee> uw = new LambdaUpdateWrapper<>();
        System.out.println("id=" + id);
        uw.eq(Employee::getId, id);
        Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);
        employeeMapper.updatee(employee);
    }

    @Override
    public Employee queryById(Long id) {
        Employee target = this.getById(id);
        target.setPassword("*****");
        return target;
    }

    @Override
    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        BeanUtils.copyProperties(employeeDTO, employee);

        employeeMapper.updatee(employee);
    }

}
