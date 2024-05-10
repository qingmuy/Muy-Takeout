package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;
    @Resource
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO 用户的登录信息
     * @return token
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return 是否成功
     */
    @PostMapping("/logout")
    @ApiOperation("登出账号")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     *
     * @param request 请求的信息
     * @param employeeDTO 员工的信息
     * @return 是否成功
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result<Boolean> save(HttpServletRequest request, @RequestBody EmployeeDTO employeeDTO) {
        Employee loginUser = employeeService.getLoginUser(request);
        employeeService.save(loginUser, employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询返回
     *
     * @param queryDTO 分页查询的条件
     * @return 分页查询信息
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO queryDTO) {
        return Result.success(employeeService.pagequery(queryDTO));
    }

    /**
     * 改变账号状态
     *
     * @param status 状态
     * @param id 账号的id
     * @return 是否修改成功
     */
    @PostMapping("status/{status}")
    @ApiOperation("改变账号状态")
    public Result<Boolean> changeStatus(@PathVariable Integer status, Long id) {
        log.info("Controller ID = " + id);
        employeeService.changeStatus(status, id);
        return Result.success();
    }

    /**
     * 根据ID查询员工信息详情
     * @param id 要查询的员工的id
     * @return 该员工的信息
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询员工信息详情")
    public Result<Employee> quertById(@PathVariable Long id) {
        Employee employee = employeeService.queryById(id);
        return Result.success(employee);
    }

    /**
     * 编辑员工信息
     * @param employeeDTO 修改后员工的信息
     * @return 是否修改成功
     */
    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result<Boolean> update(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.updateEmployee(employeeDTO);
        return Result.success();
    }
}
