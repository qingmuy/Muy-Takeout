package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
    /**
     * 根据菜品Id查询套餐的id
     * @param dishIds 菜品的ID列表
     * @return 查询结果：套餐的id列表
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 插入所有的信息
     * @param setmealDishes 菜品信息集合
     * @param id 套餐的id
     */
    void insertall(List<SetmealDish> setmealDishes, Long id);

    /**
     * 根据套餐id查询套餐中停售的菜品个数
     * @param id 套餐的id
     * @return 停售的菜品个数
     */
    Integer selectBySetMealId(Long id);
}
