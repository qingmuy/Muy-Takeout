package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.ArrayList;

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

    /**
     * 批量删除套餐
     * @param lst 套餐列表
     */
    void deleteByIds(ArrayList<Integer> lst);

    /**
     * 根据id查询套餐详情
     * @param id 套餐的id
     * @return 套餐信息
     */
    SetmealVO queryById(Long id);

    /**
     * 修改套餐数据
     * @param setmealDTO 套餐的数据
     */
    void updateDate(SetmealDTO setmealDTO);

    /**
     * 修改套餐起售/停售的状态
     * @param id 套餐id
     * @param status 套餐的状态
     */
    void changeStatus(Long id, Integer status);
}
