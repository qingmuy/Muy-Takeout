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
}
