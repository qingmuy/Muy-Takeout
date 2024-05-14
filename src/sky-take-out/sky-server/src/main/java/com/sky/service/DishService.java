package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService extends IService<Dish> {

    /**
     * 新增菜品和对应的口味
     * @param dishDTO 菜品的信息
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品的分页查询
     * @param dishPageQueryDTO 分页查询条件
     * @return 分页结果
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品
     * @param ids 菜品Id
     */
    void deleteByids(List<Long> ids);
}
