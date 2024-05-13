package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {

    /**
     * 针对不同的口味插入相关的菜品对象
     * @param flavors 口味标签
     */
    void insertBatch(List<DishFlavor> flavors);
}
