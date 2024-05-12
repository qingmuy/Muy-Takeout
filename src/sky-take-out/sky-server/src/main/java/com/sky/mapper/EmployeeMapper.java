package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.annotation.AutoFill;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
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
    void inserte(Employee employee);

    @AutoFill(value = OperationType.UPDATE)
    void updatee(Employee employee);
}
