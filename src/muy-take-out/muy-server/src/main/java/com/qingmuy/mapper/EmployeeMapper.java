package com.qingmuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingmuy.annotation.AutoFill;
import com.qingmuy.entity.Employee;
import com.qingmuy.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 根据用户名查询员工
     * @param username 员工的姓名
     * @return  员工对象
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user)" +
            "values " +
            "(#{name}, #{username}, #{password}, #{phone}, #{sex}, #{id_number}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void inserte(Employee employee);

    @AutoFill(value = OperationType.UPDATE)
    void updatee(Employee employee);
}
