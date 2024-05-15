package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;

public interface SetMealService extends IService<Setmeal> {
    /**
     * 新增菜品
     * @param setmealDTO 菜品的详细信息
     */
    void addmeal(SetmealDTO setmealDTO);

    /**
     * 分页查询数据
     * @param queryDTO 分页查询数据
     * @return 分页查询结果
     */
    PageResult queryByPage(SetmealPageQueryDTO queryDTO);
}
